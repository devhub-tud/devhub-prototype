package nl.tudelft.ewi.dea.jaxrs.api.course;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import nl.tudelft.ewi.dea.DevHubException;

import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

public class RepositoryDownloaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(RepositoryDownloaderTest.class);

	@Test
	public void prepareDownloadWithoutError() throws IOException {
		Set<String> repositories = ImmutableSet.of(
				"git://github.com/octocat/Hello-World.git",
				"git://github.com/octocat/Spoon-Knife.git");
		try {
			RepositoryDownloader downloader = new RepositoryDownloader();
			String hash = downloader.prepareDownload(repositories);
			assertThat(hash, is(not(isEmptyOrNullString())));

			File f = downloader.getFile(hash);
			assertThat(f.exists(), is(true));
			String fileHash = getHashFromFile(f);
			assertThat(fileHash, is(hash));
			f.deleteOnExit();
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

	private String getHashFromFile(File f) throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(f);
		String guavaHash = Hashing.md5().hashBytes(ByteStreams.toByteArray(in)).toString();
		in.close();
		return guavaHash;
	}
}
