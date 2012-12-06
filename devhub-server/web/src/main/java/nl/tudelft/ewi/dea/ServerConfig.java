package nl.tudelft.ewi.dea;

import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nl.tudelft.ewi.dea.dao.DatabaseProperties;
import nl.tudelft.ewi.dea.mail.MailProperties;

import com.google.common.base.Preconditions;

@Getter
@ToString
@Singleton
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class ServerConfig extends JsonConfigFile {

	private String webUrl;
	private String feedbackEmailAddress;
	private MailProperties mailConfig;
	private DatabaseProperties dbConfig;
	private Map<String, Map<String, Properties>> services;

	@Override
	public void verifyConfig() {
		Preconditions.checkNotNull(mailConfig, "Mail must be configured");
		mailConfig.verifyConfig();
		dbConfig.verifyConfig();
	}

}
