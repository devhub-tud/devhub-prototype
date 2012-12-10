package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class RepositoryDownloaderTest {

	@Test
	@Ignore("Only for manual use")
	public void prepareDownloadWithoutError() {
		Set<String> repositories = ImmutableSet.of(
				"git://github.com/octocat/Hello-World.git",
				"git://github.com/octocat/Spoon-Knife.git");
		String out = new RepositoryDownloader().prepareDownload(repositories);
		System.out.println(out);
	}
}
