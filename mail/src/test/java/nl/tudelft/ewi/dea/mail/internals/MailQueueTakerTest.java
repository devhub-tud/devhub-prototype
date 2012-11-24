package nl.tudelft.ewi.dea.mail.internals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import nl.tudelft.ewi.dea.mail.CommonTestData;
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

import com.google.common.collect.ImmutableList;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricsRegistry;

@RunWith(MockitoJUnitRunner.class)
public class MailQueueTakerTest {

	@Spy private LinkedBlockingQueue<SimpleMessage> mailQueue;
	private MailProperties mailProps = CommonTestData.MAIL_PROPS;
	private final Session session = Session.getDefaultInstance(new Properties());
	@Mock private Transport transport;

	private MailQueueTaker mQueueTaker;

	@Before
	public void setup() {
		mQueueTaker = new MailQueueTaker(mailQueue, transport, mailProps, session, Metrics.defaultRegistry());
	}

	@Test
	public void whenConnectionIsTestedTheTakerOpensAndClosesTheTransport() throws MessagingException {
		mQueueTaker.testConnection();
		InOrder order = inOrder(mailQueue, transport);
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

	@Test(expected = MailException.class)
	public void whenAnExceptionOccursInRunItIsCatchedAndWrapped() throws InterruptedException {
		RuntimeException textExcp = new RuntimeException();
		mailQueue.add(newMessageMock());
		when(mailQueue.take()).thenThrow(textExcp);

		mQueueTaker.run();

	}

	@Test
	public void whenThereIsAProblemConnectingTheMessagesAreSentLater() throws MessagingException, InterruptedException {
		SimpleMessage message = newMessageMock();
		mailQueue.add(message);
		mQueueTaker = spy(new MailQueueTaker(mailQueue, transport, mailProps, session, Metrics.defaultRegistry()));
		SendFailedException exc = new SendFailedException();
		doThrow(exc).when(transport).connect(anyString(), anyString(), anyString());
		doNothing().when(mQueueTaker).tryAgainAfterDelay(any(ImmutableList.class), eq(exc));
		when(mailQueue.take()).thenReturn(message).thenThrow(new InterruptedException());

		mQueueTaker.run();

		verify(mQueueTaker).tryAgainAfterDelay(any(ImmutableList.class), eq(exc));
	}
}
