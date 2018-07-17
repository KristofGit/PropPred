package Durations;

import java.time.Duration;
import java.util.List;

public class DurationHelper {

	public static Duration average(List<Duration> durs)
	{
		if(durs == null || durs.isEmpty())
		{
			return null;
		}
		
		double averageDur = durs.stream().mapToLong(x->x.toMinutes()).average().getAsDouble();

		return Duration.ofMinutes((long)averageDur);		
	}
	
	public static boolean isBetween(Duration lower, Duration heigher, Duration value)
	{
		if(lower == null || heigher == null || value == null)
		{
			return false;
		}
		
		boolean greaterThenLowerB = lower.compareTo(value)<=0;
		boolean lowerThenUpperB = heigher.compareTo(value)>=0;

		return greaterThenLowerB && lowerThenUpperB;	
	}
	
	public static boolean isBelowOrEqual(Duration isThis, Duration belowThis)
	{
		return isThis.compareTo(belowThis)<=0;
	}
	
	public static boolean isAboveOrEqual(Duration isThis, Duration belowThis)
	{
		return isThis.compareTo(belowThis)>=0;
	}
	
	public static Duration getMinDuration()
	{
		return Duration.ZERO;
	}
	
	public static Duration getMaxDuration()
	{
		return Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
	}
}
