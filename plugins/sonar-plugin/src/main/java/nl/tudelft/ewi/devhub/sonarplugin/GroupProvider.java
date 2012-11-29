package nl.tudelft.ewi.devhub.sonarplugin;

import java.util.Collection;

import org.sonar.api.security.DefaultGroups;
import org.sonar.api.security.ExternalGroupsProvider;

import com.google.common.collect.ImmutableSet;

public class GroupProvider extends ExternalGroupsProvider {

	@Override
	public Collection<String> doGetGroups(String username) {
		return ImmutableSet.of(DefaultGroups.ADMINISTRATORS, DefaultGroups.USERS);
	}

}
