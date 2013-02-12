package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.util.List;

import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

public abstract class VersionControlService implements Service {

	private final RepositoryUtils repoUtils;

	public VersionControlService(RepositoryUtils repoUtils) {
		this.repoUtils = repoUtils;
	}

	public String createRepository(RepositoryRepresentation repository, String cloneRepo) throws ServiceException {
		if (cloneRepo == null) {
			return createRemoteRepository(repository);
		}
		String repositoryUrl = createRemoteRepository(repository);
		repoUtils.setCustomTemplateInRepo(repositoryUrl, cloneRepo);
		return repositoryUrl;
	}

	public String createRepository(RepositoryRepresentation repository) throws ServiceException {
		String repositoryUrl = createRemoteRepository(repository);
		repoUtils.setDefaultTemplateInRepo(repositoryUrl);
		return repositoryUrl;
	}

	protected abstract String createRemoteRepository(RepositoryRepresentation repository) throws ServiceException;

	public abstract void removeRepository(String repoPath) throws ServiceException;

	public abstract void addSshKey(SshKeyRepresentation sshKey) throws ServiceException;

	public abstract void removeSshKeys(SshKeyIdentifier... sshKeys) throws ServiceException;

	public abstract void addUsers(String repoPath, List<ServiceUser> usersToAdd) throws ServiceException;

	public abstract void removeMembers(String repoPath, List<ServiceUser> usersToRemove) throws ServiceException;

}
