package DataStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import Histograms.CustomClass;

public class Propabilities<T extends CustomClass<?>> {
 
	private Map<T, AtomicInteger> distributionOfCases = new HashMap<>();
	
	private Map<T, Double> propabilities = new HashMap<>();
	
	private boolean mustCalculatePropabilities = false;
	
	public void addHistoricBehavior(T behaviorClass)
	{
		AtomicInteger count = distributionOfCases.get(behaviorClass);
		
		if(count != null)
		{
			count.incrementAndGet();
		}
		else 
		{
			distributionOfCases.put(behaviorClass, new AtomicInteger(1));
		}
		
		mustCalculatePropabilities = true;
	}
	
	private void calculatePropabilites()
	{
		//recalculate propabilities only if necessary
		if(mustCalculatePropabilities)
		{
			propabilities.clear();
			
			int totalCount = distributionOfCases.values().stream().mapToInt(x->x.get()).sum();
			
			for(T eachClass : distributionOfCases.keySet())
			{
				int individualCount = distributionOfCases.get(eachClass).get();
				
				double relativeCount = ((double)individualCount)/totalCount;
				
				propabilities.put(eachClass, relativeCount);
			}
			
			mustCalculatePropabilities = false;
		}
	}
	
	public T getMostPropableClass()
	{
		calculatePropabilites();
		
		
		double maxPropability = Double.MIN_VALUE;
		T maxPropClass = null;
		
		for(T eachClass : distributionOfCases.keySet())
		{
			double propability = distributionOfCases.get(eachClass).get();

			if(propability > maxPropability)
			{
				maxPropability = propability;
				maxPropClass = eachClass;
			}
		}
		
		
		return maxPropClass;
	}
}
