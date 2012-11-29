package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.util.concurrent.Future;

import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.CreatedRepositoryResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

public interface VersionControlService extends Service {

	Future<CreatedRepositoryResponse> createRepository(RepositoryRepresentation repository);

	Future<ServiceResponse> removeRepository(RepositoryIdentifier repository);

	Future<ServiceResponse> addSshKey(SshKeyRepresentation sshKey);

	Future<ServiceResponse> removeSshKeys(SshKeyIdentifier... sshKeys);

}
