package PredDuration;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import AccuracyMeasure.Accuracy;
import Configuration.ConfigEvaluation;
import Configuration.DataSource;
import HelperClasses.DurationClassHelper;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionEventPair;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import LogRepresentation.TrainingTesting;
import MeanAverageError.MAE;
import NullHelper.Null;
import PredictionModel.PropabilityPredictionActivityAndTime;
import ReadData.ReadDataIntoObjects;

public class PropabilityDURATION {

	public static void main(String[] args) {
		ProcessModel model = readData();

		TrainingTesting data = model.separateData();
		
		ProcessModel training = data.getDataForTraining();
		
		DurationClassHelper.fillDurationClasses(training);
		
		ProcessModel testing = data.getDataForTesting();
				
		int totalPrediction = 0;

		MAE mae = new MAE();
		
		for(ExecutionTrace eachTesting : testing.getAllTraces())
		{
			int lengthOfTrace = eachTesting.getTraceLength();
			//start with 1 as predicting the start event is not useful
			//go till -2 because the comparison approaches are not predicting the end final even
			for(int currentPosition=1;currentPosition<lengthOfTrace-2;currentPosition++)
			{
				totalPrediction++;
				
				//1) Check if the short term rules apply
				//2) If not fall back to propability based approach
				
				Instant pointInTimeObserved = eachTesting.getEvent(currentPosition+1).getPointInTime(); //to get the followup event

				//String activityNameExpectedRule = RulePrediction.determineMostProablyFollowUpActivity(eachTesting, currentPosition, shortTermRules);		
				Instant pointInTimeExpectedPropability = PropabilityPredictionActivityAndTime.determineMostProablyFollowUpEventTimestamp(
						eachTesting, currentPosition, null, training, null);		

		
				mae.newPrediction(pointInTimeObserved, pointInTimeExpectedPropability);	
					
				mae.print();
			}			
		}
		
		System.out.println("totalPrediction"+":"+totalPrediction);


		mae.print();
	}
	
	private static ProcessModel readData()
	{
		System.out.println("start reading");
		
		ReadDataIntoObjects readBPIC12 = new ReadDataIntoObjects();
		ProcessModel processes = readBPIC12.readData(ConfigEvaluation.dataToUse, false);

		System.out.println("finished reading");
		
		return processes;
	}
}
