package nl.tudelft.ewi.dea.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

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

import liquibase.util.MD5Util;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Sets;

@Entity
@NotThreadSafe
@Table(name = "Users")
@EqualsAndHashCode(of = {"id"})
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(unique = true, nullable = false) private String email;
	@Column(nullable = false) private String displayName;
	@Column(unique = true, nullable = false) private String netId;
	private long studentNumber;

	@Column(nullable = false) private String salt;
	@Column(nullable = false) private String password;

	@Enumerated(EnumType.STRING) @Column(name = "access_role", nullable = false) private UserRole role;

	@OneToMany(mappedBy = "user") private final Set<ProjectMembership> memberships = Sets.newHashSet();

	/**
	 * Constructor required by Hibernate.
	 */
	@SuppressWarnings("unused")
	private User() {}

	public User(final String displayName, final String email, final String netId, final long studentNumber, final String salt, final String password, final UserRole role) {
		checkArgument(!isNullOrEmpty(displayName));
		checkArgument(!isNullOrEmpty(email));
		checkArgument(!isNullOrEmpty(netId));
		checkArgument(!isNullOrEmpty(password));
		checkArgument(!isNullOrEmpty(salt));
		checkNotNull(role);
		this.displayName = displayName;
		this.email = email;
		this.netId = netId;
		this.studentNumber = studentNumber;
		this.salt = salt;
		this.password = password;
		this.role = role;
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

	public String getGravatarUrl(int size) {
		return "http://www.gravatar.com/avatar/" + MD5Util.computeMD5(email.toLowerCase()) + "?s=" + size + "&d=mm";
	}

	public String getNetId() {
		return netId;
	}

	public long getStudentNumber() {
		return studentNumber;
	}

	public String getSalt() {
		return salt;
	}

	public UserRole getRole() {
		return role;
	}

	public String getPassword() {
		return password;
	}

	public boolean isAdmin() {
		return role == UserRole.ADMIN;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("id", getId());
		builder.append("email", getEmail());
		builder.append("displayName", getDisplayName());
		builder.append("netid", getNetId());
		builder.append("studentNumber", getStudentNumber());
		builder.append("role", getRole());
		return builder.toString();
	}

	public void promoteToAdmin() {
		role = UserRole.ADMIN;
	}

	public void demoteToUser() {
		role = UserRole.USER;
	}

	public Set<ProjectMembership> getProjectMemberships() {
		return memberships;
	}

	public ProjectMembership addProjectMembership(Project p) {
		return new ProjectMembership(this, p);
	}

	/**
	 * @param hashedPassword The hashed password.
	 */
	public void setPassword(String hashedPassword) {
		password = hashedPassword;
	}

}
