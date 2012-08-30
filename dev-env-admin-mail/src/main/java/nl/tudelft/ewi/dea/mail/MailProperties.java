package nl.tudelft.ewi.dea.mail;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MailProperties {

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
		checkArgument(port > 0 && port <= 65535, "Not a valid port number");
		this.port = port;
		this.auth = auth;
	}

	@Override
	public String toString() {
		return "MailProperties [host=" + host + ", from=" + from + ", user=" + user + ", password=" + password + ", ssl=" + ssl + ", port=" + port + ", auth=" + auth + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (auth ? 1231 : 1237);
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result + (ssl ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MailProperties other = (MailProperties) obj;
		if (auth != other.auth) {
			return false;
		}
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		if (ssl != other.ssl) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}

}
