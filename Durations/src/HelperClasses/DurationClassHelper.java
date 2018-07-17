package HelperClasses;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import java.util.Set;

import BaseObject.Tuple;
import Durations.DurationHelper;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionEventPair;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;


public class DurationClassHelper {

	public static class ActivitFromTo
	{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((activityFrom == null) ? 0 : activityFrom.hashCode());
			result = prime * result + ((getActivityTo() == null) ? 0 : getActivityTo().hashCode());
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
			ActivitFromTo other = (ActivitFromTo) obj;
			if (activityFrom == null) {
				if (other.activityFrom != null)
					return false;
			} else if (!activityFrom.equals(other.activityFrom))
				return false;
			if (getActivityTo() == null) {
				if (other.getActivityTo() != null)
					return false;
			} else if (!getActivityTo().equals(other.getActivityTo()))
				return false;
			return true;
		}

		protected final String activityFrom;
		private final String activityTo;
		
		public ActivitFromTo(ExecutionEvent from, ExecutionEvent to)
		{
			this.activityFrom = from.getActivityName();
			this.activityTo = to.getActivityName();
		}
		
		public ActivitFromTo(String from, String to)
		{
			this.activityFrom = from;
			this.activityTo = to;
		}

		public String getActivityTo() {
			return activityTo;
		}
	}
	
	public static class ActivitFromToWithDurations extends ActivitFromTo
	{
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((higherDur == null) ? 0 : higherDur.hashCode());
			result = prime * result + ((lowerDur == null) ? 0 : lowerDur.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			ActivitFromToWithDurations other = (ActivitFromToWithDurations) obj;
			if (higherDur == null) {
				if (other.higherDur != null)
					return false;
			} else if (!higherDur.equals(other.higherDur))
				return false;
			if (lowerDur == null) {
				if (other.lowerDur != null)
					return false;
			} else if (!lowerDur.equals(other.lowerDur))
				return false;
			return true;
		}

		private final Duration lowerDur;
		private final Duration higherDur;
		
		public ActivitFromToWithDurations(ActivitFromTo fromto, Duration lower, Duration higher) {
			super(fromto.activityFrom, fromto.getActivityTo());
			
			this.lowerDur = lower;
			this.higherDur = higher;
		}
		
		public ActivitFromToWithDurations(String from, String to, Duration lower, Duration higher) {
			super(from, to);
			
			this.lowerDur = lower;
			this.higherDur = higher;
		}

		public Duration getLowerDur() {
			return lowerDur;
		}

		public Duration getHigherDur() {
			return higherDur;
		}		
		
		public DurationClass toDurClass()
		{
			return new DurationClass(getLowerDur(), getHigherDur());
		}
		
		public ActivitFromTo toActivityFromTo()
		{
			return new ActivitFromTo(activityFrom, getActivityTo());
		}
	}
	
	//key activityname, value list of related durations from the training process models 
	//this are the real durations which it takes between the two actiivities
	//the activities need to follow directly after each other
	private static Map<ActivitFromTo, List<Duration>> activityAndRealDurations = new HashMap<>();

	//this are durations as duration classes, we we only hold durations
	//the durations list is SORTED by the lower duration
	private static Map<ActivitFromTo, List<ActivitFromToWithDurations>> activityAndRuleDurations = new HashMap<>();

	private static boolean isInitialized = false;
	
	public static void fillDurationClasses(ProcessModel processes)
	{
		activityAndRealDurations.clear();
		activityAndRuleDurations.clear();
		
		prepareGeneralDurationData(processes);
		prepareDurationClasses();		
		isInitialized = true;
	}
	
	public static boolean isInitalized()
	{
		return isInitialized;
	}

	public static int getAmountOfBins(ExecutionEvent from, ExecutionEvent to)
	{
		return activityAndRuleDurations.get(new ActivitFromTo(from,to)).size();
	}
	
	public static int getBinsBetween(ActivitFromToWithDurations one, ActivitFromToWithDurations two)
	{
		//assumes that one and two are for the same activities#
		 List<ActivitFromToWithDurations> durations = activityAndRuleDurations.get(one.toActivityFromTo());
				 
		 return Math.abs(durations.indexOf(one) - durations.indexOf(two));		 
	}
	
	public static List<ActivitFromToWithDurations> getDurationBinsFor(ActivitFromTo activity)
	{
		return activityAndRuleDurations.get(activity);
	}
	
	public static ActivitFromToWithDurations getDurationBinFor(ExecutionEvent from, ExecutionEvent to)
	{
		if(from.isArtificalEvent())
		{
			return null;
		}
		
		String fromA = from.getActivityName();
		String toA = to.getActivityName();
		
		Duration dur = ExecutionEvent.between(from, to);
		
		List<ActivitFromToWithDurations> durationBins = activityAndRuleDurations.get(new ActivitFromTo(fromA, toA));
		
		ActivitFromToWithDurations result = null;
				
		for(ActivitFromToWithDurations eachEntry : durationBins)
		{
			Duration low = eachEntry.getLowerDur();
			Duration high = eachEntry.getHigherDur();
			
			if(DurationHelper.isBetween(low, high, dur))
			{				
				result = eachEntry;
				break;
			}
		}		
				
		return result;
	}
	
	private static void prepareDurationClasses()
	{
		Set<Entry<ActivitFromTo, List<Duration>>> entries = activityAndRealDurations.entrySet();
	
		for(Entry<ActivitFromTo, List<Duration>> eachEntry : entries)
		{
			ActivitFromTo key = eachEntry.getKey();
			
			String activityNameOne = key.activityFrom;
			String activityNameTwo = key.getActivityTo();

			if(Objects.equals(activityNameOne, "W_Beoordelen fraude") && Objects.equals(activityNameTwo, "W_Completeren aanvraag"))
			{
				System.out.println("got you");
			}
			
			
			List<Tuple<Duration, Duration>> classes = DurationUtils.durationClassesFromTo(eachEntry.getValue());

			List<ActivitFromToWithDurations> bins = new ArrayList<>();
			
			Duration lowestDuration=DurationHelper.getMaxDuration(), highestDuration=DurationHelper.getMinDuration();

			for(Tuple<Duration, Duration> eachClass : classes)
			{
				Duration lower = eachClass.x;
				Duration higher = eachClass.y;
				
				bins.add(new ActivitFromToWithDurations(key, lower, higher));
		
			
				if(DurationHelper.isBelowOrEqual(lower, lowestDuration))
				{
					lowestDuration = lower;
				}
				
				if(DurationHelper.isAboveOrEqual(higher, highestDuration))
				{
					highestDuration = higher;
				}
			}	
			
			//add two dummy durations to cover all potential cases
			bins.add(new ActivitFromToWithDurations(key, DurationHelper.getMinDuration(), lowestDuration));
			bins.add(new ActivitFromToWithDurations(key, highestDuration, DurationHelper.getMaxDuration()));

			//we sort to later on easly determine the amount of bins that lay between two durations
			bins.sort(new Comparator<ActivitFromToWithDurations>() {

				@Override
				public int compare(ActivitFromToWithDurations o1, ActivitFromToWithDurations o2) {
					return o1.lowerDur.compareTo(o2.lowerDur);
				}
			});
			
			activityAndRuleDurations.put(key, bins);
		}
	}

	private static void prepareGeneralDurationData(ProcessModel processes) {
		List<ExecutionTrace> traces = processes.getAllTraces();
		
		for(ExecutionTrace eachTrace : traces)
		{
			List<ExecutionEventPair> pairs = eachTrace.getAsEventPairs();
			
			for(ExecutionEventPair eachPair : pairs)
			{
				if(eachPair.isForEndEvent())
				{
					continue;
				}
				
				String activityNameOne = eachPair.getEventOne().getActivityName();
				String activityNameTwo = eachPair.getEventTwo().getActivityName();

				Duration dur = eachPair.getDuration();
				
				if(dur == null)
				{
					continue;
				}
				
				ActivitFromTo fromTo = new ActivitFromTo(activityNameOne, activityNameTwo);
				
				 List<Duration> durationList = activityAndRealDurations.get(fromTo);
				 
				 if(durationList == null)
				 {
					 durationList = new ArrayList<>();
					 activityAndRealDurations.put(fromTo, durationList);
				 }
				 
				 durationList.add(dur);
			}
		}
	}
	

}
