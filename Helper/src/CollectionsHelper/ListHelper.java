package CollectionsHelper;

import java.util.List;

public class ListHelper {

	public static boolean isNullOrEmpty(List<?> list)
	{
		return list == null || list.isEmpty();
	}
	
	public static <T> T getLast(List<T> list)
	{
		T result = null;
		
		if(list == null  || list.isEmpty())
		{
			return result;
		}
		
		result = get(list, list.size()-1);
		
		return result;
	}
	
	public static <T> T get(List<T> list, int index)
	{
		T result = null;
		
		if(list == null  || list.isEmpty() || index<0 || index>list.size()-1)
		{
			return result;
		}
		
		result = list.get(index);
				
		return result;
	}
}
