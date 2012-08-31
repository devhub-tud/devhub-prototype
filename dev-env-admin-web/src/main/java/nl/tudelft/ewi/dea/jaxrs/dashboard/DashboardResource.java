package nl.tudelft.ewi.dea.jaxrs.dashboard;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import com.google.common.collect.Lists;

@Singleton
@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private final Provider<Renderer> renderers;

	@Inject
	public DashboardResource(Provider<Renderer> renderers) {
		this.renderers = renderers;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {
		List<Project> invitations = Lists.newArrayList(
				new Project("IN2610 Advanced Algorithms - Group 22")
				);

		List<Project> projects = Lists.newArrayList(
				new Project("IN2010 Algorithms - Group 5"),
				new Project("IN2105 Software Quality & Testing - Group 8"),
				new Project("IN2505 Context project 1 - Group 7")
				);

		return renderers.get()
				.setValue("invitations", invitations)
				.setValue("projects", projects)
				.setValue("scripts", Lists.newArrayList("dashboard.js"))
				.render("dashboard.tpl");
	}

	// TODO: Temp class, this should be replaced with something reading from the
	// database...
	public static class Project {
		private final String name;

		public Project(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
