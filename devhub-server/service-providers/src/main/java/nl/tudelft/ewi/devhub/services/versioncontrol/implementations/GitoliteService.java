package nl.tudelft.ewi.devhub.services.versioncontrol.implementations;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.exceptions.ServiceUnavailable;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.minicom.gitolite.manager.models.User;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.CreatedRepositoryResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class GitoliteService extends VersionControlService {

	private static final Logger LOG = LoggerFactory.getLogger(GitoliteService.class);

	private final ConfigManager configManager;
	private final String gitAddress;

	public GitoliteService(Properties properties) {
		this(properties.getProperty("user"), properties.getProperty("host"), properties.getProperty("admin-repo"),
				new PassphraseCredentialsProvider(properties.getProperty("passphrase")));
	}

	public GitoliteService(String user, String gitHost, String adminRepo, CredentialsProvider credentials) {
		Preconditions.checkNotNull(user);
		Preconditions.checkNotNull(gitHost);
		Preconditions.checkNotNull(adminRepo);

		this.gitAddress = user + "@" + gitHost;
		this.configManager = ConfigManager.create(gitAddress + ":" + adminRepo, Files.createTempDir(), credentials);
	}

	@Override
	public Future<CreatedRepositoryResponse> createRepository(final RepositoryRepresentation repository) {
		return submit(new Callable<CreatedRepositoryResponse>() {
			@Override
			public CreatedRepositoryResponse call() throws Exception {
				try {
					Config config = configManager.getConfig();
					if (config.hasRepository(repository.getName())) {
						return new CreatedRepositoryResponse(false, "Repository already exists!", null);
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

					return new CreatedRepositoryResponse(true, "Successfully provisioned new repository!", gitAddress + ":" + repo.getName());
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
					return new CreatedRepositoryResponse(false, "The Gitolite service seems to be offline.", null);
				} catch (Throwable e) {
					LOG.error(e.getMessage(), e);
					return new CreatedRepositoryResponse(false, "Failed to provision your new repository!", null);
				}
			}
		});
	}

	@Override
	public Future<ServiceResponse> removeRepository(RepositoryIdentifier repository) {
		return submit(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() {
				return new ServiceResponse(false, "Removing repositories is not yet supported!");
			}
		});
	}

	@Override
	public Future<ServiceResponse> addSshKey(final SshKeyRepresentation sshKey) {
		return submit(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() {
				try {
					Config config = configManager.getConfig();
					ServiceUser user = sshKey.getCreator();
					User gitUser = config.ensureUserExists(user.getIdentifier());

					gitUser.defineKey(sshKey.getName(), sshKey.getKey());
					configManager.applyConfig();

					return new ServiceResponse(true, "Successfully added your new SSH key!");
				} catch (IOException | ServiceUnavailable e) {
					LOG.error(e.getMessage(), e);
					return new ServiceResponse(false, "The Gitolite service seems to be offline.");
				} catch (Throwable e) {
					LOG.error(e.getMessage(), e);
					return new ServiceResponse(false, "Failed to add your SSH key!");
				}
			}
		});
	}

	@Override
	public Future<ServiceResponse> removeSshKeys(final SshKeyIdentifier... sshKeys) {
		return submit(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() {
				try {
					Config config = configManager.getConfig();
					for (SshKeyIdentifier sshKey : sshKeys) {
						ServiceUser user = sshKey.getCreator();
						User gitUser = config.ensureUserExists(user.getIdentifier());
						gitUser.removeKey(sshKey.getName());
					}

					configManager.applyConfig();

					return new ServiceResponse(true, "Successfully removed your SSH key(s)!");
				} catch (IOException | ServiceUnavailable e) {
					LOG.error(e.getMessage(), e);
					return new ServiceResponse(false, "The Gitolite service seems to be unavailable...");
				} catch (Throwable e) {
					LOG.error(e.getMessage(), e);
					return new ServiceResponse(false, "Failed to remove your SSH key(s)!");
				}
			}
		});
	}

	@Override
	public String getName() {
		return "Gitolite";
	}

}
