package nl.tudelft.ewi.dea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class RegistrationToken {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(nullable = false, unique = true, updatable = false) private String email;
	@Column(nullable = false, unique = true, updatable = true) private String token;

	@SuppressWarnings("unused")
	private RegistrationToken() {}

	public RegistrationToken(final String email, final String token) {
		this.email = email;
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

		builder.append("id", getId());
		builder.append("email", getEmail());
		builder.append("token", getToken());

		return builder.toString();
	}

}
