package nl.tudelft.ewi.dea;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import nl.tudelft.ewi.dea.di.ServerStartupListener;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerConfigTest {

	private final ObjectMapper mapper = new CommonModule().objectMapper();

	@Test
	public void verifyConfigIsReadbleAndCorrect() {
		ServerConfig config = ServerStartupListener.readServerConfig(mapper);
		config.verifyConfig();
	}

	@Test
	public void verifySampleConfigIsCorrect() throws IOException {
		File sampleConfig = new File("env/serverconfig.json.example");
		assertThat(sampleConfig.exists(), is(true));
		ServerConfig config = mapper.readValue(sampleConfig, ServerConfig.class);
		config.verifyConfig();
	}

}
