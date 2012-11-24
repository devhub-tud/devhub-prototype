package nl.tudelft.ewi.dea.mail;

import static nl.tudelft.ewi.dea.mail.internals.CommonTestData.SAMPLE_MESSAGE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class SimpleMessageTest {

	@Test(expected = NullPointerException.class)
	public void messageDoesntAllowNullSubject() {
		new SimpleMessage(SAMPLE_MESSAGE.to, null, SAMPLE_MESSAGE.body, SAMPLE_MESSAGE.from);
	}

	@Test(expected = NullPointerException.class)
	public void messageDoesntAllowNullTo() {
		String to = null;
		new SimpleMessage(to, SAMPLE_MESSAGE.subject, SAMPLE_MESSAGE.body, SAMPLE_MESSAGE.from);
	}

	@Test(expected = NullPointerException.class)
	public void messageDoesntAllowNullCollection() {
		String to = null;
		new SimpleMessage(to, SAMPLE_MESSAGE.subject, SAMPLE_MESSAGE.body, SAMPLE_MESSAGE.from);
	}

	@Test(expected = IllegalArgumentException.class)
	public void messageDoesntAllowEmptyCollection() {
		Set<String> emptySet = Collections.emptySet();
		new SimpleMessage(emptySet, null, SAMPLE_MESSAGE.body, SAMPLE_MESSAGE.from);
	}

	@Test(expected = NullPointerException.class)
	public void messageDoesntAllowNullBody() {
		new SimpleMessage(SAMPLE_MESSAGE.to, SAMPLE_MESSAGE.body, null, SAMPLE_MESSAGE.from);
	}

	@Test(expected = NullPointerException.class)
	public void messageDoesntAllowNullFrom() {
		new SimpleMessage(SAMPLE_MESSAGE.to, SAMPLE_MESSAGE.body, SAMPLE_MESSAGE.body, null);
	}

	@Test
	public void mimeMessageIsAssembledCorrectly() throws MessagingException, IOException {
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = SAMPLE_MESSAGE.asMimeMessage(session);
		Address toAddress = new InternetAddress(SAMPLE_MESSAGE.to.iterator().next());
		assertThat(message.getAllRecipients().length, is(1));
		assertThat(message.getAllRecipients(), hasItemInArray(toAddress));

		Address from = new InternetAddress(SAMPLE_MESSAGE.from);
		assertThat(message.getFrom()[0], is(from));
		assertThat(message.getContent(), is(instanceOf(String.class)));
		assertThat((String) message.getContent(), is(SAMPLE_MESSAGE.body));
	}
}
