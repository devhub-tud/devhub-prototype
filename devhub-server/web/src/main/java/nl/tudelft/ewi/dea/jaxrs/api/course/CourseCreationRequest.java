package nl.tudelft.ewi.dea.jaxrs.api.course;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CourseCreationRequest {

	private String name;
	private String templateUrl;

}
