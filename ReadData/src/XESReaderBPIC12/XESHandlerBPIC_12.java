package XESReaderBPIC12;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.management.RuntimeErrorException;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;

public class XESHandlerBPIC_12 extends DefaultHandler {
  
	/*
	 * As we want to compare ourself with the following paper
	 * Predictive Business Process Monitoring with LSTM Neural Networks
	 * 
	 * We need to filter for specific processes (i.e., sub processes) but also event types 
	 * The comparison paper only considers sub process W
	 * The comparison paper only considers event types = COMPLETED
	 * 
	 * We only take Event Types with an Event were 
	 * Lifecycle=Completed
	 * 
	 * And only events which are from the Subprocess W 
	 * For this check the concept_name, first character = subprocess name
	 */
	
	 private final ProcessModel process = new ProcessModel();
	 
	 private ExecutionTrace currentTrace = null;
	 private boolean filterDuplicateAcitivites = false;
	 public XESHandlerBPIC_12(boolean filterDuplicateAcitivites)
	 {
		 this.filterDuplicateAcitivites = filterDuplicateAcitivites;
		 currentTrace = new ExecutionTrace();
		 currentTrace.addNewEvent(ExecutionEvent.getStartEvent());
	 }
	
	 private boolean foundEvent = false;
	 private boolean shouldBeStored = false;
	 
	 private String resouce = null;
	 private Instant pointInTime = null;
	 private String activityName = null;
	 
	   @Override
	   public void startElement(
	   String uri, 
	   String localName,
	   String qName, 
	   Attributes attributes)
	   {
		   if(qName.equals("event"))
		   {
			   foundEvent = true;
			   shouldBeStored = true;
		   }
		   else if(foundEvent)
		   {
			   String key = attributes.getValue("key");
			   String value = attributes.getValue("value");
			   
			   switch(key)
			   {
			   case "org:resource":
				   resouce = value;
				   break;
			   case "lifecycle:transition":
				   if(!value.equals("COMPLETE"))
				   {
					   shouldBeStored = false; //only completed events
				   }
				   break;
			   case "concept:name":
				   if(value == null || !value.startsWith("W"))
				   {
					   shouldBeStored = false; //we only want one specific sub process
				   }
				   activityName = value;
				   break;
			   case "time:timestamp":
				   OffsetDateTime dateTime = OffsetDateTime.parse(value);
				   pointInTime = dateTime.toInstant();
				   break;
			   }
		   }
	   }
	   	   
	   @Override
	   public void endElement(String uri, 
			   String localName, String qName)
	   {
		   if(qName.equals("trace"))
		   {
			   initNextTrace();
		   } 
		   else if(qName.equals("event"))
		   {
			   if(filterDuplicateAcitivites && isLastEventDuplicate())
			   {
				   shouldBeStored = false;
			   }
			   
			   if(shouldBeStored)
			   {
				   currentTrace.addNewEvent(new ExecutionEvent(resouce, activityName, pointInTime));
			   }
			   
			   foundEvent = false;
			   shouldBeStored = false;
			   resouce = null;
			   pointInTime = null;
			   activityName = null;
		   }
	   }
	   
	   private void initNextTrace()
	   {
		   currentTrace.addNewEvent(ExecutionEvent.getEndEvent());
		   
		   if(currentTrace.getTraceLength()>2) //if we only have two an the trace only holds the start and end event
		   {
			   getProcess().addTrace(currentTrace);
		   }		   
		   
		   currentTrace = new ExecutionTrace();
		   currentTrace.addNewEvent(ExecutionEvent.getStartEvent());
		   
		   if(process.getTraceCount()>5000)
		   {
			   //throw new RuntimeException("Stop reading data");
		   }
	   }
	   
	   private boolean isLastEventDuplicate()
	   {
		   boolean result = false;
		   
		   ExecutionEvent event = currentTrace.getLastEvent();
		   
		   if(event != null)
		   {
			  String name =  event.getActivityName();

			  if(name != null && Objects.equals(name, activityName))
			  {
				  result = true;
			  }
		   }
			  
		   return result;
	}

	public ProcessModel getProcess() {
		return process;
	}
}
