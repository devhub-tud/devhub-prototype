package nl.tudelft.ewi.dea.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Project {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private final long id;

	@ManyToOne(optional = false) private final Course course;

	@OneToMany(mappedBy = "project") private final Set<ProjectMembership> members = new HashSet<>();

	private final String name;

	public Project(final String name, final Course course) {
		id = 0;
		this.name = name;
		this.course = course;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Course getCourse() {
		return course;
	}

}
