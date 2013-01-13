package nl.tudelft.ewi.devhub.services.versioncontrol.models;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

import com.google.common.collect.Sets;

@Data
@EqualsAndHashCode
public class RepositoryRepresentation {

	private final String repoName;

	@Setter(AccessLevel.NONE) private final Collection<ServiceUser> members;

	public RepositoryRepresentation(String repoName, ServiceUser... members) {
		this.repoName = repoName;
		this.members = Sets.newHashSet(members);
	}

	public void addMember(ServiceUser user) {
		members.add(user);
	}

}
