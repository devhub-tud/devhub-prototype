package nl.tudelft.ewi.dea.model;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "unsent_mails")
public class UnsentMail {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
	@Column(name = "mail", nullable = false) private String mail;

	public UnsentMail(String mailAsJson) {
		this.mail = mailAsJson;
		id = 0L;
	}

	public long getId() {
		return id;
	}

	public String getMail() {
		return mail;
	}

}
