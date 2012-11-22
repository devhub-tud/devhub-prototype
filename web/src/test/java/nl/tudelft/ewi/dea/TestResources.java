package nl.tudelft.ewi.dea;

import nl.tudelft.ewi.dea.di.ServerStartupListener;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestResources {

	public static final ServerConfig SERVER_CONFIG = serverConfig();

	private static ServerConfig serverConfig() {
		ObjectMapper mapper = new CommonModule().objectMapper();
		return ServerStartupListener.readServerConfig(mapper);
	}

}
