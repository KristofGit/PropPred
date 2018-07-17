package Randomization;

import java.util.Random;

public class RandUtils {

	private static Random random = new Random();
	
	public static double getDouble()
	{
		return random.nextDouble();
	}
	
}
