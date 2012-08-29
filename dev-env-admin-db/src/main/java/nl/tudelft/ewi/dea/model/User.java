package nl.tudelft.ewi.dea.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NotThreadSafe
public class User {

	@Id @GeneratedValue @Column(name = "id") private final long id;

	@Column(name = "display_name", unique = true, nullable = false) private String displayName;

	@Column(name = "mail_address", unique = true, nullable = false) private String mailAddress;

	@Column(name = "salt", nullable = false) private final String salt;

	@Column(name = "password", nullable = false) private String password;

	@Enumerated(EnumType.STRING) @Column(name = "role", nullable = false) private UserRole role;

	/**
	 * Constructor required by Hibernate.
	 */
	@SuppressWarnings("unused")
	private User() {
		id = 0;
		salt = "";
	}

	public User(String username, String mailAddress, String salt, String password, UserRole user) {
		checkArgument(!isNullOrEmpty(username));
		checkArgument(!isNullOrEmpty(mailAddress));
		checkArgument(!isNullOrEmpty(password));
		checkArgument(!isNullOrEmpty(salt));
		checkNotNull(user);
		this.id = 0;
		this.displayName = username;
		this.mailAddress = mailAddress;
		this.salt = salt;
		this.password = password;
		this.role = user;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public long getId() {
		return id;
	}

	public String getSalt() {
		return salt;
	}

	UserRole getRole() {
		return role;
	}

	public String getPassword() {
		return password;
	}

	public boolean isAdmin() {
		return role == UserRole.ADMIN;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((mailAddress == null) ? 0 : mailAddress.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (mailAddress == null) {
			if (other.mailAddress != null) {
				return false;
			}
		} else if (!mailAddress.equals(other.mailAddress)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (role != other.role) {
			return false;
		}
		if (salt == null) {
			if (other.salt != null) {
				return false;
			}
		} else if (!salt.equals(other.salt)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", displayName=" + displayName + ", mailAddress=" + mailAddress + ", role=" + role + "]";
	}

}
