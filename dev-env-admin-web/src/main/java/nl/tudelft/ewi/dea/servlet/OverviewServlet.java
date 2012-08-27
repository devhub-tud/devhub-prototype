package nl.tudelft.ewi.dea.servlet;

import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.ewi.dea.servlet.util.Get;
import nl.tudelft.ewi.dea.servlet.util.Post;
import nl.tudelft.ewi.dea.servlet.util.Response;
import nl.tudelft.ewi.dea.servlet.util.Servlet;

import com.google.common.collect.Lists;

@Singleton
@SuppressWarnings("serial")
public class OverviewServlet extends Servlet {

	@Override
	public void onGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Project> invitations = Lists.newArrayList(
				new Project("IN2610 Advanced Algorithms - Group 22")
		);
		
		List<Project> projects = Lists.newArrayList(
				new Project("IN2010 Algorithms - Group 5"),
				new Project("IN2105 Software Quality & Testing - Group 8"),
				new Project("IN2505 Context project 1 - Group 7")
		);
		
		getRenderer()
			.setValue("invitations", invitations)
			.setValue("projects", projects)
			.setValue("scripts", Lists.newArrayList("overview.js"))
			.render("index.tpl");
	}
	
	@Get
	public Response checkProjectName(HttpServletRequest request, HttpServletResponse response) {
		boolean isValid = false;
		String name = request.getParameter("name");
		if (name != null && name.matches("[a-zA-Z0-9]{4,}")) {
			isValid = true;
		}
		return new Response(isValid);
	}
	
	@Post
	public Response provisionNewProject(HttpServletRequest request, HttpServletResponse response) {
		
		
		
		return new Response(true);
	}
	
	public class Project {
		private final String name;
		
		public Project(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
}
