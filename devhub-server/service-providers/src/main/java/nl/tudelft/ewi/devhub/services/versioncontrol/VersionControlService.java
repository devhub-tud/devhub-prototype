package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.CreatedRepositoryResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

public abstract class VersionControlService implements Service {

	private static final Logger LOG = LoggerFactory.getLogger(VersionControlService.class);

	public Future<CreatedRepositoryResponse> createRepository(final RepositoryRepresentation repository, final String cloneRepo) {
		return submit(new Callable<CreatedRepositoryResponse>() {

			@Override
			public CreatedRepositoryResponse call() throws Exception {
				CreatedRepositoryResponse createdRepository = createRepository(repository).get();
				setTemplateInRepo(createdRepository.getRepositoryUrl(), cloneRepo);
				return createdRepository;
			}
		});
	}

	@VisibleForTesting
	void setTemplateInRepo(String repositoryUrl, String cloneRepo) {
		File tmpDir = Files.createTempDir();
		LOG.info("Creating clone in {}", tmpDir.getPath());
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setGitDir(tmpDir).readEnvironment().findGitDir().build();

			Git git = new Git(repository);
			CloneCommand clone = Git.cloneRepository();
			clone.setBare(true);
			clone.setCloneAllBranches(true);
			clone.setDirectory(tmpDir).setURI(cloneRepo);

			LOG.info("Cloning {}", cloneRepo);
			clone.call();

			LOG.info("Changing remote to {}", repositoryUrl);
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", repositoryUrl);
			config.unset("remote", "origin", "fetch");
			config.save();
			Iterable<PushResult> result = git.push().setPushAll().setRemote("origin").call();
			LOG.info("Push result {}", Joiner.on('\n').join(result));

		} catch (IOException | JGitInternalException | InvalidRemoteException e) {
			LOG.error("Could not instantiate repo", e);
			throw new DevHubException("Could not instantiate repo", e);
		}

	}

	protected <T> Future<T> submit(Callable<T> callable) {
		FutureTask<T> task = new FutureTask<T>(callable);
		task.run();
		return task;
	}

	public abstract Future<CreatedRepositoryResponse> createRepository(RepositoryRepresentation repository);

	public abstract Future<ServiceResponse> removeRepository(RepositoryIdentifier repository);

	public abstract Future<ServiceResponse> addSshKey(SshKeyRepresentation sshKey);

	public abstract Future<ServiceResponse> removeSshKeys(SshKeyIdentifier... sshKeys);
}
