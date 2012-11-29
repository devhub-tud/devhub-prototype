package nl.tudelft.ewi.devhub.services.versioncontrol.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SshKeyRepresentation extends SshKeyIdentifier {

	private final String key;

	public SshKeyRepresentation(SshKeyIdentifier id, String key) {
		super(id.getName(), id.getCreator());
		this.key = key;
	}

}
