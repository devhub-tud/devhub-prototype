package nl.tudelft.ewi.dea.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@NotThreadSafe
@Table(name = "Users")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(unique = true, nullable = false) private String email;
	@Column(nullable = false) private String displayName;
	@Column(unique = true, nullable = false) private String netid;
	private long studentNumber;

	@Column(nullable = false) private String salt;
	@Column(nullable = false) private String password;

	@Enumerated(EnumType.STRING) @Column(name = "role", nullable = false) private UserRole role;

	@OneToMany(mappedBy = "user") private Set<ProjectMembership> memberships = new HashSet<>();

	/**
	 * Constructor required by Hibernate.
	 */
	@SuppressWarnings("unused")
	private User() {}

	public User(final String displayName, final String email, final String netid, final long studentNumber, final String salt, final String password, final UserRole user) {
		checkArgument(!isNullOrEmpty(displayName));
		checkArgument(!isNullOrEmpty(email));
		checkArgument(!isNullOrEmpty(netid));
		checkArgument(!isNullOrEmpty(password));
		checkArgument(!isNullOrEmpty(salt));
		checkNotNull(user);
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

	public String getEmail() {
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
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("id", getId());
		builder.append("email", getEmail());
		builder.append("displayName", getDisplayName());
		builder.append("netid", getNetid());
		builder.append("studentNumber", getStudentNumber());
		builder.append("role", getRole());
		builder.append("memberships", getProjectMemberships());
		return builder.toString();
	}

	public void makeAdmin() {
		role = UserRole.ADMIN;
	}

	public Set<ProjectMembership> getProjectMemberships() {
		return memberships;
	}

	void setProjectMemberships(final Set<ProjectMembership> memberships) {
		this.memberships = memberships;
	}

	public ProjectMembership addProjectMembership(final Project p) {

		final ProjectMembership pm = new ProjectMembership(this, p);

		memberships.add(pm);

		return pm;

	}

}
