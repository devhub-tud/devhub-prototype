package nl.tudelft.ewi.dea.mail.internals;

import static org.mockito.Mockito.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

import nl.tudelft.ewi.dea.dao.UnsentMailDao;
import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class QueuedMailSenderTest {

	@Mock private BlockingQueue<UnsentMail> mailsToSend;
	@Mock private Provider<UnsentMailDao> unsentMailDaoProv;
	@Mock private UnsentMailDao unsentMailDao;
	@Mock private ObjectMapper objectMapper;
	@Mock private ExecutorService executorService;
	@Mock private MailQueueTaker taker;
	@InjectMocks private QueuedMailSender mailSender;

	@Test
	public void whenStartedTheTakerIsPutIntoTheExecutor() {
		verify(executorService).execute(taker);
	}

	@Test
	public void whenAMessageMustBeDeliveredItIsPersistedFirst() throws JsonProcessingException {
		SimpleMessage message = Mockito.mock(SimpleMessage.class);
		String messageAsString = "jsonMessage";
		when(objectMapper.writeValueAsString(message)).thenReturn(messageAsString);
		when(unsentMailDaoProv.get()).thenReturn(unsentMailDao);
		when(unsentMailDao.persist(messageAsString)).thenReturn(new UnsentMailAsJson(messageAsString));

		mailSender.deliver(message);

		verify(unsentMailDao).persist(messageAsString);
	}
}
