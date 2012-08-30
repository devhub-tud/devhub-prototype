package nl.tudelft.ewi.dea.jaxrs.home;

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
		return renderers.get()
				.setValue("scripts", Lists.newArrayList("dashboard.js"))
				.render("dashboard.tpl");
	}

}
