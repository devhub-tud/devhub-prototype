package nl.tudelft.ewi.dea.servlet.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.gson.Gson;


@SuppressWarnings("serial")
public abstract class Servlet extends HttpServlet {

	private final Logger LOG = LoggerFactory.getLogger(Servlet.class);
	
	private final Map<String, Method> getProcessors = Maps.newHashMap();
	private final Map<String, Method> postProcessors = Maps.newHashMap();

	@Inject
	private Provider<Renderer> renderers;
	
	public Servlet() {
		for (Method method : getClass().getMethods()) {
			Get get = method.getAnnotation(Get.class);
			if (get != null) {
				getProcessors.put(method.getName(), method);
			}
			Post post = method.getAnnotation(Post.class);
			if (post != null) {
				postProcessors.put(method.getName(), method);
			}
		}
	}

	@Override
	public final void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String method = request.getParameter("method");
			if (StringUtils.isEmpty(method)) {
				onGet(request, response);
			}
			else if (getProcessors.containsKey(method)) {
				Object result = getProcessors.get(method).invoke(this, request,  response);
				if (result == null) {
					result = new Response(true);
				}
				response.getWriter().write(new Gson().toJson(result));
			}
			else {
				throw new IllegalArgumentException("Could not find method in class: " 
						+ getClass().getSimpleName() + " annotated with @Get(\"" + method + "\")");
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public abstract void onGet(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	
	@Override
	public final void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String method = request.getParameter("method");
			if (postProcessors.containsKey(method)) {
				Object result = postProcessors.get(method).invoke(this, request,  response);
				if (result == null) {
					result = new Response(true);
				}
				response.getWriter().write(new Gson().toJson(result));
			}
			else {
				throw new IllegalArgumentException("Could not find method in class: " 
						+ getClass().getSimpleName() + " annotated with @Get(\"" + method + "\")");
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void render(String template) throws IOException {
		getRenderer().render(template);
	}
	
	public Renderer getRenderer() {
		return renderers.get();
	}
	
}
