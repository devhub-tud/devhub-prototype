package nl.tudelft.ewi.dea.mail.internals;

import static nl.tudelft.ewi.dea.mail.internals.CommonTestData.newMessageMock;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Provider;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.dao.UnsentMailDao;
import nl.tudelft.ewi.dea.mail.MailException;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.testutil.SimpleProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.yammer.metrics.Metrics;

@RunWith(MockitoJUnitRunner.class)
public class MailQueueTakerTest {

	@Spy private LinkedBlockingQueue<UnsentMail> mailQueue;
	private final MailProperties mailProps = CommonTestData.MAIL_PROPS;
	private final Session session = Session.getDefaultInstance(new Properties());
	@Mock private Transport transport;
	@Mock UnsentMailDao unsentMailDao;
	private MailQueueTaker mQueueTaker;

	@Before
	public void setup() {
		Provider<ObjectMapper> mapperProv = SimpleProvider.forInstance(new CommonModule().objectMapper());
		mQueueTaker = spy(new MailQueueTaker(mailQueue, transport, mailProps, session, SimpleProvider.forInstance(unsentMailDao), mapperProv, Metrics.defaultRegistry()));
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
		UnsentMail m1 = newMessageMock(1);
		UnsentMail m2 = newMessageMock(2);
		UnsentMail m3 = newMessageMock(3);
		mailQueue.addAll(Arrays.asList(m1, m2, m3));

		when(mailQueue.take()).thenReturn(m1).thenThrow(new InterruptedException());

		mQueueTaker.run();

		InOrder order = inOrder(mailQueue, transport, unsentMailDao);
		order.verify(transport).connect(anyString(), anyString(), anyString());
		order.verify(transport).close();

		order.verify(mailQueue).take();
		order.verify(mailQueue).drainTo(Matchers.<Collection<UnsentMail>> any());

		order.verify(transport).connect(anyString(), anyString(), anyString());

		order.verify(transport).sendMessage(m1.getMessage().asMimeMessage(session), null);
		order.verify(unsentMailDao).remove(m1.getId());

		order.verify(transport).sendMessage(m2.getMessage().asMimeMessage(session), null);
		order.verify(unsentMailDao).remove(m2.getId());

		order.verify(transport).sendMessage(m3.getMessage().asMimeMessage(session), null);
		order.verify(unsentMailDao).remove(m3.getId());

		order.verify(transport).close();

		order.verify(mailQueue).take();
		order.verify(mailQueue).size();
		order.verifyNoMoreInteractions();

	}

	@Test(expected = MailException.class)
	public void whenAnExceptionOccursInRunItIsCatchedAndWrapped() throws InterruptedException {
		RuntimeException textExcp = new RuntimeException();
		mailQueue.add(newMessageMock(1));
		when(mailQueue.take()).thenThrow(textExcp);

		mQueueTaker.run();

	}

	@Test
	public void whenThereIsAProblemConnectingTheMessagesAreSentLater() throws MessagingException, InterruptedException {
		UnsentMail message = newMessageMock(1);
		mailQueue.add(message);
		SendFailedException exc = new SendFailedException();
		doThrow(exc).when(transport).connect(anyString(), anyString(), anyString());
		doNothing().when(mQueueTaker).tryAgainAfterDelay(Matchers.<ImmutableList<UnsentMail>> any(), eq(exc));

		mQueueTaker.sendMessages(ImmutableList.of(message));

		verify(mQueueTaker).tryAgainAfterDelay(Matchers.<ImmutableList<UnsentMail>> any(), eq(exc));
	}
}
