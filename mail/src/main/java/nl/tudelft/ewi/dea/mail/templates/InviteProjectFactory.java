package nl.tudelft.ewi.dea.mail.templates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

public class InviteProjectFactory extends AbstractMailFactory {

	private static final String SUBJECT_TEXT = "Project invitation";
	private final MailProperties props;

	@Inject
	InviteProjectFactory(VelocityEngine engine, MailProperties props) {
		super(engine);
		this.props = props;
	}

	public SimpleMessage sendProjectInvite(String email, String displayName, String projectName,
			String url) {
		return generateInviteMail(email, displayName, projectName, url, "inviteToProjectMail.txt");

	}

	private SimpleMessage generateInviteMail(String email, String displayName, String projectName, String url, String template) {
		checkArgument(!isNullOrEmpty(email));
		checkArgument(!isNullOrEmpty(projectName));

		String body = buildTemplate(template,
				ImmutableMap.of("email", (Object) email
						, "userName", (Object) displayName
						, "projectName", (Object) projectName
						, "url", (Object) url));

		return new SimpleMessage(email, SUBJECT_TEXT, body, props.getFrom());
	}

	public SimpleMessage sendDevHubInvite(String email, String fromDisplayName, String projectName, String publicUrl) {
		return generateInviteMail(email, fromDisplayName, projectName, publicUrl, "inviteToDevHubMail.txt");
	}
}
