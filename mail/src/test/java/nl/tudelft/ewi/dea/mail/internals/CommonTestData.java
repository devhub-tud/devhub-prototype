package nl.tudelft.ewi.dea.mail.internals;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.ManualMailTest;
import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.mail.internals.UnsentMail;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonTestData {

	private static final Logger LOG = LoggerFactory.getLogger(CommonTestData.class);

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
			LOG.error("Could not initialize", ex);
			throw ex;
		}
	}

	public static UnsentMail newMessageMock(int id) {
		SimpleMessage smsg = mock(SimpleMessage.class);
		MimeMessage mimeMock = mock(MimeMessage.class);
		when(smsg.asMimeMessage(any(Session.class))).thenReturn(mimeMock);
		return new UnsentMail(id, smsg);
	}
}
