package nl.tudelft.ewi.dea.mail.templates;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

public class ServiceRegistrationMailFactory extends AbstractMailFactory {

	private static final String SUBJECT_TEXT = "Service registration";
	private final MailProperties props;

	@Inject
	ServiceRegistrationMailFactory(VelocityEngine engine, MailProperties props) {
		super(engine);
		this.props = props;
	}

	public SimpleMessage sendServiceRegistrationMail(String email, String userName, String password, String serviceName) {
		checkArgument(!isNullOrEmpty(email));
		checkArgument(!isNullOrEmpty(serviceName));

		String body = buildTemplate("serviceRegistrationMail.txt",
				ImmutableMap.<String, Object> of("email", email
						, "userName", userName
						, "serviceName", serviceName
						, "password", password));

		return new SimpleMessage(email, SUBJECT_TEXT, body, props.getFrom());
	}

}
