package nl.tudelft.ewi.dea.servlet.util;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This {@link HttpServlet} redirects the user to a different URL.
 * 
 * @author michael
 */
public class RedirectServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7809604171384361709L;
	
	private final String path;
	
	/**
	 * This constructs a new {@link RedirectServlet}.
	 * 
	 * @param path
	 * 		The path to redirect the user to.
	 */
	public RedirectServlet(String path) {
		this.path = path;
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		redirect(response);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		redirect(response);
	}

	private void redirect(HttpServletResponse response) throws IOException {
		response.sendRedirect(path);
	}
	
}
