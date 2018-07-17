package HelperObjects;

import LogRepresentation.ExecutionEvent;

public class NextEvent {

	private final ExecutionEvent nextEvent;
	private final ExecutionEvent eventBeforeNextEvent;
	private final double distance;
	
	public NextEvent(ExecutionEvent nextEvent, ExecutionEvent eventBeforeNextEvent, double distance)
	{
		this.nextEvent = nextEvent;
		this.eventBeforeNextEvent = eventBeforeNextEvent;
		this.distance = distance;
	}

	public ExecutionEvent getNextEvent() {
		return nextEvent;
	}

	public ExecutionEvent getEventBeforeNextEvent() {
		return eventBeforeNextEvent;
	}

	public double getDistance() {
		return distance;
	}
}
