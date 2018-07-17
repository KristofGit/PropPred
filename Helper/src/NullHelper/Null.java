package NullHelper;

public class Null {

	public static <T> T coalesce(T ...items) {
	    for(T i : items) if(i != null) return i;
	    return null;
	}
	
	public static <T> boolean isAnyNull(T ...items)
	{
	    for(T i : items) if(i == null) return true;
	    return false;
	}
}
