package nl.tudelft.ewi.dea.jaxrs.admin;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@Path("admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

	private final Provider<Renderer> renderers;

	@Inject
	public AdminResource(final Provider<Renderer> renderers) {
		this.renderers = renderers;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {

		LOG.trace("Serving admin dashboard.");

		return renderers.get()
				.setValue("scripts", Lists.newArrayList("admin.js"))
				.render("admin.tpl");

	}

}
