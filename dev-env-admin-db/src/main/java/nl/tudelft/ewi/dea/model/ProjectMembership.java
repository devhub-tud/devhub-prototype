package nl.tudelft.ewi.dea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProjectMembership {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private final long id = 0;

	@ManyToOne(optional = false) private final User user;
	@ManyToOne(optional = false) private final Project project;

	@SuppressWarnings("unused")
	private ProjectMembership() {
		user = null;
		project = null;
	}

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

}
