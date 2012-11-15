package nl.tudelft.ewi.dea.mail;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.experimental.Value;
import nl.tudelft.ewi.dea.JsonConfigFile;

@Value
@Immutable
@EqualsAndHashCode(callSuper = false)
public final class MailProperties extends JsonConfigFile {

	private static final int MAX_PORT_NUMBER = 65535;
	private static final int DEFAULT_SMTP_PORT = 25;

	private final String host = "localhost";
	private final String from = null;
	private final String user = null;
	private final String password = null;
	private final boolean ssl = false;
	private final int port = DEFAULT_SMTP_PORT;
	private final boolean auth = false;

	@Override
	public void verifyConfig() {
		checkArgument(port > 0 && port <= MAX_PORT_NUMBER, port + " is not a valid port number");
	}
}
