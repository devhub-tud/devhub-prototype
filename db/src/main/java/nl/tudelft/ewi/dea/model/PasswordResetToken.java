package nl.tudelft.ewi.dea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class PasswordResetToken {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@OneToOne(optional = false) private User user;
	private String token;

	@SuppressWarnings("unused")
	private PasswordResetToken() {

	}

	public PasswordResetToken(final User user, final String token) {
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

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

		builder.append("id", getId());
		builder.append("user.id", getUser().getId());
		builder.append("user.displayName", getUser().getDisplayName());
		builder.append("token", getToken());

		return builder.toString();
	}

}
