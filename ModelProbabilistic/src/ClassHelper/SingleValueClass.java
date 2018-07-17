package ClassHelper;

import java.time.Duration;
import java.util.Objects;

import Histograms.CustomClass;

public abstract class SingleValueClass<T> extends CustomClass<T> {
	
	public SingleValueClass(T value)
	{
		super(value);
	}
	
	@Override
	public boolean belongsToThisClass(T value) {
		return Objects.equals(value, lowerBound);
	}

	@Override
	public T getRandomRepresentation() {
		return lowerBound;
	}

	@Override
	public boolean equals(Object other) {

		if(other == null || !(other instanceof SingleValueClass))
		{
			return false;
		}
		
		SingleValueClass<T> convert = (SingleValueClass<T>)other;
		
		T otherValue = convert.lowerBound;
		T thisValue = lowerBound;
		
		return Objects.equals(otherValue, thisValue);
	}

	@Override
	public int hashCode() {
		return lowerBound.hashCode();
	}

}
