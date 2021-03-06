package nl.tudelft.ewi.dea.model;

import java.net.URL;
import java.util.Collections;
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

import com.google.common.collect.Sets;

@Entity
public class Project {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

	@Column(unique = true) private String name;

	@Column(name = "source_code_url") private String sourceCodeUrl;
	@Column(name = "version_control_service") private String versionControlService;

	@Column(name = "continuous_integration_url") private URL continuousIntegrationUrl;
	@Column(name = "continuous_integration_service") private String continuousIntegrationService;

	private boolean deployed;

	@ManyToOne(optional = false) private Course course;

	@OneToMany(mappedBy = "project") private final Set<ProjectInvitation> invitations = Sets.newHashSet();

	@OneToMany(mappedBy = "project") private Set<ProjectMembership> members = Sets.newHashSet();

	@SuppressWarnings("unused")
	private Project() {}

	public Project(final String name, final Course course) {
		this.name = name;
		this.course = course;
		this.deployed = false;
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

	public String getVersionControlService() {
		return versionControlService;
	}

	public void setVersionControlService(String versionControlService) {
		this.versionControlService = versionControlService;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
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

	public URL getContinuousIntegrationUrl() {
		return continuousIntegrationUrl;
	}

	public void setContinuousIntegrationUrl(URL continuesIntegrationUrl) {
		this.continuousIntegrationUrl = continuesIntegrationUrl;
	}

	public String getContinuousIntegrationService() {
		return continuousIntegrationService;
	}

	public void setContinuousIntegrationService(String continuousIntegrationService) {
		this.continuousIntegrationService = continuousIntegrationService;
	}

	public String getProjectId() {
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
		builder.append("SCM Url", getSourceCodeUrl());
		builder.append("CI Url", getContinuousIntegrationUrl());

		return builder.toString();
	}

}
