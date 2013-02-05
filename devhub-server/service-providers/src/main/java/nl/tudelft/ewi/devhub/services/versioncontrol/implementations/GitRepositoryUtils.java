package nl.tudelft.ewi.devhub.services.versioncontrol.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.devhub.services.versioncontrol.RepositoryUtils;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

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

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

@Slf4j
public class GitRepositoryUtils implements RepositoryUtils {

	@Override
	public void setDefaultTemplateInRepo(String repositoryUrl) {
		setCustomTemplateInRepo(repositoryUrl, null);
	}

	@Override
	public void setCustomTemplateInRepo(String repositoryUrl, String cloneRepo) {
		File tmpDir = Files.createTempDir();
		log.info("Creating clone in {}", tmpDir.getPath());
		try {
			Git git;
			if (cloneRepo == null) {
				git = copyDefaultTemplate(tmpDir);
			} else {
				git = cloneRepo(cloneRepo, tmpDir);
			}
			pushClonedRepoToOurRepository(repositoryUrl, git);

		} catch (IOException | JGitInternalException | GitAPIException | URISyntaxException e) {
			log.error("Could not instantiate repo", e);
			throw new DevHubException("Could not instantiate repo", e);
		} finally {
			boolean deleted = FileUtils.deleteQuietly(tmpDir);
			log.debug("Temporary template deletion succes = {}", deleted);
		}

	}

	private Git copyDefaultTemplate(File tmpDir) throws URISyntaxException, IOException, NoFilepatternException,
			NoHeadException, NoMessageException, UnmergedPathException, ConcurrentRefUpdateException,
			WrongRepositoryStateException {
		Git git;

		copySkeleton(tmpDir);

		git = Git.init().setDirectory(tmpDir).call();
		git.add().addFilepattern(".").call();
		git.commit().setCommitter("DevHub", "devhub@devhub.nl").setMessage("Initial commit").call();
		log.debug("Initialized git repo with default template");
		return git;
	}

	private void copySkeleton(File tmpDir) {
		URL skeleton = VersionControlService.class.getResource("/project-skeleton");
		if (skeleton.toExternalForm().contains("jar!")) {
			copySkeletonFromJar(skeleton, tmpDir);
		} else {
			log.debug("Loading skeleton as file");
			try {
				FileUtils.copyDirectory(new File(skeleton.toURI()), tmpDir);
			} catch (IOException | URISyntaxException e) {
				throw new RuntimeException(
						"Could not copy required resources: " + e.getMessage(), e);
			}
		}

	}

	private void copySkeletonFromJar(URL skeleton, File tmpDir) {
		log.debug("Loading skeleton as JAR entry {}", skeleton);
		String path = skeleton.getPath();
		String jarpath = path.substring("file:".length(), path.indexOf("jar!") + "jar".length());
		File jar = new File(jarpath);
		log.debug("Jar file {} from path {}", jar, path);
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jar))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().startsWith("skeleton") && !entry.isDirectory()) {
					String filename = entry.getName().substring("skeleton/".length());
					File newFile = new File(tmpDir, filename);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream out = new FileOutputStream(newFile);
					ByteStreams.copy(zis, out);
					out.close();
				}
			}
		} catch (IOException e1) {
			throw new RuntimeException("Could not copy required resources: " + e1.getMessage(),
					e1);
		}
	}

	private Git cloneRepo(String cloneRepo, File tmpDir) {
		log.debug("Cloning {}", cloneRepo);
		Git git = Git.cloneRepository()
				.setCloneAllBranches(true)
				.setDirectory(tmpDir).setURI(cloneRepo)
				.call();
		return git;
	}

	private void pushClonedRepoToOurRepository(String repositoryUrl, Git git) throws IOException, InvalidRemoteException {
		log.debug("Changing remote to {}", repositoryUrl);
		StoredConfig config = git.getRepository().getConfig();
		config.setString("remote", "origin", "url", repositoryUrl);
		config.unset("remote", "origin", "fetch");
		config.save();
		git.push().setPushAll().setRemote("origin").call();
		log.debug("Push complete");
	}

}
