package nl.tudelft.ewi.dea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class RegistrationToken {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private final long id;

	@Column(nullable = false, unique = true, updatable = false) private final String email;
	@Column(nullable = false, unique = true, updatable = false) private final String token;

	@SuppressWarnings("unused")
	private RegistrationToken() {
		id = 0;

		email = null;
		token = null;
	}

	public RegistrationToken(final String email, final String token) {
		this.id = 0;

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

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
