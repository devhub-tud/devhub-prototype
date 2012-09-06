package nl.tudelft.ewi.dea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PasswordResetToken {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@OneToOne(optional = false) private User user;
	private String token;

	@SuppressWarnings("unused")
	private PasswordResetToken() {

	}

	public PasswordResetToken(User user, String token) {
		this.user = user;
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getToken() {
		return token;
	}

}
