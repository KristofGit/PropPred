package LogRepresentation;

import java.time.Duration;

public class ExecutionEventPair {

	private final ExecutionEvent eventOne;
	private final ExecutionEvent eventTwo;
	
	public ExecutionEventPair(ExecutionEvent eventOne, ExecutionEvent eventTwo)
	{
		this.eventOne = eventOne;
		this.eventTwo = eventTwo;
	}
	
	public boolean isForEndEvent()
	{
		return eventTwo == null;
	}

	public ExecutionEvent getEventOne() {
		return eventOne;
	}
	
	public ExecutionEvent getEventTwo() {
		return eventTwo;
	}
		
	public boolean containsArtificalEvent()
	{
		if(getEventOne() != null && getEventOne().isArtificalEvent())
		{
			return true;
		}
		
		if(getEventTwo() != null && getEventTwo().isArtificalEvent())
		{
			return true;
		}
			
		return false;		
	}
	
	public Duration getDuration()
	{
		if(isForEndEvent())
		{
			return null;
		}
		
		if(eventOne.getPointInTime() == null || eventTwo.getPointInTime() == null)
		{
			return null;
		}
		
		return Duration.between(eventOne.getPointInTime(), eventTwo.getPointInTime());
	}
}
