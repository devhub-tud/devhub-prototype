package nl.tudelft.ewi.dea.dao;

import java.util.Properties;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.experimental.Value;
import nl.tudelft.ewi.dea.ConfigurationException;
import nl.tudelft.ewi.dea.JsonConfigFile;

@Value
@Immutable
@EqualsAndHashCode(callSuper = false)
public class DatabaseProperties extends JsonConfigFile {

	private final String dburl = null;
	private final String user = null;
	private final String password = null;
	private final String persistanceUnit = null;

	@Override
	public void verifyConfig() throws ConfigurationException {
		notNullNorEmpty(dburl, "Database URL must be specified");
		notNullNorEmpty(user, "Database Username must be specified");
		checkArgument(password != null, "Database Password must be specified");
	}

	public Properties asJpaProperties() {
		Properties props = new Properties();
		props.setProperty("javax.persistence.jdbc.url", dburl);
		props.setProperty("javax.persistence.jdbc.user", user);
		props.setProperty("javax.persistence.jdbc.password", password);
		return props;
	}

	public String getPersistanceUnit() {
		return persistanceUnit;
	}
}
