package nl.tudelft.ewi.devhub.services.continuousintegration.models;

import lombok.Data;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

@Data
public class BuildIdentifier {

	private final String name;
	private final ServiceUser creator;

}
