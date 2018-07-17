package Histograms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import BaseObject.Tuple;
import Configuration.ConfigEvaluation;
import Randomization.RandUtils;

public class HistogramCustomClasses<Y,T extends CustomClass<Y>> {

	private final Map<T, Long> distribution;
	private long totalCount = -1;
	private long mostFrequent = -1;
	
	public HistogramCustomClasses(List<T> potentialClasses, List<Y> values, List<Double> weights)
	{
		distribution= new HashMap<>();
		
		if(values == null)
		{
			values = new ArrayList<>();
		}
		
		prepareHistorgramDistribution(potentialClasses, values, weights);
	}
	
	public HistogramCustomClasses(List<T> potentialClasses, List<Y> values)
	{
		distribution= new HashMap<>();
		
		if(values == null)
		{
			values = new ArrayList<>();
		}
		
		prepareHistorgramDistribution(potentialClasses, values, null);
	}
		
	private void prepareHistorgramDistribution(List<T> potentialClasses, List<Y> values, List<Double> weights)
	{
		if(weights == null)
		{
			for(T eachCustomClass : potentialClasses)
			{
				long amount = values.stream().filter(x->eachCustomClass.belongsToThisClass(x)).count();
				distribution.put(eachCustomClass, amount);
			}
		}
		else
		{
			for(T eachCustomClass : potentialClasses)
			{
				BigDecimal amount = new BigDecimal("0");

				for(int i=0;i<values.size();i++)
				{
					Y value = values.get(i);
					Double weight = weights.get(i);
					
					BigDecimal weightDec = new BigDecimal(weight);
					
					if(eachCustomClass.belongsToThisClass(value))
					{
						amount = amount.add(BigDecimal.ONE.multiply(weightDec));
					}
				}

				distribution.put(eachCustomClass, amount.setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
			}
		}
	}
	
	private long getMostFrequentOccurrenceCount()
	{
		if(mostFrequent == -1)
		{
			mostFrequent = distribution.values().stream().max(Long::compare).get();			
		}
		
		return mostFrequent;
	}
	
	private long getTotalAmounttOfOccurrences()
	{
		if(totalCount == -1)
		{
			totalCount = distribution.values().stream().mapToLong(Long::longValue).sum();
		}
		
		return totalCount;
	}
	
	public Y getMostLikely()
	{	
		if(distribution.isEmpty())
		{
			return null;
		}
		
		return distribution.entrySet().stream().max((x,y)-> {
			return Long.compare(x.getValue(), y.getValue());
		}).map(x->x.getKey()).get().getRandomRepresentation();		
	}
	
	/*public List<Y> getMostLikelyComplex(int numberOfMostLikely)
	{
		distribution.values().stream().sorted(Comparator.reverseOrder()).limit(numberOfMostLikely).collect(Collectors.t);
		
		
	}*/
	
	public List<Y> getMostLikelyComplex()
	{	
		if(distribution.isEmpty())
		{
			return new ArrayList<>();
		}
		
		long maxOccurrence = distribution.entrySet().stream().map(x->x.getValue()).max(Long::compare).get();
		
		double minRelevantOccurrence = maxOccurrence - maxOccurrence*ConfigEvaluation.histMostLikelySeparation;
		
		List<Tuple<T, Long>> distrubution = distribution.entrySet().stream().filter(x->x.getValue()>=minRelevantOccurrence).map(x->{
			return new Tuple<T, Long>(x.getKey(), x.getValue());
		}).collect(Collectors.toList());
		
		List<Y> result = new ArrayList<Y>();
		
		for(Tuple<T, Long> each : distrubution)
		{
			for(long i=0;i<each.y;i++)
			{
				result.add(each.x.getRandomRepresentation());
			}
		}
		
		return result;
	}
	
	public boolean isLikelihoodOfMostLikelyOneAbove(double comparisonLikelihood)
	{
		return getMostLikelyLikelihood() > comparisonLikelihood;
	}
	
	public double getMostLikelyLikelihood()
	{
		if(distribution.isEmpty())
		{
			return 0;
		}
		
		long totalAmount = getTotalAmounttOfOccurrences();
		long mostFrequente = getMostFrequentOccurrenceCount();
		
		return (((double)mostFrequente)/totalAmount);
	}
	
	public Y getRandom()
	{
		Y result = null;
		
		double randomValue = RandUtils.getDouble();
		long totalAmount = getTotalAmounttOfOccurrences();
		
		long relativeMinAmount = (long)(totalAmount*randomValue);
		
		Set<T> allCalsses = distribution.keySet();
		
		long amountSum = 0;
		
		for(T eachClass : allCalsses)
		{
			long individualAmount = distribution.get(eachClass);
			amountSum=amountSum+individualAmount;
			
			if(amountSum>relativeMinAmount)
			{
				result = eachClass.getRandomRepresentation();
				break;
			}
		}
		
		return result;		
	}
}
