package nl.tudelft.ewi.dea.mail;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.experimental.Value;
import nl.tudelft.ewi.dea.JsonConfigFile;

@Value
@Immutable
@EqualsAndHashCode(callSuper = false)
public final class MailProperties extends JsonConfigFile {

	private static int MAX_PORT_NUMBER = 65535;

	private final String host = null;
	private final String from = null;
	private final String user = null;
	private final String password = null;
	private final Boolean ssl = null;
	private final Integer port = null;
	private final Boolean auth = null;

	@Override
	public void verifyConfig() {
		checkArgument(port > 0 && port <= MAX_PORT_NUMBER, port + " is not a valid port number");
	}
}
