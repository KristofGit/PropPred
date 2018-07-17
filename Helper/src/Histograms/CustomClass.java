package Histograms;

public abstract class CustomClass<T>  {

	protected final T lowerBound;
	protected final T upperBound;
	
	public CustomClass(T lowerBound, T upperBound)
	{
		this.lowerBound =lowerBound;
		this.upperBound = upperBound;
	}
	
	public CustomClass(T oneBound)
	{
		this.lowerBound = oneBound;
		this.upperBound = oneBound;
	}
	
	public abstract boolean belongsToThisClass(T value);
	public abstract T getRandomRepresentation();
	
    public abstract boolean equals(Object other);
    public abstract int hashCode();
}
