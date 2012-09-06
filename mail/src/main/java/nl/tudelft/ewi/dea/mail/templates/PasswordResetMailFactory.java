package nl.tudelft.ewi.dea.mail.templates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

public class PasswordResetMailFactory extends AbstractMailFactory {

	public static final String SUBJECT_TEXT = "DevHub reset password";
	private final MailProperties mailProps;

	@Inject
	PasswordResetMailFactory(VelocityEngine engine, MailProperties props) {
		super(engine);
		this.mailProps = props;
	}

	public SimpleMessage newMail(String mailAddress, String verifyUrl) {
		checkArgument(!isNullOrEmpty(verifyUrl));

		String body = buildTemplate("passwordResetMail.txt", ImmutableMap.of("link", (Object) verifyUrl));

		SimpleMessage message = new SimpleMessage(mailAddress, SUBJECT_TEXT, body, mailProps.from);

		return message;
	}

}
