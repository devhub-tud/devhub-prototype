package nl.tudelft.ewi.dea.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

	public User getUser() {
		return user;
	}

	public Project getProject() {
		return project;
	}

}
