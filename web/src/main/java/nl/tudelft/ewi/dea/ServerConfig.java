package nl.tudelft.ewi.dea;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import lombok.EqualsAndHashCode;
import lombok.experimental.Value;
import nl.tudelft.ewi.dea.dao.DatabaseProperties;
import nl.tudelft.ewi.dea.mail.MailProperties;

@Value
@Singleton
@EqualsAndHashCode(callSuper = false)
public final class ServerConfig extends JsonConfigFile {

	private final String gitoliteUrl = null;
	private final String gitHost = null;
	private final String sshPassPhrase = null;
	private final String jenkinsUrl = null;
	private final String sonarUrl = null;
	private final String webUrl = null;
	private final MailProperties mailConfig = null;
	private final DatabaseProperties dbConfig = null;

	@Override
	public void verifyConfig() {
		notNullNorEmpty(gitHost, "Git url must be filled in");
		notNullNorEmpty(gitoliteUrl, "Gitolite url must be filled in");
		notNullNorEmpty(jenkinsUrl, "Jenkins url must be filled in");
		notNullNorEmpty(sonarUrl, "Sonar url must be filled in");
		checkNotNull(mailConfig, "Mail must be configured");
		mailConfig.verifyConfig();
		dbConfig.verifyConfig();
	}

}
