package nl.tudelft.ewi.dea.mail.internals;

import lombok.experimental.Value;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

@Value
public class UnsentMail {

	private final long id;
	private final SimpleMessage message;

	UnsentMail(long id, SimpleMessage message) {
		this.id = id;
		this.message = message;
	}

}
