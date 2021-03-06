package nl.tudelft.ewi.dea.jaxrs.api.projects.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.devhub.services.PasswordGenerator;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.ServiceProvider;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.jenkins.client.exceptions.JenkinsException;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Slf4j
public class ServicesBackend {

	private final ScheduledExecutorService executor;
	private final ProjectMembershipDao dao;
	private final ServiceProvider services;
	private final ProjectDao projects;
	private final DevHubMail mailer;

	@Inject
	public ServicesBackend(ScheduledExecutorService executor,
			ServiceProvider services, ProjectDao projects,
			DevHubMail mailer, ProjectMembershipDao memberships) {

		this.executor = executor;
		this.services = services;
		this.projects = projects;
		this.mailer = mailer;
		dao = memberships;
	}

	public void addMembers(long projectId, ServiceUser... users) {
		addMembers(projectId, Lists.newArrayList(users));
	}

	public void addMembers(long projectId, Collection<ServiceUser> users) {
		Future<?> future = executor.submit(new AddMembers(projectId, Lists.newArrayList(users)));

		// TODO: Hack. The project membership should be set in this Runnable, but
		// is currently set outside of this method.
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void removeMembers(long projectId, ServiceUser... users) {
		removeMembers(projectId, Lists.newArrayList(users));
	}

	public void removeMembers(long projectId, Collection<ServiceUser> users) {
		Future<?> future = executor.submit(new RemoveMembers(projectId, Lists.newArrayList(users)));

		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void ensureUserExists(ContinuousIntegrationService service, ServiceUser user) throws ServiceException {
		try {
			String password = PasswordGenerator.generate();
			service.registerUser(user, password);
			mailer.sendServiceRegistrationEmail(service.getName(), user.getIdentifier(), password, user.getEmail());
		} catch (JenkinsException e) {
			log.warn(e.getMessage(), e);
		}
	}

	@AllArgsConstructor
	private class AddMembers implements Runnable {

		private final long projectId;
		private final List<ServiceUser> users;

		@Override
		public void run() {
			Project project = projects.findById(projectId);
			VersionControlService versionControlService = services.getVersionControlService(project.getVersionControlService());
			ContinuousIntegrationService continuousIntegrationService = services.getContinuousIntegrationService(project.getContinuousIntegrationService());

			List<ServiceUser> usersToAdd = Lists.newArrayList();
			List<ProjectMembership> projectMembers = dao.findByProjectId(projectId);
			for (ServiceUser user : users) {
				if (!alreadyMember(projectMembers, user)) {
					usersToAdd.add(user);
					try {
						ensureUserExists(continuousIntegrationService, user);
					} catch (ServiceException e) {
						log.error("Could not register user for Continuous Integration Service: " + continuousIntegrationService.getName(), e);
					}
				}
			}

			try {
				versionControlService.addUsers(project.getProjectId(), usersToAdd);
				continuousIntegrationService.addMembers(project.getProjectId(), usersToAdd);
			} catch (ServiceException e) {
				log.error("Could not register users for background services: " + Joiner.on(", ").join(usersToAdd), e);
			}
		}

		private boolean alreadyMember(List<ProjectMembership> currentMembers, ServiceUser user) {
			for (ProjectMembership member : currentMembers) {
				if (member.getUser().getNetId().equals(user.getIdentifier())) {
					return true;
				}
			}
			return false;
		}
	}

	@AllArgsConstructor
	private class RemoveMembers implements Runnable {

		private final long projectId;
		private final List<ServiceUser> users;

		@Override
		public void run() {
			Project project = projects.findById(projectId);
			VersionControlService versionControlService = services.getVersionControlService(project.getVersionControlService());
			ContinuousIntegrationService continuousIntegrationService = services.getContinuousIntegrationService(project.getContinuousIntegrationService());

			List<ServiceUser> usersToRemove = Lists.newArrayList();
			List<ProjectMembership> projectMembers = dao.findByProjectId(projectId);
			for (ServiceUser user : users) {
				if (alreadyMember(projectMembers, user)) {
					usersToRemove.add(user);
				}
			}

			try {
				versionControlService.removeMembers(project.getProjectId(), usersToRemove);
				continuousIntegrationService.removeMembers(project.getProjectId(), usersToRemove);
			} catch (ServiceException e) {
				log.error("Could not deregister users for background services: " + Joiner.on(", ").join(usersToRemove), e);
			}
		}

		private boolean alreadyMember(List<ProjectMembership> currentMembers, ServiceUser user) {
			for (ProjectMembership member : currentMembers) {
				if (member.getUser().getNetId().equals(user.getIdentifier())) {
					return true;
				}
			}
			return false;
		}
	}

}
