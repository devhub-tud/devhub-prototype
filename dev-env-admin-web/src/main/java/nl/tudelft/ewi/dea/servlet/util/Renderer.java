package nl.tudelft.ewi.dea.servlet.util;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.velocity.VelocityContext;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Renderer {
	
	private final HttpServletResponse response;
	private final TemplateEngine engine;
	private final VelocityContext context;

	@Inject
	public Renderer(HttpServletResponse response, TemplateEngine engine) {
		this.response = response;
		this.engine = engine;
		this.context = new VelocityContext();
	}
	
	public Renderer setValue(String key, Object value) {
		context.put(key, value);
		return this;
	}
	
	public void render(String template) throws IOException {
		engine.getTemplate(template).merge(context, response.getWriter());
	}
}