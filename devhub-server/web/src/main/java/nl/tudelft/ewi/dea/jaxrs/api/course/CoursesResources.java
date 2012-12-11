package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.io.Files;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CoursesResources {

	private static final Logger LOG = LoggerFactory.getLogger(CoursesResources.class);

	private final CourseDao courseDao;
	private final UserDao userDao;

	private final RepositoryDownloader repoDownloader;

	@Inject
	public CoursesResources(CourseDao courseDao, UserDao userDao, RepositoryDownloader repoDownloader) {
		this.courseDao = courseDao;
		this.userDao = userDao;
		this.repoDownloader = repoDownloader;
	}

	@GET
	@Transactional
	public Response find(@QueryParam("enrolled") Boolean enrolled, @QueryParam("substring") String subString) {
		List<CourseDto> courses = CourseDto.convert(courseDao.find(enrolled, subString));
		GenericEntity<List<CourseDto>> entity = new GenericEntity<List<CourseDto>>(courses) {};
		return Response.ok(entity).build();
	}

	@POST
	@Path("create")
	@Transactional
	public Response create(CourseCreationRequest request) {
		if (!request.getName().matches("^[a-zA-Z0-9]{6,}\\s.{6,}$")) {
			return Response.status(Status.CONFLICT).entity("invalid-name").build();
		}

		try {
			courseDao.findByName(request.getName());
			return Response.status(Status.CONFLICT).entity("already-taken").build();
		} catch (NoResultException e) {
			// Proceed to next step.
		}

		String email = (String) SecurityUtils.getSubject().getPrincipal();
		User owner = userDao.findByEmail(email);
		Course course = new Course(request.getName(), owner, request.getTemplateUrl());

		try {
			checkTemplateUrl(request.getTemplateUrl());
		} catch (JGitInternalException e) {
			LOG.debug("Could not clone template git repo " + request.getTemplateUrl(), e);
			return Response.status(Status.CONFLICT).entity("could-not-clone-repo").build();
		}

		courseDao.persist(course);
		return Response.ok(course).build();
	}

	private void checkTemplateUrl(String templateUrl) {
		if (templateUrl == null || templateUrl.trim().isEmpty()) {
			return;
		} else {
			LOG.info("Checking if {} is valid Git repo", templateUrl);
			File tmpDir = Files.createTempDir();
			try {
				Git.cloneRepository()
						.setCloneAllBranches(true)
						.setDirectory(tmpDir).setURI(templateUrl)
						.call();
			} finally {
				try {
					if (tmpDir.exists()) {
						FileUtils.deleteDirectory(tmpDir);
					}
				} catch (Exception e) {
					LOG.warn("Could not delete tmp directory " + tmpDir.getAbsolutePath());
				}
			}
		}

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
		return repoDownloader.prepareDownload(sourceCodeurls);
	}

	@GET
	@Path("download/{hash}")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Produces("application/x-zip-compressed")
	public Response download(@PathParam("hash") String hash) {
		File file = repoDownloader.getFile(hash);
		if (file == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition", "attachment; filename = repositories.zip")
					.build();
		}
	}
}
