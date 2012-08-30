package nl.tudelft.ewi.dea.mail;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.StringWriter;

import javax.inject.Inject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VerifyRegistrationMailFactory {

	private VelocityEngine engine;

	@Inject
	VerifyRegistrationMailFactory(VelocityEngine engine) {
		this.engine = engine;
	}

	String newMail(String mailAddress, String verifyUrl) {
		checkArgument(!isNullOrEmpty(mailAddress));
		checkArgument(!isNullOrEmpty(verifyUrl));

		VelocityContext context = new VelocityContext();
		context.put("link", verifyUrl);
		context.put("mailAddress", mailAddress);

		Template t = engine.getTemplate("verifyRegistrationMail.txt");

		StringWriter writer = new StringWriter();

		t.merge(context, writer);

		return writer.toString();
	}
}
