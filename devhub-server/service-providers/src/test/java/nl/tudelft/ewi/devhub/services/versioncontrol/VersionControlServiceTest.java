package nl.tudelft.ewi.devhub.services.versioncontrol;

import org.junit.Test;
import org.mockito.Mockito;

public class VersionControlServiceTest {

	@Test
	public void testCloningAreRepo() {
		VersionControlService service = Mockito.mock(VersionControlService.class, Mockito.CALLS_REAL_METHODS);
		service.setTemplateInRepo("git@devhub.nl:testalex.git", "git://github.com/alexnederlof/Jasper-report-maven-plugin.git");
	}

}
