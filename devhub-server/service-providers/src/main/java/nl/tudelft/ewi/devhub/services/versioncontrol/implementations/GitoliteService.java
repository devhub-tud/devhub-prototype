package nl.tudelft.ewi.devhub.services.versioncontrol.implementations;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.exceptions.ServiceUnavailable;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.minicom.gitolite.manager.models.User;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
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
	public String createRepository(RepositoryRepresentation repository) throws ServiceException {
		try {
			Config config = configManager.getConfig();
			String repositoryName = repository.getName();

			if (config.hasRepository(repositoryName)) {
				throw new ServiceException("The repository '" + repositoryName + "' already exists!");
			}

			Set<User> users = Sets.newHashSet();
			for (ServiceUser member : repository.getMembers()) {
				User user = config.ensureUserExists(member.getIdentifier());
				users.add(user);
			}

			Repository repo = config.createRepository(repositoryName);
			for (User user : users) {
				repo.setPermission(user, Permission.ALL);
			}

			configManager.applyConfig();
			return gitAddress + ":" + repo.getName();

		} catch (IOException | ServiceUnavailable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("The Gitolite service seems to be offline.");
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("Failed to create the specified repository!");
		}
	}

	@Override
	public void removeRepository(RepositoryIdentifier repository) throws ServiceException {
		throw new ServiceException("Removing repositories is not yet supported!");
	}

	@Override
	public void addSshKey(SshKeyRepresentation sshKey) throws ServiceException {
		try {
			Config config = configManager.getConfig();
			ServiceUser user = sshKey.getCreator();
			User gitUser = config.ensureUserExists(user.getIdentifier());

			gitUser.defineKey(sshKey.getName(), sshKey.getKey());
			configManager.applyConfig();
		} catch (IOException | ServiceUnavailable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("The Gitolite service seems to be offline.");
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("Failed to add your SSH key!");
		}
	}

	@Override
	public void removeSshKeys(SshKeyIdentifier... sshKeys) throws ServiceException {
		try {
			Config config = configManager.getConfig();
			for (SshKeyIdentifier sshKey : sshKeys) {
				ServiceUser user = sshKey.getCreator();
				User gitUser = config.ensureUserExists(user.getIdentifier());
				gitUser.removeKey(sshKey.getName());
			}

			configManager.applyConfig();
		} catch (IOException | ServiceUnavailable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("The Gitolite service seems to be unavailable...");
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("Failed to remove your SSH key(s)!");
		}
	}

	@Override
	public String getName() {
		return "Gitolite";
	}

}
