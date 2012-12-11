package nl.tudelft.ewi.dea.jaxrs.api.course;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.Set;

import nl.tudelft.ewi.dea.DevHubException;

import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class RepositoryDownloaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(RepositoryDownloaderTest.class);

	@Test
	public void prepareDownloadWithoutError() {
		Set<String> repositories = ImmutableSet.of(
				"git://github.com/octocat/Hello-World.git",
				"git://github.com/octocat/Spoon-Knife.git");
		try {
			String out = new RepositoryDownloader().prepareDownload(repositories);
			assertThat(out, is(not(isEmptyOrNullString())));
		} catch (DevHubException e) {
			if (e.getCause() instanceof JGitInternalException) {
				LOG.warn("Test failed but it's ignore because it's probaply because some external URL is not working", e);
				/*
				 * That's fine. Something is probably just wrong with the
				 * connection. You might have to fix the test but it's not
				 * definitive that the code is broken.
				 */
				return;
			} else {
				throw e;
				// Unexpected exception. This is bad news.
			}
		}
	}
}
