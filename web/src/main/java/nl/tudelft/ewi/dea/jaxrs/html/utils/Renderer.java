package nl.tudelft.ewi.dea.jaxrs.html.utils;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.velocity.VelocityContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Renderer {

	private static final Set<String> RESERVED_WORDS = Sets.newHashSet(
			"SCRIPTS", "INNER-PAGE");

	private final TemplateEngine engine;
	private final VelocityContext context;

	private final List<String> scripts = Lists.newArrayList();

	@Inject
	public Renderer(TemplateEngine engine, SecurityProvider securityProvider) {
		this.engine = engine;
		this.context = new VelocityContext();

		context.put("SCRIPTS", scripts);
		if (securityProvider.getSubject().isAuthenticated()) {
			setValue("user", securityProvider.getUser());
		}
	}

	public Renderer setValue(String key, Object value) {
		if (RESERVED_WORDS.contains(key)) {
			throw new IllegalArgumentException("The key: " + key + " is a reserved keyword!");
		}

		context.put(key, value);
		return this;
	}

	public Renderer addJS(String javascriptFile) {
		scripts.add(javascriptFile);
		return this;
	}

	public String render(String template) {
		final StringWriter writer = new StringWriter();
		engine.getTemplate(template).merge(context, writer);
		writer.flush();
		return writer.toString();
	}

	public String render(String primaryTemplate, String template) {
		context.put("INNER-PAGE", render(template));
		return render(primaryTemplate);
	}
}