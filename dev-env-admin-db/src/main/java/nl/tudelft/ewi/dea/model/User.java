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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NotThreadSafe
@Table(name = "Users")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private final long id;

	@Column(unique = true, nullable = false) private String email;
	@Column(nullable = false) private String displayName;
	@Column(unique = true, nullable = false) private String netid;
	private long studentNumber;

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

	public User(final String displayName, final String email, final String netid, final long studentNumber, final String salt, final String password, final UserRole user) {
		checkArgument(!isNullOrEmpty(displayName));
		checkArgument(!isNullOrEmpty(email));
		checkArgument(!isNullOrEmpty(netid));
		checkArgument(!isNullOrEmpty(password));
		checkArgument(!isNullOrEmpty(salt));
		checkNotNull(user);
		id = 0;
		this.displayName = displayName;
		this.email = email;
		this.netid = netid;
		this.studentNumber = studentNumber;
		this.salt = salt;
		this.password = password;
		role = user;
	}

	public long getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMailAddress() {
		return email;
	}

	public String getNetid() {
		return netid;
	}

	public long getStudentNumber() {
		return studentNumber;
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
		result = prime * result + (int) (id ^ id >>> 32);
		result = prime * result + (displayName == null ? 0 : displayName.hashCode());
		result = prime * result + (email == null ? 0 : email.hashCode());
		result = prime * result + (netid == null ? 0 : netid.hashCode());
		result = prime * result + (int) (studentNumber ^ studentNumber >>> 32);
		result = prime * result + (password == null ? 0 : password.hashCode());
		result = prime * result + (role == null ? 0 : role.hashCode());
		result = prime * result + (salt == null ? 0 : salt.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if (id != other.id) {
			return false;
		}
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (netid == null) {
			if (other.netid != null) {
				return false;
			}
		} else if (!netid.equals(other.netid)) {
			return false;
		}
		if (studentNumber != other.studentNumber) {
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
		return "User [id=" + id + ", displayName=" + displayName + ", mailAddress=" + email + ", role=" + role + "]";
	}

	public void makeAdmin() {
		role = UserRole.ADMIN;
	}

}
