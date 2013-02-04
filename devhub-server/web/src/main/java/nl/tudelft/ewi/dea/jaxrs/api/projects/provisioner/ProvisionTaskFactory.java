package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

public interface ProvisionTaskFactory {

	ProvisionTask create(ProvisioningRequest request);

}
