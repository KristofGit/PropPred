package TraceSimilarity;

import java.util.List;
import java.util.Objects;

import BaseObject.Equal;

public class DamerauLevenshtein_activity_and_dur{


	//leave all this at one or adapt the DamerauLevenshteinSimilarity class normalization too
	private static int costDelete = 2;
	private static int costSubstitue = 3;
	private static int costTranspose = 2; //assume that transposition isnt that "bad" as it could stem from parallel executions
	private static int costInsert = 1;

	public DamerauLevenshtein_activity_and_dur()
	{
		
	}
	
	public DamerauLevenshtein_activity_and_dur(int generalCost)
	{
		costDelete = costSubstitue = costTranspose = costInsert = generalCost;
	}
	
	public static interface ItemEqualityNew<T>
	{
		public boolean isMajorSimiar(T one, T two);
		
		public boolean isMinorSimilar(T one, T two);
		
		public double minorSimilarCost(T one, T two, double totalPossibleCost);
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
	
	public static <T> double distance(ItemEqualityNew<T> equality, List<T> source, List<T> target)
	{
		if (source == null || target == null || equality == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
		
		int sourceLength = source.size();
        int targetLength = target.size();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        
        double[][] dist = new double[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
            	
            	boolean isMajorSimilar = equality.isMajorSimiar(source.get(i - 1), target.get(j - 1));
            	
            	double cost = 0;
            	
            	if(isMajorSimilar && !equality.isMinorSimilar(source.get(i - 1), target.get(j - 1)))
            	{
            		cost = equality.minorSimilarCost(source.get(i - 1), target.get(j - 1), costSubstitue);
            	}
            	else if(!isMajorSimilar)
            	{
            		cost = costSubstitue;
            	}
                
                dist[i][j] = Math.min(
                		Math.min(dist[i - 1][j] + costDelete, //delete
                				dist[i][j - 1] + costInsert), //insert
                		dist[i - 1][j - 1] + cost); //substitute
                
                //transpose
                if (i > 1 && j > 1 &&
                		equality.isMajorSimiar(source.get(i - 1), target.get(j - 2)) &&
                		equality.isMajorSimiar(source.get(i - 2), target.get(j - 1))) {
                	
                	cost = costTranspose;

                	if(!equality.isMinorSimilar(source.get(i - 1), target.get(j - 2)))
					{
                		cost = Math.max(equality.minorSimilarCost(source.get(i - 1), target.get(j - 2), costTranspose),
                				equality.minorSimilarCost(source.get(i - 2), target.get(j - 1), costTranspose));
					}
                	
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        
        return dist[sourceLength][targetLength];		
	}	
}
