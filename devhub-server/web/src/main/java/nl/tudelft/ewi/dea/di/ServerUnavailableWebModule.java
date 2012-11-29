package nl.tudelft.ewi.dea.di;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

public class ServerUnavailableWebModule extends ServletModule {

	@SuppressWarnings("serial")
	private static final class DevHubDownServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			req.getRequestDispatcher("/devhubdown.html").forward(req, resp);
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			doGet(req, resp);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ServerUnavailableWebModule.class);

	@Override
	protected void configureServlets() {
		LOG.warn("Configuring the Unavailable service");
		serveRegex("/(?!(devhubdown.html|img|js|css)).*$").with(new DevHubDownServlet());
	}

}
