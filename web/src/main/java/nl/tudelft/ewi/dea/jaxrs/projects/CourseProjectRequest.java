package nl.tudelft.ewi.dea.jaxrs.projects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseProjectRequest {

	private final Long course = null;

	private final String[] invites = null;

}