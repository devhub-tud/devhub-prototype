package nl.tudelft.ewi.devhub.services.versioncontrol.models;

import lombok.Data;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

@Data
public class RepositoryIdentifier {

	private final String name;
	private final ServiceUser creator;

}
