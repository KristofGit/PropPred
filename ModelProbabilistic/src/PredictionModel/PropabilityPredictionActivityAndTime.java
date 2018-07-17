package PredictionModel;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ActivityHelper.ActivityClass;
import BaseObject.Tripple;
import Configuration.ConfigEvaluation;
import Durations.DurationHelper;
import HelperClasses.DurationClass;
import HelperClasses.DurationClassHelper;
import HelperClasses.DurationClassHelper.ActivitFromTo;
import HelperClasses.DurationClassHelper.ActivitFromToWithDurations;
import HelperClasses.DurationUtils;
import HelperObjects.NextEvent;
import Histograms.HistogramCustomClasses;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionEventPair;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur.ItemEqualityNew;
import TraceSimilarity.DamerauLevenshtein_activity_only;
import TraceSimilarity.DamerauLevenshtein_activity_only.ItemEquality;

public class PropabilityPredictionActivityAndTime {

	//predict the duration that we will have to wait till the next event shows up
	
	public static Instant determineMostProablyFollowUpEventTimestamp(
			ExecutionTrace currentTrace, 
			int currentPosition, 
			String expectedNextActivity,
			ProcessModel historicTrace,
			Instant pointInTimeObserved)
	{
		
		//String nextAcitivty = determineMostProablyFollowUpActivity(currentTrace, currentPosition, historicTrace, null);
		
		List<NextEvent> followUpEventsFromSimilarTraces = nextEventBasedOnSimilarTraces(currentTrace, currentPosition, expectedNextActivity, historicTrace);

		List<Duration> durations = new ArrayList<Duration>();
		List<Double> weights = new ArrayList<Double>();

		for(NextEvent eachNextEvent : followUpEventsFromSimilarTraces)
		{			
			Instant currentEvent = eachNextEvent.getEventBeforeNextEvent().getPointInTime();
			Instant nextEvent = eachNextEvent.getNextEvent().getPointInTime();
						
			Duration ns = Duration.between(currentEvent, nextEvent);		
			
			durations.add(ns);
			weights.add(1/(eachNextEvent.getDistance()+1)); //we do +1 to address the case when distance = 0. Diviging by fractions between 1 and 0 would result in unreasonable heigh weights
		}
		
		List<DurationClass> classesForHist = DurationUtils.durationClasses(durations);

		/*if(expectedNextActivity != null)
		{
			NextEvent exampleEvent = followUpEventsFromSimilarTraces.get(0);
			
			List<ActivitFromToWithDurations> bins = DurationClassHelper.getDurationBinsFor(new ActivitFromTo(exampleEvent.getEventBeforeNextEvent(), exampleEvent.getNextEvent()));

			classesForHist = bins.stream().map(x->{
				return new DurationClass(x.getLowerDur(), x.getHigherDur());
			}).collect(Collectors.toList());
		}*/
		
 		HistogramCustomClasses<Duration,DurationClass> hist = 
				new HistogramCustomClasses(classesForHist, durations, weights);

 		HistogramCustomClasses<Duration,DurationClass> hist2 = 
				new HistogramCustomClasses(classesForHist, durations, null);
 		
		//Duration mostLikelyDurationsdaw =  hist.getMostLikely();
		//Duration mostdurLikely =  hist.getMostLikely();

		Duration predictedDuration =  DurationHelper.average(hist.getMostLikelyComplex());
		
		Duration mostLikelyWeight=  hist.getMostLikely();
		Duration mostLikelyRandomWeight =  hist.getRandom();
		Duration mostLikelyComplexWeight=  DurationHelper.average(hist.getMostLikelyComplex());
		Duration avg = DurationHelper.average(durations);
		Duration mostLikelyWeight2=  hist2.getMostLikely();
		Duration mostLikelyRandomWeight2 =  hist2.getRandom();
		Duration mostLikelyComplexWeight2=  DurationHelper.average(hist2.getMostLikelyComplex());
		
		//Duration mostLikelyDurationsdaw =  DurationHelper.average(durations);
		//take the current event instant and add the predicted most probable duration
		//to construct the expected instant
		
		if(predictedDuration == null)
		{
			System.out.println("failed with propability!");
			
			predictedDuration = Duration.ofHours(8);
		}
		
		Instant currentEventStartingInstant = currentTrace.getEvent(currentPosition).getPointInTime();
		Instant nextPredictedStartingInstant = currentEventStartingInstant.plus(predictedDuration);
		
		if(pointInTimeObserved != null)
		{
			String currentActivity = currentTrace.getEvent(currentPosition).getActivityName();
			
			Duration optimalDuration = Duration.between(currentEventStartingInstant, pointInTimeObserved).abs();

			Duration differencePredictedAndExpected = Duration.between(pointInTimeObserved, nextPredictedStartingInstant);
			
			if(differencePredictedAndExpected.abs().toDays()>1)
			{
				
				System.out.println("dif greted one day! From: " + currentActivity + " To:" + expectedNextActivity);
			}
		}

		return nextPredictedStartingInstant;
	}
	
