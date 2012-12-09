package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.io.File;
import java.io.IOException;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.StoredConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

public abstract class VersionControlService implements Service {

	private static final Logger LOG = LoggerFactory.getLogger(VersionControlService.class);

	public String createRepository(RepositoryRepresentation repository, String cloneRepo) throws ServiceException {
		if (cloneRepo == null) {
			return createRepository(repository);
		}
		String repositoryUrl = createRepository(repository);
		setTemplateInRepo(repositoryUrl, cloneRepo);
		return repositoryUrl;
	}

	@VisibleForTesting
	void setTemplateInRepo(String repositoryUrl, String cloneRepo) {
		File tmpDir = Files.createTempDir();
		LOG.info("Creating clone in {}", tmpDir.getPath());
		try {
			LOG.debug("Cloning {}", cloneRepo);
			Git git = Git.cloneRepository()
					.setCloneAllBranches(true)
					.setDirectory(tmpDir).setURI(cloneRepo)
					.call();

			pushClonedRepoToOurRepository(repositoryUrl, git);

		} catch (IOException | JGitInternalException | InvalidRemoteException e) {
			LOG.error("Could not instantiate repo", e);
			throw new DevHubException("Could not instantiate repo", e);
		} finally {
			try {
				if (tmpDir.exists()) {
					FileUtils.deleteDirectory(tmpDir);
				}
			} catch (IOException e) {
				LOG.warn("Could not delete temporary directory " + tmpDir.getPath());
			}
		}

	}

	private void pushClonedRepoToOurRepository(String repositoryUrl, Git git) throws IOException, InvalidRemoteException {
		LOG.debug("Changing remote to {}", repositoryUrl);
		StoredConfig config = git.getRepository().getConfig();
		config.setString("remote", "origin", "url", repositoryUrl);
		config.unset("remote", "origin", "fetch");
		config.save();
		git.push().setPushAll().setRemote("origin").call();
		LOG.debug("Push complete");
	}

	public abstract String createRepository(RepositoryRepresentation repository) throws ServiceException;

	public abstract void removeRepository(RepositoryIdentifier repository) throws ServiceException;

	public abstract void addSshKey(SshKeyRepresentation sshKey) throws ServiceException;

	public abstract void removeSshKeys(SshKeyIdentifier... sshKeys) throws ServiceException;
}
