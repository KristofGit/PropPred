package Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InterquartileRange {

	private static int medianIndex(int l, int r)
	{
	    int n = r - l + 1;
	    n = (n + 1) / 2 - 1;
	    return n + l;
	}
	
	public static long calculateIQR(List<Long> values)
	{
		List<Long> valuesSorted = new ArrayList<>(values);
		Collections.sort(valuesSorted);
	
		try
		{
			
			int mid_index = medianIndex(0, valuesSorted.size());
			
			long Q1 = valuesSorted.get(medianIndex(0, mid_index));
			
			long Q3 = valuesSorted.get(medianIndex(mid_index+1, valuesSorted.size()));

			return Q3-Q1;
		}
		catch(Exception e)
		{
			boolean test = false;
		}
		return 0;
	}
	
}
