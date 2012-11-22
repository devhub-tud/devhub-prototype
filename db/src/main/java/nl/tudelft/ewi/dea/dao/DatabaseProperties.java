package nl.tudelft.ewi.dea.dao;

import java.util.Properties;

import nl.tudelft.ewi.dea.ConfigurationException;
import nl.tudelft.ewi.dea.JsonConfigFile;

public class DatabaseProperties extends JsonConfigFile {

	private final String dburl;
	private final String user;
	private final String password;
	private final String persistanceUnit;

	@SuppressWarnings("unused")
	private DatabaseProperties() {
		this.dburl = null;
		this.user = null;
		this.password = null;
		this.persistanceUnit = null;
	}

	public DatabaseProperties(String dbUrl, String user, String password, String persistenceUnit) {
		this.dburl = dbUrl;
		this.user = user;
		this.password = password;
		this.persistanceUnit = persistenceUnit;
	}

	@Override
	public void verifyConfig() throws ConfigurationException {
		checkNotNull(dburl, "Database URL must be specified");
		checkNotNull(user, "Database Username must be specified");
		checkArgument(password != null, "Database Password must be specified");
	}

	public Properties asJpaProperties() {
		Properties props = new Properties();
		props.setProperty("javax.persistence.jdbc.url", dburl);
		props.setProperty("javax.persistence.jdbc.user", user);
		props.setProperty("javax.persistence.jdbc.password", password);
		return props;
	}

	public String getDburl() {
		return dburl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getPersistanceUnit() {
		return persistanceUnit;
	}

}
