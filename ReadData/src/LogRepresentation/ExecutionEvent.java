package LogRepresentation;

import java.time.Duration;
import java.time.Instant;

import BaseObject.HashEqual;

public class ExecutionEvent implements HashEqual{

	private final String resourceName;
	private final String activityName;
	private final Instant pointInTime;
	
	private final InternalEventType eventType;
	
	private ExecutionEvent(InternalEventType type)
	{
		this.resourceName = null;
		this.activityName = type.name();
		this.pointInTime = null;
		eventType = type;
	}
	
	public InternalEventType getEventType() {
		return eventType;
	}
	
	public static Duration between(ExecutionEvent one, ExecutionEvent two)
	{	
		if(one.getPointInTime() == null ||  two.getPointInTime() == null)
		{
			return null;
		}
		
		return Duration.between(one.getPointInTime(), two.getPointInTime()).abs();
	}
	
	public ExecutionEvent(String resourceName, String activityName, Instant pointInTime)
	{
		this.resourceName = resourceName;
		this.activityName = activityName;
		this.pointInTime = pointInTime;
		eventType = InternalEventType.NormalEvent;
	}
	
	public static ExecutionEvent getStartEvent()
	{
		return new ExecutionEvent(InternalEventType.TraceStarted);
	}
	
	public static ExecutionEvent getEndEvent()
	{
		return new ExecutionEvent(InternalEventType.TraceFinished);
	}

	public boolean isArtificalStartEvent()
	{
		return eventType == InternalEventType.TraceStarted;
	}
	
	public boolean isArtificalFinalEvent()
	{
		return eventType == InternalEventType.TraceFinished;
	}
	
	public boolean isArtificalEvent()
	{
		return eventType == InternalEventType.TraceFinished || eventType == InternalEventType.TraceStarted;
	}
	
	public String getActivityName() {
		return activityName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityName == null) ? 0 : activityName.hashCode());
		result = prime * result + ((pointInTime == null) ? 0 : pointInTime.hashCode());
		result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutionEvent other = (ExecutionEvent) obj;
		if (activityName == null) {
			if (other.activityName != null)
				return false;
		} else if (!activityName.equals(other.activityName))
			return false;
		if (pointInTime == null) {
			if (other.pointInTime != null)
				return false;
		} else if (!pointInTime.equals(other.pointInTime))
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		return true;
	}

	public Instant getPointInTime() {
		return pointInTime;
	}
}
