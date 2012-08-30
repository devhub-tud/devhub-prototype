package nl.tudelft.ewi.dea.mail.templates;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class VerifyRegistrationMailFactoryTest {

	private VerifyRegistrationMailFactory factory;

	@Before
	public void setup() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		factory = new VerifyRegistrationMailFactory(engine);
	}

	@Test
	public void test() throws IOException {
		String generatedTemplate = factory.newMail("test@user.com", "html://verify.this.com");

		String expextedTemplate = Resources.toString(Resources.getResource("verifyRegistrationMailSample.txt"), Charsets.UTF_8);
		assertThat(generatedTemplate, is(expextedTemplate));
	}
}
