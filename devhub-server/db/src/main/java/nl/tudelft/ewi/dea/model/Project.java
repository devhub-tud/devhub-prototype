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
public class Project {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(unique = true) private String name;

	@Column(name = "source_code_url", nullable = false, unique = true) private String sourceCodeUrl;

	@ManyToOne(optional = false) private Course course;

	@OneToMany(mappedBy = "project") private final Set<ProjectInvitation> invitations = new HashSet<>();

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

	public String getSourceCodeUrl() {
		return sourceCodeUrl;
	}

	public void setSourceCodeUrl(String newUrl) {
		this.sourceCodeUrl = newUrl;
	}

	public Set<ProjectMembership> getMembers() {
		return Collections.unmodifiableSet(members);
	}

	public Set<ProjectInvitation> getInvitations() {
		return Collections.unmodifiableSet(invitations);
	}

	void setMembers(final Set<ProjectMembership> members) {
		this.members = members;
	}

	public String getSafeName() {
		String makeSafe = name;
		makeSafe = makeSafe.replace(" - ", "-");
		makeSafe = makeSafe.replace(" -", "-");
		makeSafe = makeSafe.replace("- ", "-");
		makeSafe = makeSafe.replace(" : ", "-");
		makeSafe = makeSafe.replace(" :", "-");
		makeSafe = makeSafe.replace(": ", "-");
		makeSafe = makeSafe.replace(" ", "-");
		makeSafe = makeSafe.toLowerCase();

		return makeSafe;
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
