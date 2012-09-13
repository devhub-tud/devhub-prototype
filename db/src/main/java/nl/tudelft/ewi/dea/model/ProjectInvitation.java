package nl.tudelft.ewi.dea.model;

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

	@ManyToOne(optional = false) private User user;
	@ManyToOne(optional = false) private Project project;

	@SuppressWarnings("unused")
	private ProjectInvitation() {}

	public ProjectInvitation(final User user, final Project project) {
		this.user = user;
		this.project = project;
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

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

		builder.append("id", getId());
		builder.append("user.id", getUser().getId());
		builder.append("user.displayName", getUser().getDisplayName());
		builder.append("project.id", getProject().getId());
		builder.append("project.name", getProject().getName());

		return builder.toString();
	}

}
