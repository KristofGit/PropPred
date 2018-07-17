package TraceSimilarity;

import java.util.List;
import java.util.Objects;

import BaseObject.Equal;

public class DamerauLevenshtein_activity_only{

	//leave all this at one or adapt the DamerauLevenshteinSimilarity class normalization too
	private static int costDelete = 1;
	private static int costSubstitue = 1;
	private static int costTranspose = 1; //assume that transposition isnt that "bad" as it could stem from parallel executions
	private static int costInsert = 1;

	public static interface ItemEquality<T>
	{
		public boolean isEqual(T one, T two);
	}
	
	public static int getMaxCost(List<?> source, List<?> target)
	{
		if(source == null || target == null)
		{
			return 0;
		}
			
		int maxCostPerChange = Math.max(costInsert, Math.max(costTranspose, Math.max(costTranspose, costSubstitue)));
		
		int maxNumberOfChanges = Math.max(source.size(), target.size());
		
		return maxCostPerChange * maxNumberOfChanges;
	}
	
	public static <T> int distance(ItemEquality<T> equality, List<T> source, List<T> target)
	{
		if (source == null || target == null || equality == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
		
		int sourceLength = source.size();
        int targetLength = target.size();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = equality.isEqual(source.get(i - 1), target.get(j - 1)) ? 0 : costSubstitue;
                
                dist[i][j] = Math.min(
                		Math.min(dist[i - 1][j] + costDelete, //delete
                				dist[i][j - 1] + costInsert), //insert
                		dist[i - 1][j - 1] + cost); //substitute
                
                //transpose
                if (i >= 2 && j >= 2 &&
                		equality.isEqual(source.get(i - 1), target.get(j - 2)) &&
                		equality.isEqual(source.get(i - 2), target.get(j - 1))) {
                	
                	
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + costTranspose);
                }
            }
        }
        
        return dist[sourceLength][targetLength];		
	}
	
}
