package nl.tudelft.ewi.dea.mail.templates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Map;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

public class VerifyRegistrationMailFactory extends AbstractMailFactory {

	public final static String SUBJECT_TEXT = "DevHub registration conformation";
	private final MailProperties mailProps;

	@Inject
	VerifyRegistrationMailFactory(VelocityEngine engine, MailProperties props) {
		super(engine);
		this.mailProps = props;
	}

	public SimpleMessage newMail(String mailAddress, String verifyUrl) {
		checkArgument(!isNullOrEmpty(mailAddress));
		checkArgument(!isNullOrEmpty(verifyUrl));

		Map<String, Object> entities = ImmutableMap.of(
				"link", (Object) verifyUrl,
				"mailAddress", (Object) mailAddress);

		String body = buildTemplate("verifyRegistrationMail.txt", entities);

		SimpleMessage message = new SimpleMessage(mailAddress, SUBJECT_TEXT, body, mailProps.from);

		return message;
	}

}
