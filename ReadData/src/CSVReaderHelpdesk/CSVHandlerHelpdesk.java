package CSVReaderHelpdesk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;

public class CSVHandlerHelpdesk {

	
	public ProcessModel readFile(File file) throws Exception
	{
		boolean firstLine = true;
		
		final ProcessModel process = new ProcessModel();

		Set<String> uniqueTraceNumbers = new HashSet<>();
		
		String currentTraceName = null;
		ExecutionTrace trace = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       
		    	if(firstLine)
		    	{	//to skip the first line which holds the csv column names
		    		firstLine = false;
		    		continue;
		    	}
		    	
		    	String[] splittedDetails = line.split(",");
		    	
		    	String traceName = splittedDetails[0];
		    	String activityName = splittedDetails[1];
		    	String timeStamp = splittedDetails[2];
		    	
		    	uniqueTraceNumbers.add(traceName);
		    	
		    	if(currentTraceName == null || !Objects.equals(currentTraceName, traceName))
		    	{
		    		if(trace != null)
		    		{
		    			trace.addNewEvent(ExecutionEvent.getEndEvent());
		    		}
		    		
		    		process.addTrace(trace);

		    		//got a new trace
		    		currentTraceName = traceName;
		    		trace = new ExecutionTrace();  	
		    		
		    		trace.addNewEvent(ExecutionEvent.getStartEvent());
		    	}
		    	
		    	trace.addNewEvent(new ExecutionEvent(null, activityName, parseTime(timeStamp)));
		    }
		}
		
		return process;
	}
	
	private Instant parseTime(String timeString)
	{
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.from(f.parse(timeString)).toInstant(ZoneOffset.UTC);
	}
}
