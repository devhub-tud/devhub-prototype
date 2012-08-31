package nl.tudelft.ewi.dea.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Course {

	@Id @GeneratedValue private final long id;

	@Column(unique = true, nullable = false) private String name;

	@ManyToOne(optional = false) private User owner;

	@OneToMany(mappedBy = "course") private final Set<Project> projects = new HashSet<>();

	@SuppressWarnings("unused")
	private Course() {
		id = 0;
	}

	public Course(final String name, final User owner) {
		id = 0;
		this.owner = owner;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public User getOwner() {
		return owner;
	}

	public Set<Project> getProjects() {
		return Collections.unmodifiableSet(projects);
	}

}
