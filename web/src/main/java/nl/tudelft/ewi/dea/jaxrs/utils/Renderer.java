package nl.tudelft.ewi.dea.jaxrs.utils;

import java.io.StringWriter;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Renderer {

	private static final Logger LOG = LoggerFactory.getLogger(Renderer.class);

	private final TemplateEngine engine;
	private final VelocityContext context;
	private final SecurityProvider securityProvider;

	@Inject
	public Renderer(final TemplateEngine engine, final SecurityProvider securityProvider) {
		this.engine = engine;
		this.securityProvider = securityProvider;
		context = new VelocityContext();

		if (securityProvider.getSubject().isAuthenticated()) {
			setDefaultValuesForTemplate();
		}
	}

	private void setDefaultValuesForTemplate() {
		LOG.debug("Setting default values for template");
		final User user = securityProvider.getUser();
		setValue("userDisplayName", user.getDisplayName());
		setValue("userId", user.getId());
		setValue("isAdmin", user.isAdmin());
	}

	public Renderer setValue(final String key, final Object value) {
		context.put(key, value);
		return this;
	}

	public String render(final String template) {
		final StringWriter writer = new StringWriter();
		engine.getTemplate(template).merge(context, writer);
		writer.flush();
		return writer.toString();
	}
}