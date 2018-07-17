package Entropy;

import java.util.HashMap;
import java.util.Map;

public class EntropyUtils {

	//http://blog.dkbza.org/2007/05/scanning-data-for-entropy-anomalies.html
	//https://stackoverflow.com/questions/990477/how-to-calculate-the-entropy-of-a-file
	
	public static double ShannonEntropy(String s)
	{
		if(s == null)
		{
			return 0;
		}
		
	    final Map<Character, Integer> map = new HashMap<>();
	    for (char c : s.toCharArray())
	    {
	        if (!map.containsKey(c))
	            map.put(c, 1);
	        else
	        	map.put(c,(map.get(c)+1));
	    }

	    double result = 0;
	    int len = s.length();
	    for (Integer eachValue : map.values())
	    {
	        double frequency = (double)eachValue / len;
	        result -= frequency * (Math.log(frequency) / Math.log(2));
	    }

	    return result;
	}
}
