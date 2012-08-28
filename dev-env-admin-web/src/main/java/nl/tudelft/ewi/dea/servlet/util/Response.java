package nl.tudelft.ewi.dea.servlet.util;

public class Response {
	
	private final boolean ok;
	private final String message;
	
	public Response(boolean ok) {
		this(ok, null);
	}
	
	public Response(boolean ok, String message) {
		this.ok = ok;
		this.message = message;
	}

	public boolean isOk() {
		return ok;
	}
	
	public String getMessage() {
		return message;
	}
	
}
