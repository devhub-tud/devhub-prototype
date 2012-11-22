package nl.tudelft.ewi.dea;

import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;

import lombok.EqualsAndHashCode;
import lombok.experimental.Value;
import nl.tudelft.ewi.dea.dao.DatabaseProperties;
import nl.tudelft.ewi.dea.mail.MailProperties;

@Value
@Singleton
@EqualsAndHashCode(callSuper = false)
public final class ServerConfig extends JsonConfigFile {

	private final String webUrl = null;
	private final MailProperties mailConfig = null;
	private final DatabaseProperties dbConfig = null;
	private final Map<String, Map<String, Properties>> services = null;

	@Override
	public void verifyConfig() {
		checkNotNull(mailConfig, "Mail must be configured");
		mailConfig.verifyConfig();
		dbConfig.verifyConfig();
	}

}
