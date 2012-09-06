package nl.tudelft.ewi.dea.mail;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Immutable
@EqualsAndHashCode
public class MailProperties {

	private static final int MAX_PORT_NUMBER = 65535;

	/**
	 * Create a new {@link MailProperties} that uses authorization to conenct to
	 * the SMPT server.
	 * 
	 * @param host The hostname of the SMPT server.
	 * @param user The username for the SMPT server.
	 * @param from The from address used.
	 * @param ssl If the SMPT server uses SSL.
	 * @param port The port the SMPT server runs on.
	 * @return A verified mail configuration that uses authorization to login to
	 *         the mail server..
	 */
	public static MailProperties newWithAuth(String host, String user, String password, String from, boolean ssl, int port) {
		return new MailProperties(user, host, from, password, ssl, port, true);
	}

	/**
	 * Create a new {@link MailProperties} that logs in on the SMTP server
	 * anonymously.
	 * 
	 * @param host The hostname of the SMPT server.
	 * @param from The from address used.
	 * @param ssl If the SMPT server uses SSL.
	 * @param port The port the SMPT server runs on.
	 * @return A verified mail configuration.
	 */
	public static MailProperties newAnon(String host, String from, boolean ssl, int port) {
		return new MailProperties("", host, from, "", ssl, port, false);
	}

	public final String host;
	public final String from;
	public final String user;
	public final String password;
	public final boolean ssl;
	public final int port;
	public final boolean auth;

	private MailProperties(String user, String host, String from, String password, boolean ssl, int port, boolean auth) {
		this.user = checkNotNull(user);
		this.host = checkNotNull(host);
		this.from = checkNotNull(from);
		this.password = checkNotNull(password);
		this.ssl = checkNotNull(ssl);
		checkArgument(port > 0 && port <= MAX_PORT_NUMBER, "Not a valid port number");
		this.port = port;
		this.auth = auth;
	}

}
