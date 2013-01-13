package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.StoredConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

public abstract class VersionControlService implements Service {

	private static final Logger LOG = LoggerFactory.getLogger(VersionControlService.class);

	public String createRepository(RepositoryRepresentation repository, String cloneRepo) throws ServiceException {
		if (cloneRepo == null) {
			return createRemoteRepository(repository);
		}
		String repositoryUrl = createRemoteRepository(repository);
		setTemplateInRepo(repositoryUrl, cloneRepo);
		return repositoryUrl;
	}

	@VisibleForTesting
	void setTemplateInRepo(String repositoryUrl, String cloneRepo) {
		File tmpDir = Files.createTempDir();
		LOG.info("Creating clone in {}", tmpDir.getPath());
		try {
			Git git;
			if (cloneRepo == null) {
				git = copyDefaultTemplate(tmpDir);
			} else {
				git = cloneRepo(cloneRepo, tmpDir);
			}
			pushClonedRepoToOurRepository(repositoryUrl, git);

		} catch (IOException | JGitInternalException | GitAPIException | URISyntaxException e) {
			LOG.error("Could not instantiate repo", e);
			throw new DevHubException("Could not instantiate repo", e);
		} finally {
			boolean deleted = FileUtils.deleteQuietly(tmpDir);
			LOG.debug("Temporary template deletion succes = {}", deleted);
		}

	}

	private Git copyDefaultTemplate(File tmpDir) throws URISyntaxException, IOException, NoFilepatternException,
			NoHeadException, NoMessageException, UnmergedPathException, ConcurrentRefUpdateException,
			WrongRepositoryStateException {
		Git git;
		File srcDir = new File(VersionControlService.class.getResource("/project-skeleton").toURI());
		FileUtils.copyDirectory(srcDir, tmpDir);
		git = Git.init().setDirectory(tmpDir).call();
		git.add().addFilepattern(".").call();
		git.commit().setCommitter("DevHub", "devhub@devhub.nl").setMessage("Initial commit").call();
		LOG.debug("Initialized git repo with default template");
		return git;
	}

	private Git cloneRepo(String cloneRepo, File tmpDir) {
		LOG.debug("Cloning {}", cloneRepo);
		Git git = Git.cloneRepository()
				.setCloneAllBranches(true)
				.setDirectory(tmpDir).setURI(cloneRepo)
				.call();
		return git;
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

	public String createRepository(RepositoryRepresentation repository) throws ServiceException {
		String repositoryUrl = createRemoteRepository(repository);
		setTemplateInRepo(repositoryUrl, null);
		return repositoryUrl;
	}

	protected abstract String createRemoteRepository(RepositoryRepresentation repository) throws ServiceException;

	public abstract void removeRepository(String repoPath) throws ServiceException;

	public abstract void addSshKey(SshKeyRepresentation sshKey) throws ServiceException;

	public abstract void removeSshKeys(SshKeyIdentifier... sshKeys) throws ServiceException;

	public abstract void addUsers(String repoPath, List<ServiceUser> usersToAdd) throws ServiceException;

}
