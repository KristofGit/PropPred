package Histograms;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import Randomization.RandUtils;

public class HistogramEnum<T extends Enum<T>> {

	private final Map<T, Long> distribution;
	private long totalCount = -1;
	private final Class<T> type;
	
	public HistogramEnum(Class<T> type, List<T> enums)
	{
		this.type = type;
		distribution= new EnumMap<>(type);
		
		if(enums == null)
		{
			enums = new ArrayList<>();
		}
		
		prepareHistorgramDistribution(type, enums);
	}
		
	private void prepareHistorgramDistribution(Class<T> type, List<T> enums)
	{
		T[] enumConstants = type.getEnumConstants();
		
		for(T eachEnum : enumConstants)
		{
			//count number of occurrences
			long amount = enums.stream().filter(x->x==eachEnum).count();
			distribution.put(eachEnum, amount);
		}		
	}
	
	private long getTotalAmounttOfOccurrences()
	{
		if(totalCount == -1)
		{
			totalCount = distribution.values().stream().mapToLong(Long::longValue).sum();
		}
		
		return totalCount;
	}
	
	public T getRandom()
	{
		T result = null;
		
		double randomValue = RandUtils.getDouble();
		long totalAmount = getTotalAmounttOfOccurrences();
		
		long relativeMinAmount = (long)(totalAmount*randomValue);
		
		T[] enumConstants = type.getEnumConstants();
		
		long amountSum = 0;
		
		for(T eachEnum : enumConstants)
		{
			long individualAmount = distribution.get(eachEnum);
			amountSum=amountSum+individualAmount;
			
			if(amountSum>relativeMinAmount)
			{
				result = eachEnum;
				break;
			}
		}
		
		return result;		
	}
	
}
