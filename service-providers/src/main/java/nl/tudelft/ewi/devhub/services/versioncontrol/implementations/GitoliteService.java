package nl.tudelft.ewi.devhub.services.versioncontrol.implementations;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.minicom.gitolite.manager.models.User;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import org.eclipse.jgit.transport.CredentialsProvider;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class GitoliteService implements VersionControlService {

	private final ConfigManager configManager;

	public GitoliteService(Properties properties) {
		this(properties.getProperty("url"), new PassphraseCredentialsProvider(properties.getProperty("passphrase")));
	}

	public GitoliteService(String gitUri, CredentialsProvider credentials) {
		configManager = ConfigManager.create(gitUri, Files.createTempDir(), credentials);
	}

	@Override
	public Future<ServiceResponse> createRepository(final RepositoryRepresentation repository) {
		return execute(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() throws Exception {
				Config config = null;
				try {
					config = configManager.getConfig();
				} catch (IOException e) {
					return new ServiceResponse(false, "The Gitolite service seems to be offline.");
				}

				if (config.hasRepository(repository.getName())) {
					return new ServiceResponse(false, "Repository already exists!");
				}

				Set<User> users = Sets.newHashSet();
				for (ServiceUser member : repository.getMembers()) {
					User user = config.ensureUserExists(member.getIdentifier());
					users.add(user);
				}

				Repository repo = config.createRepository(repository.getName());
				for (User user : users) {
					repo.setPermission(user, Permission.ALL);
				}

				configManager.applyConfig();
				return new ServiceResponse(true, "Successfully provisioned new repository!");
			}
		});
	}

	@Override
	public Future<ServiceResponse> removeRepository(RepositoryIdentifier repository) {
		return execute(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() throws Exception {
				return new ServiceResponse(false, "Removing repositories is not yet supported!");
			}
		});
	}

	private Future<ServiceResponse> execute(Callable<ServiceResponse> callable) {
		FutureTask<ServiceResponse> task = new FutureTask<ServiceResponse>(callable);
		task.run();
		return task;
	}

	@Override
	public String getName() {
		return "Gitolite";
	}

}