	public static String determineMostProablyFollowUpActivity(
			ExecutionTrace currentTrace, 
			int currentPosition, 
			ProcessModel historicTrace,
			String corrextWouldBe)
	{
		List< ExecutionEvent> followUpEventsFromSimilarTraces = nextEventBasedOnSimilarTraces(currentTrace, currentPosition, historicTrace)
				.stream().map(x->x.getNextEvent()).collect(Collectors.toList());
				
		List<String> activityNamesRelevant = new ArrayList<>();
		
		for(ExecutionEvent eachRelevantEvent : followUpEventsFromSimilarTraces)
		{
			if(eachRelevantEvent == null)
			{
				activityNamesRelevant.add(eachRelevantEvent.getActivityName()); 

			}
			
			activityNamesRelevant.add(eachRelevantEvent.getActivityName()); 
		}
		
		List<ActivityClass> possibleClasses = activityNamesRelevant
.stream().distinct().map(x->new ActivityClass(x)).collect(Collectors.toList());
		
		HistogramCustomClasses<String,ActivityClass> hist = 
				new HistogramCustomClasses(possibleClasses, activityNamesRelevant);

		String mostLikelyAcitivty =  hist.getMostLikely();
		
		if(!Objects.equals(mostLikelyAcitivty, corrextWouldBe))
		{
			//mostLikelyAcitivty =  hist.getMostLikely();
			//to set brakepoints for manual comparison and analysis
		}
		
		return mostLikelyAcitivty;
	}
	
	private static List<NextEvent> nextEventBasedOnSimilarTraces(ExecutionTrace currentTrace, int currentPosition, ProcessModel historicTrace)
	{
		return nextEventBasedOnSimilarTraces(currentTrace, currentPosition, null, historicTrace);
	}
		
	private static List<NextEvent> nextEventBasedOnSimilarTraces(ExecutionTrace currentTrace, int currentPosition, String expectedNextActivity, ProcessModel historicTrace)
	{
		ExecutionEvent currentEvent = currentTrace.getEvent(currentPosition);	
				
		ExecutionTrace comparisonTrace = currentTrace.getTraceWithEventsBeforeAndIncludingIndex(currentPosition);
		
		//distance, trace snipped extraced till the searched event position, followup event next to the searched one
		List<Tripple<Double, ExecutionTrace, NextEvent>> subtracesMeasuredSimilarity = new ArrayList<>();
		
		boolean relaxedSearch = false;
		
		int prefix = ConfigEvaluation.PrefixForSimCalculation;
		
		/*Duration range = Duration.ofDays(90);
		Instant rangeStart = comparisonTrace.traceStart().minus(range);
		Instant rangeEnd = comparisonTrace.traceEnd().plus(range);*/

		do
		{		
			for(ExecutionTrace eachTrace : historicTrace.getAllTraces())
			{
				/*if(!eachTrace.isInRange(rangeStart, rangeEnd))
				{
					continue;
				}*/
				
				List<Integer> foundIndex = eachTrace.containsEqualActivityAtAbout(currentEvent, currentPosition, relaxedSearch);
				
				//its -1 if something went wrong or if it was not found
				for(Integer eachIndex : foundIndex)
				{
					ExecutionEvent folloUpEvent = eachTrace.getEvent(eachIndex+1);
					
					if(folloUpEvent == null)
					{
						continue;
					}
					
					if(expectedNextActivity != null)
					{
						String followUpActivityName = folloUpEvent.getActivityName();
						
						if(!Objects.equals(followUpActivityName, expectedNextActivity))
						{
							continue;
						}
					}
					
					if(folloUpEvent.isArtificalFinalEvent())
					{
						continue; //ignore the artifical final events as those wre not predicted by the comparison paper
					}
										
					ExecutionTrace eachTraceSpecialized = eachTrace.getTraceWithEventsBeforeAndIncludingIndex(eachIndex);
						
					double distance = measureTraceSimilarityActivityOnly(comparisonTrace.getLastXEvents(prefix),eachTraceSpecialized.getLastXEvents(prefix));

					//double distance = measureTraceSimilarityActivityAndDuration(comparisonTrace.getLastXEvents(prefix),eachTraceSpecialized.getLastXEvents(prefix));
										
					NextEvent nextEvent = new NextEvent(folloUpEvent, eachTrace.getEvent(eachIndex), distance);
					
					subtracesMeasuredSimilarity.add(
							new Tripple<Double, ExecutionTrace, NextEvent>(distance, eachTraceSpecialized, nextEvent));
				}
			}
			
			if(subtracesMeasuredSimilarity.isEmpty())
			{
			
				if(relaxedSearch)
				{
					System.out.println("Giving up relaxed search too!");
					break; //give up
				}
				
				relaxedSearch = true;
			}	
			
		}
		while(subtracesMeasuredSimilarity.isEmpty());
	
		//check if we have traces with 0 differences and prefere them
		if(subtracesMeasuredSimilarity.stream().filter(x->x.x==0).count()>0)
		{
			//subtracesMeasuredSimilarity = subtracesMeasuredSimilarity.stream().filter(x->x.x==0).collect(Collectors.toList());
			
			//return subtracesMeasuredSimilarity.stream().map(x->x.z).collect(Collectors.toList());
		}
		
		Collections.sort(subtracesMeasuredSimilarity, new Comparator<Tripple<Double, ExecutionTrace, NextEvent>>() {
	        @Override
	        public int compare(Tripple<Double, ExecutionTrace, NextEvent> trace1, Tripple<Double, ExecutionTrace, NextEvent> trace2)
	        {
	        	return Double.compare(trace1.x, trace2.x);
	        }
	    });	
		
		 
		int amountOfTracesToTage = Math.max((int) (subtracesMeasuredSimilarity.size()*ConfigEvaluation.MostSimTracesTaken),1);
		if(ConfigEvaluation.FixedAmountOfMostSimTraces > 0)
		{			
			amountOfTracesToTage = ConfigEvaluation.FixedAmountOfMostSimTraces;			
		}
		
		return 	subtracesMeasuredSimilarity.stream().limit(amountOfTracesToTage).map(x->x.z).collect(Collectors.toList());
	}
	
