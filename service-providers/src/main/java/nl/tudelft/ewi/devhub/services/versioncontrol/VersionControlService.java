package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.util.concurrent.Future;

import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

public interface VersionControlService extends Service {

	Future<ServiceResponse> createRepository(RepositoryRepresentation repository);

	Future<ServiceResponse> removeRepository(RepositoryIdentifier repository);

}
