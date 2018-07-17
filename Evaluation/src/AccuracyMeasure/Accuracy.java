package AccuracyMeasure;

import java.util.Objects;

public class Accuracy {

	// if the prediced and the real activity are "the same"
	private int correctlyDetermined;
	private int incorrectlyDetermined; 
	
	public void analyze(String realObserved, String predicted)
	{
		if(Objects.equals(realObserved, predicted))
		{
			correctlyDetermined++;
		}
		else
		{
			incorrectlyDetermined++;
		}
	}
	
	public double getAccuracy()
	{
		return ((double)correctlyDetermined)/(correctlyDetermined+incorrectlyDetermined);
	}
	
	public void print()
	{
		System.out.println("Achived Accuracy was:"+getAccuracy());
	}
}

