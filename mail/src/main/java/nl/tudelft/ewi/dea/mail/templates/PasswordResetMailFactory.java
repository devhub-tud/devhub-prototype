package nl.tudelft.ewi.dea.mail.templates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.StringWriter;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class PasswordResetMailFactory {

	public final static String SUBJECT_TEXT = "DevHub reset password";
	private final VelocityEngine engine;
	private final MailProperties mailProps;

	@Inject
	PasswordResetMailFactory(VelocityEngine engine, MailProperties props) {
		this.engine = engine;
		this.mailProps = props;
	}

	public SimpleMessage newMail(String mailAddress, String verifyUrl) {
		checkArgument(!isNullOrEmpty(verifyUrl));

		String body = generateBody(verifyUrl);

		SimpleMessage message = new SimpleMessage(mailAddress, SUBJECT_TEXT, body, mailProps.from);

		return message;
	}

	private String generateBody(String verifyUrl) {
		VelocityContext context = new VelocityContext();
		context.put("link", verifyUrl);

		Template t = engine.getTemplate("passwordResetMail.txt");

		StringWriter writer = new StringWriter();

		t.merge(context, writer);

		String body = writer.toString();
		return body;
	}
}
