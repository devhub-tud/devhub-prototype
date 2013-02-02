package nl.tudelft.ewi.dea.mail.internals;

import nl.tudelft.ewi.dea.mail.SimpleMessage;

import com.google.inject.ImplementedBy;

@ImplementedBy(QueuedMailSender.class)
public interface MailSender {

	void deliver(SimpleMessage message);

	void initialize();

}
