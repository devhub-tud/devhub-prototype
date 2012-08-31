package nl.tudelft.ewi.dea.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Course {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(unique = true, nullable = false) private String name;

	@ManyToOne(optional = false) private User owner;

	@OneToMany(mappedBy = "course") private Set<Project> projects = new HashSet<>();

	@SuppressWarnings("unused")
	private Course() {}

	public Course(final String name, final User owner) {
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

	// TODO: Is this necessary?
	@SuppressWarnings("unused")
	private void setProjects(final Set<Project> projects) {
		this.projects = projects;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("id", getId());
		builder.append("name", getName());
		builder.append("owner", getOwner().getMailAddress());
		return builder.toString();
	}

}