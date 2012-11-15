package nl.tudelft.ewi.dea;

import nl.tudelft.ewi.dea.di.ServerStartupListener;

import org.junit.Test;

public class ServerConfigTest {

	@Test
	public void verifyConfigIsReadbleAndCorrect() {
		ServerConfig config = new ServerStartupListener().readServerConfig();
		config.verifyConfig();
	}

}
