package nl.tudelft.ewi.dea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class ProjectInvitation {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@ManyToOne(optional = false) private Project project;
	@ManyToOne private User user;
	@Column(nullable = false) private String email;

	@SuppressWarnings("unused")
	private ProjectInvitation() {}

	public ProjectInvitation(final User user, final Project project) {
		this.user = user;
		this.project = project;
		this.email = user.getEmail();
	}

	/**
	 * New {@link ProjectInvitation} with user = <code>null</code>.
	 */
	public ProjectInvitation(final String email, final Project project) {
		this.user = null;
		this.project = project;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Project getProject() {
		return project;
	}

	public String getEmail() {
		return email;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

		builder.append("id", getId());
		if (user == null) {
			builder.append("user = null");
		} else {
			builder.append("user.id", getUser().getId());
			builder.append("user.displayName", getUser().getDisplayName());
		}
		builder.append("project.id", project.getId());
		builder.append("project.name", project.getName());
		builder.append("email", email);

		return builder.toString();
	}

}
