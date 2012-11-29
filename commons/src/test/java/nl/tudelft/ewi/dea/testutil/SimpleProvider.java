package nl.tudelft.ewi.dea.testutil;

import javax.inject.Provider;

/**
 * A {@link Provider} that just returns the instance givin in it's contructor.
 * 
 * <p>
 * Useful for testing so you dont have to write:
 * 
 * <pre>
 * {@literal @}Mock private {@link Provider}{@literal <}MyThing> mythingmock
 * {@literal @}Mock private MyThing instance 
 * when(mytthingmock.get()).thenReturn(instance)
 * </pre>
 */
public final class SimpleProvider<T> implements Provider<T> {

	public static <T> SimpleProvider<T> forInstance(T instance) {
		return new SimpleProvider<T>(instance);
	}

	private final T returnType;

	private SimpleProvider(T returnType) {
		this.returnType = returnType;
	}

	@Override
	public T get() {
		return returnType;
	}

	@Override
	public String toString() {
		return "Provider for " + returnType.getClass();
	}
}
