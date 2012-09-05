package nl.tudelft.ewi.dea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class ProjectMembership {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@ManyToOne(optional = false) private User user;
	@ManyToOne(optional = false) private Project project;

	@SuppressWarnings("unused")
	private ProjectMembership() {}

	public ProjectMembership(final User user, final Project project) {
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
		builder.append("user", getUser().getEmail());
		builder.append("project", getProject().getName());
		return builder.toString();
	}

}
