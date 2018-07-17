package LogRepresentation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import CollectionsHelper.ListHelper;
import Configuration.ConfigEvaluation;

public class ProcessModel {

	private boolean areTracesOrdered = true;
	private final List<ExecutionTrace> allTraces = new ArrayList<>();

	public static ProcessModel of(ExecutionTrace... trace)
	{
		ProcessModel m = new ProcessModel();
		
		Arrays.stream(trace).forEach(x->m.addTrace(x));
		
		return m;
	}
	
	public void addTrace(ExecutionTrace trace)
	{
		if(trace != null)
		{
			allTraces.add(trace);
			areTracesOrdered = false;
		}
	}

	public List<ExecutionTrace> getAllTraces() {
		return Collections.unmodifiableList(allTraces);
	}
	
	public int getTraceCount()
	{
		return allTraces.size();
	}
	
	public ProcessModel shortenTheTraces(int length)
	{
		ProcessModel result =  new ProcessModel();
		 
		//extracts the traces with up to length elements, does not count the artifical ones
		List<ExecutionTrace> allShortenedTraces = new ArrayList<>();

		for(ExecutionTrace eachTrace : allTraces)
		{
			//+1 because we have our artifical start event
			List<ExecutionEvent> events = new ArrayList<>(eachTrace.getTraceWithEventsBeforeAndIncludingIndex(length).getAllEvents());
			
			ExecutionEvent lastEvent = ListHelper.getLast(events);
			
			if(lastEvent != null && !lastEvent.isArtificalFinalEvent())
			{
				events.add(ExecutionEvent.getEndEvent());
			}
			
			ExecutionTrace trace = new ExecutionTrace();
			events.stream().forEach(x->trace.addNewEvent(x));
			
			allShortenedTraces.add(trace);
		}
		
		result.allTraces.addAll(allShortenedTraces);
		return result;
	}
	
	public TrainingTesting separateData()
	{
		 //chronologically order the traces based on their first events
		 //take the XX first percent ones
		orderTracesChronologically();
		
		int indexSplitter = (int) (allTraces.size()*ConfigEvaluation.DataSeparataionForTraining);
		
		List<ExecutionTrace> tracesForTraining = allTraces.subList(0, indexSplitter);
		List<ExecutionTrace> tracesForTesting = allTraces.subList(indexSplitter, allTraces.size());

		
		ProcessModel testingData = new ProcessModel();
		testingData.allTraces.addAll(tracesForTesting);
		
		ProcessModel trainingData = new ProcessModel();;
		trainingData.allTraces.addAll(tracesForTraining);

		return new TrainingTesting(trainingData, testingData);
	}
	
	private void orderTracesChronologically()
	{
		if(!areTracesOrdered)
		{
		areTracesOrdered = true;
		Collections.sort(allTraces, new Comparator<ExecutionTrace>() {
		        @Override
		        public int compare(ExecutionTrace trace1, ExecutionTrace trace2)
		        {

		        	ExecutionEvent event1 = trace1.getFirstEventWithTime();
		        	ExecutionEvent event2 = trace2.getFirstEventWithTime();

		        	if(event1 != null && event2 != null)
		        	{
		        		Instant instance1 = event1.getPointInTime();
		        		Instant instance2 = event2.getPointInTime();
		        		
		        		return instance1.compareTo(instance2);
		        	}
		        	else
		        	{
		        		return 0;
		        	}
		        }
		    });	
		}
	}
	
	public List<ExecutionEventPair> getAllEventPairs()
	{
		List<ExecutionEventPair> result = new ArrayList<>();
		
		for(ExecutionTrace eachTrace : allTraces)
		{			
			result.addAll(eachTrace.getAsEventPairs());
		}
		
		return result;
	}
	
	public Set<ExecutionEvent> getAnExampleEventForEachActivity()
	{
		Set<String> allActivityNames = new HashSet<String>();

		Set<ExecutionEvent> eventExamples = new HashSet<>();
		
		for(ExecutionTrace eachTrace : allTraces)
		{
			List<ExecutionEvent> allExecutionEvents = eachTrace.getAllEvents();
			
			for(ExecutionEvent eachEvent : allExecutionEvents)
			{
				String activity = eachEvent.getActivityName();
				
				if(!allActivityNames.contains(activity))
				{
					allActivityNames.add(activity);
					eventExamples.add(eachEvent);
				}
				
			}
		}
		
		return eventExamples;
	}
	
}
