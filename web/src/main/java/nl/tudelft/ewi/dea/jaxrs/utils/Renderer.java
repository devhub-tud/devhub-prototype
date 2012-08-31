package nl.tudelft.ewi.dea.jaxrs.utils;

import java.io.StringWriter;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.velocity.VelocityContext;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Renderer {
	
	private final TemplateEngine engine;
	private final VelocityContext context;

	@Inject
	public Renderer(TemplateEngine engine) {
		this.engine = engine;
		this.context = new VelocityContext();
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