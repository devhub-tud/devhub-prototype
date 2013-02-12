package nl.tudelft.ewi.devhub.services.models;

import nl.tudelft.ewi.dea.model.User;
import lombok.Data;

@Data
public class ServiceUser {

	private final String identifier;
	private final String fullName;
	private final String email;

	@Override
	public String toString() {
		return fullName;
	}

	public static ServiceUser fromUser(User user) {
		return new ServiceUser(user.getNetId(), user.getDisplayName(), user.getEmail());
	}

}
