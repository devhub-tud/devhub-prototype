package nl.tudelft.ewi.devhub.services.versioncontrol;

import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class VersionControlServiceTest {

	private static final Logger LOG = LoggerFactory.getLogger(VersionControlServiceTest.class);

	private File repoDirToClone;
	private Git repoToClone;
	private File targetRepoDir;
	private Git targetRepo;

	@Before
	public void setup() throws Exception {
		setupRepoToClone();
		setupEmptyTargetRepo();
	}

	private void setupEmptyTargetRepo() throws Exception {
		targetRepoDir = Files.createTempDir();
		targetRepo = Git.init().setBare(true).setDirectory(targetRepoDir).call();
	}

	private void setupRepoToClone() throws Exception {
		repoDirToClone = Files.createTempDir();
		repoToClone = Git.init().setDirectory(repoDirToClone).call();
		// repoToClone = Git.cloneRepository()
		// .setCloneAllBranches(true)
		// .setDirectory(repoDirToClone).setURI("git://github.com/octocat/Spoon-Knife.git")
		// .call();
		Files.write("Test file".getBytes(), new File(repoDirToClone, "testFile"));
		repoToClone.add().addFilepattern("testFile").call();
		repoToClone.commit().setAuthor("JUnit", "test@example.com").setMessage("Setting test file").call();
	}

	@Test
	public void testCloningAreRepo() throws Exception {
		VersionControlService service = Mockito.mock(VersionControlService.class, Mockito.CALLS_REAL_METHODS);
		service.setTemplateInRepo("file:" + targetRepoDir.getAbsolutePath(),
				repoDirToClone.getAbsolutePath());
		List<RevCommit> targetCommit = Lists.newArrayList(targetRepo.log().all().call());
		List<RevCommit> clonedCommit = Lists.newArrayList(repoToClone.log().all().call());
		Assert.assertThat(targetCommit, is(clonedCommit));
	}

	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(repoDirToClone);
			FileUtils.deleteDirectory(targetRepoDir);
		} catch (IOException e) {
			LOG.error("Could not clean up test directories", e);
		}
	}
}
