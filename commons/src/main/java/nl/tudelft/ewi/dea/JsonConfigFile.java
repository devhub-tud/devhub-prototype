package nl.tudelft.ewi.dea;

import com.google.common.base.Strings;

/**
 * Implemented by beans that represent a configuration in JSON format.
 * <p>
 * After the bean has be deserialized, the configuration can be checked with
 * {@link #verifyConfig()}.
 * </p>
 */
public abstract class JsonConfigFile {

	/**
	 * Verify the configuration.
	 * 
	 * @throws ConfigurationException when the configuration is incorrect.
	 */
	public abstract void verifyConfig() throws ConfigurationException;

	protected void notNullNorEmpty(String value, String errorMessage) throws ConfigurationException {
		checkArgument(Strings.emptyToNull(value) != null, errorMessage);
	}

	protected void checkNotNull(Object value, String errorMessage) throws ConfigurationException {
		checkArgument(value != null, errorMessage);
	}

	protected void checkArgument(boolean argument, String errorMessage) throws ConfigurationException {
		if (!argument) {
			throw new ConfigurationException(errorMessage);
		}
	}

}
