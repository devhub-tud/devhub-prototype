package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.util.List;

import lombok.Data;
import nl.tudelft.ewi.dea.model.Course;

import com.google.common.collect.Lists;

@Data
public class CourseDto {

	public static List<CourseDto> convert(List<Course> courses) {
		List<CourseDto> result = Lists.newArrayList();
		for (Course course : courses) {
			result.add(new CourseDto(course.getId(), course.getName()));
		}
		return result;
	}

	private final long id;
	private final String name;

}