	private static ItemEquality<ExecutionEvent> distanceEquality = new ItemEquality<ExecutionEvent>()
	{
		@Override
		public boolean isEqual(ExecutionEvent one, ExecutionEvent two) {
			return Objects.equals(one.getActivityName(), two.getActivityName());
		}
	};
	
	private  static int measureTraceSimilarityActivityOnly(ExecutionTrace traceOne, ExecutionTrace traceTwo)
	{
		List<ExecutionEvent> eventOne = traceOne.getAllEvents();
		List<ExecutionEvent> eventTwo = traceTwo.getAllEvents();
		
		return DamerauLevenshtein_activity_only.distance(distanceEquality, eventTwo, eventOne);
	}
	
	private static ItemEqualityNew<ExecutionEventPair> distanceAdvanced = new ItemEqualityNew<ExecutionEventPair>()
	{
		@Override
		public boolean isMajorSimiar(ExecutionEventPair one, ExecutionEventPair two) {
			//same activity
						
			return Objects.equals(one.getEventOne().getActivityName(), two.getEventOne().getActivityName()) &&
					Objects.equals(one.getEventTwo().getActivityName(), two.getEventTwo().getActivityName());
		}

		@Override
		public boolean isMinorSimilar(ExecutionEventPair one, ExecutionEventPair two) {
			
			if(one.containsArtificalEvent() || two.containsArtificalEvent())
			{
				return true;
			}
			
			ActivitFromToWithDurations binOne = DurationClassHelper.getDurationBinFor(one.getEventOne(), one.getEventTwo());
			ActivitFromToWithDurations binTwo = DurationClassHelper.getDurationBinFor(two.getEventOne(), two.getEventTwo());
				
			return binOne.equals(binTwo);
		}

		@Override
		public double minorSimilarCost(ExecutionEventPair one, ExecutionEventPair two, double totalPossibleCost) {
			//how far aparte are the duration classes
			
			ActivitFromToWithDurations binOne = DurationClassHelper.getDurationBinFor(one.getEventOne(), one.getEventTwo());
			ActivitFromToWithDurations binTwo = DurationClassHelper.getDurationBinFor(two.getEventOne(), two.getEventTwo());
				
			int amount = DurationClassHelper.getAmountOfBins(one.getEventOne(), one.getEventTwo());
			int difference = DurationClassHelper.getBinsBetween(binOne, binTwo);
				
			return (totalPossibleCost*(1-(difference/(double)amount)));
		}

	};
	
	private static double measureTraceSimilarityActivityAndDuration(ExecutionTrace traceOne, ExecutionTrace traceTwo)
	{
		List<ExecutionEventPair> eventOne = traceOne.getAsEventPairs();
		List<ExecutionEventPair> eventTwo = traceTwo.getAsEventPairs();

		return DamerauLevenshtein_activity_and_dur.distance(distanceAdvanced, eventOne, eventTwo);
	}
	
}
