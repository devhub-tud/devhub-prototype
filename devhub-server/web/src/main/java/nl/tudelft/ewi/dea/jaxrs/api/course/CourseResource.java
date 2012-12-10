package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.io.File;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/course")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;
	private final SecurityProvider securityProvider;

	private RepositoryDownloader downloader;

	@Inject
	public CourseResource(CourseDao courseDao, ProjectDao projectDao,
			ProjectMembershipDao membershipDao, SecurityProvider securityProvider,
			RepositoryDownloader downloader) {

		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.securityProvider = securityProvider;
		this.downloader = downloader;
	}

	@GET
	@Path("{id}/enroll")
	@Transactional
	public Response enroll(@PathParam("id") final long id) {
		final Course course = courseDao.findById(id);
		final User currentUser = securityProvider.getUser();

		if (membershipDao.hasEnrolled(course.getId(), currentUser)) {
			return Response.status(Status.CONFLICT).entity("User: " + currentUser.getDisplayName()
					+ " is already enrolled in the course: " + course.getName()).build();
		}

		// TODO: Make this into the form: IN4321: Functional programming - Group 2
		final String projectName = course.getName() + " - " + currentUser.getDisplayName();
		final Project project = new Project(projectName, course);
		projectDao.persist(project);

		final ProjectMembership membership = currentUser.addProjectMembership(project);
		membershipDao.persist(membership);

		return Response.ok().build();
	}

	@GET
	@Path("{id}/download")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String download(@PathParam("id") long id) {
		Course course = courseDao.findById(id);
		Builder<String> setBuilder = ImmutableSet.builder();
		for (Project project : course.getProjects()) {
			if (project.getSourceCodeUrl() == null) {
				LOG.warn("This project doesnt have a source code URL: {}", project);
			} else {
				setBuilder.add(project.getSourceCodeUrl());
			}
		}
		Set<String> sourceCodeurls = setBuilder.build();
		return downloader.prepareDownload(sourceCodeurls);
	}

	@GET
	@Path("download/{hash}")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Produces("application/x-zip-compressed")
	public Response download(@PathParam("hash") String hash) {
		File file = downloader.getFile(hash);
		if (file == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition", "attachment; filename = repositories.zip")
					.build();
		}
	}
}
