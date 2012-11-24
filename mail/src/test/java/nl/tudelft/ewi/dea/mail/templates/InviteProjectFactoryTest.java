package nl.tudelft.ewi.dea.mail.templates;

import static nl.tudelft.ewi.dea.mail.internals.CommonTestData.MAIL_PROPS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.mail.TestFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class InviteProjectFactoryTest {

	private InviteProjectFactory factory;

	@Before
	public void setup() {
		factory = new InviteProjectFactory(TestFactory.getVelocityEngine(), MAIL_PROPS);
	}

	@Test
	public void verifyThatInviteMailIsCreatedCorrectly() throws IOException {
		String mailAddress = "test@example.com";
		SimpleMessage generatedMessage = factory.sendProjectInvite(mailAddress, "John", "SampleProject", "html://verify.this.com");
		String expextedTemplate = Resources.toString(Resources.getResource("inviteToProjectMailSample.txt"), Charsets.UTF_8);

		assertThat(generatedMessage.getBody(), is(expextedTemplate));
		assertThat(generatedMessage.getFrom(), is(MAIL_PROPS.getFrom()));
		assertThat(generatedMessage.to.size(), is(1));
		assertThat(generatedMessage.to, hasItem(mailAddress));
	}
}
