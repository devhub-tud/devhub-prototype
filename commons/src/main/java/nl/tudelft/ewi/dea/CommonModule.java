package nl.tudelft.ewi.dea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CommonModule extends AbstractModule {

	public static final int MINIMUM_THREADPOOL_SIZE = 3;

	@Override
	protected void configure() {
		bind(ExecutorService.class).to(ScheduledExecutorService.class);
		bind(ScheduledExecutorService.class).toInstance(Executors.newScheduledThreadPool(MINIMUM_THREADPOOL_SIZE));
	}

	/**
	 * @return an {@link ObjectMapper} that serialized all fields.
	 */
	@Provides
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper;
	}

}
