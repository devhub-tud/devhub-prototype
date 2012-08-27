package nl.tudelft.ewi.dea.servlet.util;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public abstract class Servlet extends HttpServlet {

	private final Logger LOG = LoggerFactory.getLogger(Servlet.class);

	@Inject
	private Provider<Renderer> renderers;

	@Override
	public final void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			onGet(request, response);
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public abstract void onGet(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	public void render(String template) throws IOException {
		getRenderer().render(template);
	}
	
	public Renderer getRenderer() {
		return renderers.get();
	}
	
}
