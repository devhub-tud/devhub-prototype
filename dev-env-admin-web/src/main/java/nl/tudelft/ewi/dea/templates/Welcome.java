package nl.tudelft.ewi.dea.templates;

import com.google.sitebricks.At;

@At("/")
public class Welcome {
	
	private String message = "Hello";

	public String getMessage() {
		return message;
	}
}
