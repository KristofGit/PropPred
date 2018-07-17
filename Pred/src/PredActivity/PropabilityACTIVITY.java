package PredActivity;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import AccuracyMeasure.Accuracy;
import Configuration.ConfigEvaluation;
import Configuration.DataSource;
import HelperClasses.DurationClassHelper;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import LogRepresentation.TrainingTesting;
import NullHelper.Null;
import PredictionModel.PropabilityPredictionActivityAndTime;
import ReadData.ReadDataIntoObjects;

public class PropabilityACTIVITY {

	public static void main(String[] args) {
		ProcessModel model = readData();

		TrainingTesting data = model.separateData();
		
		ProcessModel training = data.getDataForTraining();
		
		DurationClassHelper.fillDurationClasses(training);

		ProcessModel testing = data.getDataForTesting();
		
		//testing = testing.shortenTheTraces(11); //+1 as we have here to include the artifical start event
				
		Accuracy evaluation = new Accuracy();
		
		int totalPrediction = 0;

		int correctPredictedPropability = 0;
		int wrongPredictedPropability = 0;

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
				
				String activityNameObserved = eachTesting.getEvent(currentPosition+1).getActivityName(); //to get the followup event

				//String activityNameExpectedRule = RulePrediction.determineMostProablyFollowUpActivity(eachTesting, currentPosition, shortTermRules);		
				String activityNamePredicted = PropabilityPredictionActivityAndTime.determineMostProablyFollowUpActivity(
						eachTesting, currentPosition, training, activityNameObserved);		
				
				if(activityNamePredicted!=null)
				{					
					if(Objects.equals(activityNameObserved, activityNamePredicted))
					{
						correctPredictedPropability++;
					}
					else
					{
						wrongPredictedPropability++;
					}				
				}
		
				if(Objects.equals(activityNameObserved, activityNamePredicted))
				{
					correctPredictedPropability++;
				}
				else
				{
					wrongPredictedPropability++;
				}
				
								
				evaluation.analyze(activityNameObserved, activityNamePredicted);
				
				evaluation.print();
			}
		}
		
		System.out.println("totalPrediction"+":"+totalPrediction);

		System.out.println("correctPredictedPropability"+":"+correctPredictedPropability);
		System.out.println("wrongPredictedPropability"+":"+wrongPredictedPropability);

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
