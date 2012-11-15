package nl.tudelft.ewi.dea.jaxrs.integration.course;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.di.ProvisioningModule;
import nl.tudelft.ewi.dea.di.ServerStartupListener;
import nl.tudelft.ewi.dea.di.WebModule;
import nl.tudelft.ewi.dea.jaxrs.course.CourseResource;
import nl.tudelft.jenkins.guice.JenkinsWsClientGuiceModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

@RunWith(MockitoJUnitRunner.class)
public class CourseResourceIntegrationTest {

	private final ObjectMapper mapper = new CommonModule().objectMapper();

	private Injector injector;

	private CourseResource resource;

	@Mock private ServletContext servletContext;

	@Before
	public void setUp() {

		when(servletContext.getRealPath("/templates/")).thenReturn("src/main/webapp/templates/");
		ServerConfig serverConfig = new ServerStartupListener().readServerConfig(mapper);
		injector = Guice.createInjector(new WebModule(servletContext, serverConfig), new ProvisioningModule(serverConfig), new JenkinsWsClientGuiceModule());

		resource = injector.getInstance(CourseResource.class);

	}

	@After
	public void tearDown() {

		injector.getInstance(PersistService.class).stop();

	}

	@Test
	@Ignore("Improve fixture first - throws com.google.inject.OutOfScopeException.")
	public void testThat() throws Exception {

		final Response response = resource.enroll(12345);

		assertThat(true, is(true));

	}

}
