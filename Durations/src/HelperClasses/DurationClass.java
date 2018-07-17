package HelperClasses;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import Histograms.CustomClass;

public class DurationClass extends CustomClass<Duration> {

	public DurationClass(Duration lowerBound, Duration upperBound) {
		super(lowerBound, upperBound);
	}

	@Override
	public boolean belongsToThisClass(Duration value) {

		//belongs to class if duratiton => lower bound und <= upper bound
		boolean greaterThenLowerB = lowerBound.compareTo(value)<=0;
		boolean lowerThenUpperB = upperBound.compareTo(value)>=0;

		return greaterThenLowerB && lowerThenUpperB;
	}

	@Override
	public boolean equals(Object other) {
		
		if(other == null || !(other instanceof DurationClass))
		{
			return false;
		}
		
		DurationClass dc = (DurationClass)other;
		
		return lowerBound.equals(dc.lowerBound);
	}

	@Override
	public int hashCode() {
		return lowerBound.hashCode();
	}

	@Override
	public Duration getRandomRepresentation() {
		
		//return lowerBound;
		
		long average = (long) (lowerBound.toMillis() + (((upperBound.toMillis()-lowerBound.toMillis()) /2)));
		return Duration.ofMillis(average);
	}

}
