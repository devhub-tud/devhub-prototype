package nl.tudelft.ewi.dea.mail.internals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import nl.tudelft.ewi.dea.mail.MailException;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MailQueueTakerTest {

	@Spy private LinkedBlockingQueue<SimpleMessage> mailQueue;
	@Mock private MailProperties mailProps;
	private final Session session = Session.getDefaultInstance(new Properties());
	@Mock private Transport transport;
	private MailQueueTaker mQueueTaker;

	@Before
	public void setup() {
		mQueueTaker = new MailQueueTaker(mailQueue, transport, mailProps, session);
	}

	@Test
	public void whenConnectionIsTestedTheTakerOpensAndClosesTheTransport() throws MessagingException {
		mQueueTaker.testConnection();
		InOrder order = inOrder(mailQueue, transport, mailProps);
		order.verify(transport).connect(anyString(), anyString(), anyString());
		order.verify(transport).close();
		order.verifyNoMoreInteractions();
	}

	@Test(expected = MailException.class)
	public void whenConnectionTestFailsTheMethodInterrups() throws MessagingException {
		doThrow(new MessagingException()).when(transport).connect(anyString(), anyString(), anyString());
		mQueueTaker.testConnection();
	}

	@Test
	public void whenTheRunnerStartsItDrainsTheQueueAndSendsTheMessage() throws InterruptedException, MessagingException {
		SimpleMessage m1 = newMessageMock();
		SimpleMessage m2 = newMessageMock();
		SimpleMessage m3 = newMessageMock();
		mailQueue.addAll(Arrays.asList(m1, m2, m3));

		when(mailQueue.take()).thenReturn(m1).thenThrow(new InterruptedException());

		mQueueTaker.run();

		InOrder order = inOrder(mailQueue, transport);
		order.verify(mailQueue).take();
		order.verify(mailQueue).drainTo(any(Collection.class));

		order.verify(transport).connect(anyString(), anyString(), anyString());
		order.verify(transport).sendMessage(m1.asMimeMessage(session), null);
		order.verify(transport).sendMessage(m2.asMimeMessage(session), null);
		order.verify(transport).sendMessage(m3.asMimeMessage(session), null);
		order.verify(transport).close();

		order.verify(mailQueue).take();
		order.verify(mailQueue).size();
		order.verifyNoMoreInteractions();

	}

	private SimpleMessage newMessageMock() {
		SimpleMessage smsg = mock(SimpleMessage.class);
		MimeMessage mimeMock = mock(MimeMessage.class);
		when(smsg.asMimeMessage(any(Session.class))).thenReturn(mimeMock);
		return smsg;
	}
}
