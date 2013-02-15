package nl.tudelft.ewi.devhub.services.continuousintegration.models;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

import com.google.common.collect.Sets;

@Data
@EqualsAndHashCode(callSuper = true)
public class BuildProject extends BuildIdentifier {

	@Setter(AccessLevel.NONE) private final Collection<ServiceUser> members;

	private final String sourceCodeUrl;

	public BuildProject(BuildIdentifier id, String sourceCodeUrl) {
		super(id.getName(), id.getCreator());
		members = Sets.newHashSet();
		this.sourceCodeUrl = sourceCodeUrl;
	}

	public void addMember(ServiceUser user) {
		members.add(user);
	}

}
