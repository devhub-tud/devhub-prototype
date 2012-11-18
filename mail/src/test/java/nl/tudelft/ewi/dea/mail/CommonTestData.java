package nl.tudelft.ewi.dea.mail;

import java.io.IOException;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.DevHubException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonTestData {

	public static final SimpleMessage SAMPLE_MESSAGE =
			new SimpleMessage("harry@potter.com", "test subject",
					"test body", "ron@potter.com");

	public static final MailProperties MAIL_PROPS = readMailProps();

	private static MailProperties readMailProps() {
		ObjectMapper mapper = new CommonModule().objectMapper();
		try {
			return mapper.readValue(ManualMailTest.class.getResourceAsStream("/mailconfig.json"), MailProperties.class);
		} catch (IOException e) {
			DevHubException ex = new DevHubException("Could not read classpath:/mailconfig.json", e);
			ex.printStackTrace();
			throw ex;
		}
	}

}
