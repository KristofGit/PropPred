package TraceSimilarity;

import java.util.List;

import TraceSimilarity.DamerauLevenshtein_activity_and_dur.ItemEqualityNew;
import TraceSimilarity.DamerauLevenshtein_activity_only.ItemEquality;

public class DamerauLevenshteinSimilarity {

	public static <T> double similarity(ItemEquality<T> equality, List<T> source, List<T> target)
	{
		if (source == null || target == null || equality == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
		
		int maxLength = Math.max(source.size(), target.size());
		
		int distance = DamerauLevenshtein_activity_only.distance(equality, source, target);
		
		return 1.0 -(((double)distance)/maxLength);
	}
}
