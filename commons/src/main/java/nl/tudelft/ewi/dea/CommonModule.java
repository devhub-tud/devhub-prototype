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

	@Override
	protected void configure() {
		bind(ExecutorService.class).to(ScheduledExecutorService.class);
		bind(ScheduledExecutorService.class).toInstance(Executors.newScheduledThreadPool(3));
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
