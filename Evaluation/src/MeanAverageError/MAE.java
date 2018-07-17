package MeanAverageError;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MAE {
//https://de.wikipedia.org/wiki/Mittlerer_absoluter_Fehler
	
	private List<Instant> predictedPointsInTime = new ArrayList<>();
	private List<Instant> exptectedPointsInTime = new ArrayList<>();

	public void newPrediction(Instant expected, Instant predicted)
	{
		if(predicted == null || expected == null)
		{
			System.out.println("Some mae data was null!");
			return;
		}
		
		this.predictedPointsInTime.add(predicted);
		this.exptectedPointsInTime.add(expected);
	}
	
	public double getMAEInDays()
	{
		int n = predictedPointsInTime.size();
		
		long millisecondsPerDay = TimeUnit.DAYS.toMinutes(1);
		
		long absolutSum = 0;
		
		for(int i=0;i<n;i++)
		{
			Instant predicted = predictedPointsInTime.get(i);
			Instant expected = exptectedPointsInTime.get(i);
			
			Duration dur = Duration.between(predicted, expected).abs();
					
			absolutSum=absolutSum+dur.toMinutes();
		}
		
		double mae = (1.0/n)*(absolutSum);
		
		return mae/millisecondsPerDay; //to scale it to days
	}
	
	public void print()
	{
		System.out.println("MAE in days:"+getMAEInDays());
	}
	
	public void printStatistic()
	{
		int n = predictedPointsInTime.size();

		long minsOverEstimated = 0;
		long minsUnterEstimated = 0;
		double totalTimesOverEstimated = 0;
		double totalTimesUnderEstimated = 0;
		
		for(int i=0;i<n;i++)
		{
			Instant predicted = predictedPointsInTime.get(i);
			Instant expected = exptectedPointsInTime.get(i);
			
			Duration dur = Duration.between(predicted, expected);
					
			if(!dur.isNegative())
			{
				totalTimesUnderEstimated++;
				
				minsUnterEstimated+=dur.abs().toMinutes();

			}
			else
			{
				totalTimesOverEstimated++;
				
				minsOverEstimated+=dur.abs().toMinutes();
			}
		}
		
		System.out.println("minsOverEstimated: " + minsOverEstimated);
		System.out.println("minsUnterEstimated: " + minsUnterEstimated);
		System.out.println("totalTimesOverEstimated: " + totalTimesOverEstimated);
		System.out.println("totalTimesUnderEstimated: " + totalTimesUnderEstimated);

	}
	
}
