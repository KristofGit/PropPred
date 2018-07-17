package Main;

import java.util.List;

import Configuration.DataSource;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import ReadData.ReadDataIntoObjects;
import StatisticalAnalysis.BPIC12_Analysis;

public class main {

	public static void main(String[] args) {
	
		ReadDataIntoObjects readBPIC12 = new ReadDataIntoObjects();
		ProcessModel processes = readBPIC12.readData(DataSource.BPIC_2012, false);
		
		BPIC12_Analysis.countDifferentActivities(processes);
		
		List<ExecutionTrace> traces = processes.getAllTraces();
		
		for(ExecutionTrace eachTrace : traces)
		{
			if(eachTrace.getTraceLength() == 6 + 2)
			{
				 List<ExecutionEvent> events = eachTrace.getAllEvents();
				
				 String eventString = "";
				 
				 for(ExecutionEvent eachEvent : events)
				 {
					 eventString += eachEvent.getActivityName();
					 eventString += ", ";
				 }
				 
				 System.out.println(eventString);
			}
		}
	}

}
