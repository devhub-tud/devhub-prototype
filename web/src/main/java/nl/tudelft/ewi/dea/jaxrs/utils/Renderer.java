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
	private SecurityProvider securityProvider;

	@Inject
	public Renderer(TemplateEngine engine, SecurityProvider securityProvider) {
		this.engine = engine;
		this.securityProvider = securityProvider;
		this.context = new VelocityContext();		
		
		if (securityProvider.getSubject().isAuthenticated()) {
			setDefaultValuesForTemplate();
		}
	}
	
	private void setDefaultValuesForTemplate() {
		LOG.debug("Setting default values for template");
		User user = securityProvider.getUser();
		setValue("userDisplayName", user.getDisplayName());
		setValue("userId", user.getId());
	}

	public Renderer setValue(String key, Object value) {
		context.put(key, value);
		return this;
	}
	
	public String render(String template) {
		StringWriter writer = new StringWriter();
		engine.getTemplate(template).merge(context, writer);
		writer.flush();
		return writer.toString();
	}
}