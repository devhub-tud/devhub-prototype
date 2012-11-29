package nl.tudelft.ewi.devhub.services.versioncontrol.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatedRepositoryResponse extends ServiceResponse {

	private final String repositoryUrl;

	public CreatedRepositoryResponse(boolean success, String message, String repositoryUrl) {
		super(success, message);
		this.repositoryUrl = repositoryUrl;
	}

}
