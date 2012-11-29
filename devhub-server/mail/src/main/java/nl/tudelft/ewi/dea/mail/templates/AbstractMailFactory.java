package nl.tudelft.ewi.dea.mail.templates;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

abstract class AbstractMailFactory {

	private final VelocityEngine engine;

	AbstractMailFactory(VelocityEngine engine) {
		this.engine = engine;
	}

	protected String buildTemplate(String templateName, Map<String, Object> entities) {
		VelocityContext context = new VelocityContext();
		for (Entry<String, Object> entry : entities.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}

		Template t = engine.getTemplate(templateName);

		StringWriter writer = new StringWriter();

		t.merge(context, writer);

		return writer.toString();
	}
}
