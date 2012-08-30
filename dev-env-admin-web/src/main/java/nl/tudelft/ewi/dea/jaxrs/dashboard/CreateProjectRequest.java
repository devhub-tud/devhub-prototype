package nl.tudelft.ewi.dea.jaxrs.dashboard;

public class CreateProjectRequest {
	private final String name;
	public CreateProjectRequest() {
		this.name = null;
	}
	public String getName() {
		return name;
	}
}