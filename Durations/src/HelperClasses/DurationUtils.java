package HelperClasses;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import BaseObject.Tuple;
import CollectionsHelper.ListHelper;
import Statistics.InterquartileRange;

public class DurationUtils {

	/* The idea is that we represent time in a fuzzy fashion
	 * so we use the lowst and the highest duration, and split ít into equal parts
	 *later we check which durations fall in which class
	 */
	public static List<DurationClass> durationClasses(List<Duration> durations)
	{
		/*List<Long> durationsLong = durations.stream().map(x->durationToSeconds(x)).collect(Collectors.toList());

		long binSize = calculateBinSize(durationsLong);
		long minDuration = durationsLong.stream().min(Long::compare).get();
		long maxDuration = durationsLong.stream().max(Long::compare).get();
			
		long currentDurationStart = minDuration;
		
		List<DurationClass> result = new ArrayList<DurationClass>();
		
		if(binSize==0)
		{
			result.add(new DurationClass(Duration.ofSeconds(1), Duration.ofSeconds(2)));
			return result;
		}
		
		do
		{
			long currentDurationEnd = currentDurationStart+binSize;
			
			Duration durationForClassLower = Duration.ofSeconds(currentDurationStart);
			Duration durationForClassHeigher = Duration.ofSeconds(currentDurationEnd);

			DurationClass classDur = new DurationClass(durationForClassLower, durationForClassHeigher);
			
			result.add(classDur);
			
			currentDurationStart = currentDurationEnd;
		}while(currentDurationStart<=maxDuration);
				
		return result;*/
		
		if(durations.isEmpty())
		{
			System.out.println("dur empty!");
		}
		
		List<Tuple<Duration, Duration>> classBorders = durationClassesFromTo(durations);
		
		List<DurationClass> result = new ArrayList<DurationClass>();

		for(Tuple<Duration, Duration> eachBorder : classBorders)
		{
			Duration durationForClassLower = eachBorder.x;
			Duration durationForClassHeigher = eachBorder.y;

			DurationClass classDur = new DurationClass(durationForClassLower, durationForClassHeigher);
	
			result.add(classDur);
		}
		
		return result;
	}
	
	public static List<Tuple<Duration, Duration>> durationClassesFromTo(List<Duration> durations)
	{
		if(ListHelper.isNullOrEmpty(durations))
		{
			return new ArrayList<>();
		}
		
		List<Long> durationsLong = durations.stream().map(x->durationToSeconds(x)).collect(Collectors.toList());

		long binSize = calculateBinSize(durationsLong);
		long minDuration = durationsLong.stream().min(Long::compare).get();
		long maxDuration = durationsLong.stream().max(Long::compare).get();
			
		long currentDurationStart = minDuration;
		
		List<Tuple<Duration, Duration>> result = new ArrayList<>();
		
		if(binSize==0)
		{
			if(durations.size()>0)
			{
				result.add(new Tuple(durations.get(0), durations.get(0)));

			}
			else
			{
				result.add(new Tuple(Duration.ofSeconds(1), Duration.ofSeconds(2)));
			}
			
			return result;
		}
		
		do
		{
			long currentDurationEnd = currentDurationStart+binSize;
			
			Duration durationForClassLower = Duration.ofSeconds(currentDurationStart);
			Duration durationForClassHeigher = Duration.ofSeconds(currentDurationEnd);
			
			result.add(new Tuple<Duration, Duration>(durationForClassLower, durationForClassHeigher));
			
			currentDurationStart = currentDurationEnd;
		}while(currentDurationStart<=maxDuration);
				
		return result;
	}
	
	
	public static long calculateBinSize(Collection<Duration> durations)
	{
		List<Long> durationsLong = durations.stream().map(x->durationToSeconds(x)).collect(Collectors.toList());
		return calculateBinSize(durationsLong);		
	}
	
	public static long calculateBinSize(List<Long> durationsLong)
	{
		if(durationsLong.size()==1)
		{
			return durationsLong.get(0);
		}
		
		if(durationsLong.size()==2)
		{
			return (Math.max(durationsLong.get(0), durationsLong.get(1))-Math.min(durationsLong.get(0), durationsLong.get(1)))/2;
		}
		
		
		//https://en.wikipedia.org/wiki/Freedman%E2%80%93Diaconis_rule
				
		int n = durationsLong.size();
		long iqr = InterquartileRange.calculateIQR(durationsLong);
		
		long binSize = (long) (2*(iqr/(Math.pow(n, 1.0/3))));
		
		return binSize;
	}
	
	public static long durationToSeconds(Duration duration)
	{
		long millis = duration.toMillis();
		
		return TimeUnit.MILLISECONDS.toSeconds(millis);
	}
}
