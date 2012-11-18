package nl.tudelft.ewi.devhub.services.versioncontrol.models;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

import com.google.common.collect.Sets;

@Data
@EqualsAndHashCode(callSuper = true)
public class RepositoryRepresentation extends RepositoryIdentifier {

	private final String groupName;

	@Setter(AccessLevel.NONE) private final Collection<ServiceUser> members;

	public RepositoryRepresentation(RepositoryIdentifier id, String groupName) {
		super(id.getName(), id.getCreator());
		this.groupName = groupName;
		this.members = Sets.newHashSet(id.getCreator());
	}

	public void addMember(ServiceUser user) {
		members.add(user);
	}

}
