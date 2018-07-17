package Main;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import AccuracyMeasure.Accuracy;
import ActivityHelper.ActivityClass;
import BaseObject.Tripple;
import BaseObject.Tuple;
import Configuration.ConfigEvaluation;
import Configuration.DataSource;
import Histograms.CustomClass;
import Histograms.HistogramCustomClasses;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import LogRepresentation.TrainingTesting;
import MeanAverageError.MAE;
import PredictionModel.PropabilityPredictionActivityAndTime;
import ReadData.ReadDataIntoObjects;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur.ItemEqualityNew;

public class mainProp {

	/* Read in all traces
	 * For a given trace X select the XY most similar traces 
	 * Measure trace similartiy bawsed on DamerauLevenshtein
	 * 
	 * Build a model that can predict exactly the next activity
	 * 
	 * Future: 
	 */
	
	public static void main(String[] args) {
	
		//testActivityPropabilityBasedPrediction();
		testNextEventTimestampPropabilityBasedPrediction();
		
	}
	
	private static void testNextEventTimestampPropabilityBasedPrediction()
	{
		System.out.println("start reading");
		
		ReadDataIntoObjects readBPIC12 = new ReadDataIntoObjects();
		ProcessModel processes = readBPIC12.readData(DataSource.BPIC_2012, false);

		System.out.println("finished reading");
		
		TrainingTesting data = processes.separateData();
		
		ProcessModel training = data.getDataForTraining();
		ProcessModel testing = data.getDataForTesting();
		
		
		MAE evaluation = new MAE();
		
		for(ExecutionTrace eachTesting : testing.getAllTraces())
		{
			int lengthOfTrace = eachTesting.getTraceLength();
			//start with 1 as predicting the start event is not useful
			for(int currentPosition=1;currentPosition<lengthOfTrace-2;currentPosition++)
			{
				Instant activityNameObserved = eachTesting.getEvent(currentPosition+1).getPointInTime(); //to get the followup event

				Instant activityNameExpected = PropabilityPredictionActivityAndTime.
						determineMostProablyFollowUpEventTimestamp(eachTesting, currentPosition, null, training, null);
		
				evaluation.newPrediction(activityNameObserved, activityNameExpected);
				
				evaluation.print();
			}
		}
	}

	private static void testActivityPropabilityBasedPrediction() {
		System.out.println("start reading");
		
		ReadDataIntoObjects readBPIC12 = new ReadDataIntoObjects();
		ProcessModel processes = readBPIC12.readData(DataSource.BPIC_2012, false);

		System.out.println("finished reading");
		
		TrainingTesting data = processes.separateData();
		
		ProcessModel training = data.getDataForTraining();
		ProcessModel testing = data.getDataForTesting();
		
		
		Accuracy evaluation = new Accuracy();
		
		for(ExecutionTrace eachTesting : testing.getAllTraces())
		{
			int lengthOfTrace = eachTesting.getTraceLength();
			//start with 1 as predicting the start event is not useful
			for(int currentPosition=1;currentPosition<lengthOfTrace-2;currentPosition++)
			{
				String activityNameObserved = eachTesting.getEvent(currentPosition+1).getActivityName(); //to get the followup event

				String activityNameExpected = PropabilityPredictionActivityAndTime.determineMostProablyFollowUpActivity(eachTesting, currentPosition, training, activityNameObserved);
		
				evaluation.analyze(activityNameObserved, activityNameExpected);
				
				evaluation.print();
			}
		}
	}
	


}
