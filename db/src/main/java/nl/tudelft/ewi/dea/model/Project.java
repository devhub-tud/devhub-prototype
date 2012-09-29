package nl.tudelft.ewi.dea.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Project {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	private String name;

	@ManyToOne(optional = false) private Course course;

	@OneToMany(mappedBy = "project") private Set<ProjectMembership> members = new HashSet<>();

	@SuppressWarnings("unused")
	private Project() {}

	public Project(final String name, final Course course) {
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

	public Set<ProjectMembership> getMembers() {
		return Collections.unmodifiableSet(members);
	}

	void setMembers(final Set<ProjectMembership> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

		builder.append("id", getId());
		builder.append("name", getName());
		builder.append("course.id", getCourse().getId());
		builder.append("course.name", getCourse().getName());

		return builder.toString();
	}

}
