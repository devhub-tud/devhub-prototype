package nl.tudelft.ewi.dea.servlet;

import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			.render("index.tpl");
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
