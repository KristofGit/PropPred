package LogRepresentation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import CollectionsHelper.ListHelper;
import Configuration.ConfigEvaluation;

public class ExecutionTrace {

	private List<ExecutionEvent> allEvents = new ArrayList<>();
	
	public List<ExecutionEvent> getAllEvents() {
		return Collections.unmodifiableList(allEvents);
	}

	public void addNewEvent(ExecutionEvent e)
	{
		allEvents.add(e);
		
		List tmp = new ArrayList(allEvents);

		allEvents.sort(new Comparator<ExecutionEvent>() {

			@Override
			public int compare(ExecutionEvent o1, ExecutionEvent o2) {
				
				if(o1.isArtificalFinalEvent())
				{
					return 1;
				}
				
				if(o1.isArtificalStartEvent())
				{
					return -1;
				}
				
				if(o2.isArtificalFinalEvent())
				{
					return -1;
				}
				
				if(o2.isArtificalStartEvent())
				{
					return 1;
				}
				
				return o1.getPointInTime().compareTo(o2.getPointInTime());
			}
		});
		
		if(!tmp.equals(allEvents))
		{
			System.out.println("Got unsorted data, sorting changed orders!");
		}
	}
	
	public List<ExecutionEvent> getEventsBeforeIndex(int index)
	{		
		if(index >= allEvents.size())
		{ 
			index = allEvents.size(); //should end up in an empty list
		}
		
		return allEvents.subList(0, index);
	}
	
	
	public Instant traceStart()
	{
		return allEvents.stream().map(x->x.getPointInTime()).filter(x->x!=null).min(Instant::compareTo).get();
	}
	
	public Instant traceEnd()
	{
		return allEvents.stream().map(x->x.getPointInTime()).filter(x->x!=null).max(Instant::compareTo).get();
	}
	
	public boolean isInRange(Instant rangeStart, Instant rangeEnd)
	{
		Instant traceStart = traceStart();
		Instant traceEnd = traceEnd();
		
		//either it starts or end in the range or it overlaps with the range
		
		return (rangeStart.isBefore(traceStart) && rangeEnd.isAfter(traceStart)) || //beginns in
				(rangeStart.isBefore(traceEnd) && rangeEnd.isAfter(traceEnd)) || //ends in
				(rangeStart.isAfter(rangeStart) && rangeEnd.isBefore(rangeEnd)); //overlaps
	}
	
	public List<ExecutionEvent> getEventsAfterIndex(int index)
	{
		index = index+1;//we search after this index
		
		if(index >= allEvents.size())
		{ 
			index = allEvents.size(); //should end up in an empty list
		}
		
		return allEvents.subList(index, allEvents.size());
	}
	
	public ExecutionTrace stripLastEvent()
	{
		return getTraceWithEventsBeforeIndex(this.getTraceLength()-1);
	}
	
	//return an integer, which is the position were we found the event
	public List<Integer> containsEqualActivityAtAbout(ExecutionEvent eventToCompare, int sourroundIndex, boolean iAmDesprate)
	{	
		List<Integer> result = new ArrayList<>();
		 
		if(eventToCompare == null)
		{
			return result;
		}
		
		int widening = (int) Math.max(allEvents.size() * ConfigEvaluation.EqualEventIndexWideningRelative, 1);
		
		if(iAmDesprate)
		{
			widening = getTraceLength();
		}
		
		//the trace should (after about the same amount of events) contain the same actiivty
				
		/*for(int i=sourroundIndex-widening;i<sourroundIndex+widening;i++)
		{
			if(i>=0 && i<allEvents.size())
			{
				String activityNameCompare = eventToCompare.getActivityName();
				String actitivtyNameTrace = allEvents.get(i).getActivityName();
				
				if(Objects.equals(activityNameCompare, actitivtyNameTrace))
				{
					return i;
				}
			}
		}*/
		
			
		for(int i=0;i<widening;i++)
		{
			boolean left = true;
		
			for(;;)
			{
				int indexToCheck = left ? sourroundIndex-i:sourroundIndex+i;

				String activityNameCompare = eventToCompare.getActivityName();
				ExecutionEvent actitivtyTrace = ListHelper.get(allEvents, indexToCheck);
				
				if(actitivtyTrace != null && Objects.equals(activityNameCompare, actitivtyTrace.getActivityName()))
				{
					result.add(indexToCheck);
				}
				
				if(!left)
				{
					break;
				}
				
				left = false;
			}
			
		}
		
		return result.stream().distinct().collect(Collectors.toList());	
	}
	
	public ExecutionEvent getEvent(int index)
	{
		if(index<0 || index >= allEvents.size())
		{ 
			return null;
		}
		
		return allEvents.get(index);		
	}
	
	public ExecutionTrace getTraceWithEventsAfterIndex(int index)
	{
		ExecutionTrace result = new ExecutionTrace();
		result.allEvents = getEventsAfterIndex(index);
		return result;
	}
	
	public ExecutionTrace getTraceWithEventsBeforeIndex(int index)
	{		
		ExecutionTrace result = new ExecutionTrace();
		result.allEvents = getEventsBeforeIndex(index);
		return result;
	}
	
	public ExecutionTrace getTraceWithEventsBeforeAndIncludingIndex(int index)
	{		
		ExecutionTrace result = new ExecutionTrace();
		result.allEvents = getEventsBeforeIndex(index+1);
		return result;
	}
	
	public ExecutionTrace getLastXEvents(int number)
	{
		ExecutionTrace result = new ExecutionTrace();
		result.allEvents = this.allEvents.subList(Math.max(0, this.getTraceLength()-number), this.getTraceLength());
		return result;
	}
	
	//assumption, the entries in the log are chronologically ordered for each trace
	public ExecutionEvent getFirstEventWithTime()
	{
		ExecutionEvent result = null;
		
		for(ExecutionEvent eachEvent : allEvents)
		{
			Instant pointInTime = eachEvent.getPointInTime();
			if(pointInTime != null)
			{
				result = eachEvent;
				break;
			}
		}
		
		return result;
	}
	
	public int getTraceLength()
	{
		return allEvents.size();
	}
	
	public List<ExecutionEventPair> getAsEventPairs()
	{
		List<ExecutionEventPair> result = new ArrayList<>();
		
		for(int i=0;i<allEvents.size();i++)
		{
			ExecutionEvent one = this.getEvent(i);
			ExecutionEvent two = null;
			
			if(i<allEvents.size()-1)
			{
				two = this.getEvent(i+1);
				result.add(new ExecutionEventPair(one, two));
			}
		}
		
		return result;
	}
	
	public ExecutionEvent getLastEvent()
	{
		ExecutionEvent result = null;
		
		if(!allEvents.isEmpty())
		{
			result = allEvents.get(allEvents.size()-1);
		}
		
		return result;
	}
}
