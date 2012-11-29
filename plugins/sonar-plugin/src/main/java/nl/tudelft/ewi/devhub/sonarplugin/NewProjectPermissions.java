package nl.tudelft.ewi.devhub.sonarplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Resource;
import org.sonar.api.security.ResourcePermissions;

public class NewProjectPermissions implements ResourcePermissions {

	private static final Logger LOG = LoggerFactory.getLogger(NewProjectPermissions.class);

	public boolean hasRoles(Resource resource) {
		LOG.info("Has roles returns true for {} {} {}", new Object[] {resource.getClass(), resource.getKey(), resource});
		return true;
	}

	public void grantDefaultRoles(Resource resource) {
		LOG.info("Grant roles for {} {} {}", new Object[] {resource.getClass(), resource.getKey(), resource});
	}

	public void grantUserRole(Resource resource, String login, String role) {
		LOG.info("grantUserRole(login={}, role={}) for {} {} {}", new Object[] {login, role, resource.getClass(), resource.getKey(), resource});
	}

	public void grantGroupRole(Resource resource, String groupName, String role) {
		LOG.info("grantGroupRole(groupName={}, role={}) for {} {} {}", new Object[] {groupName, role, resource.getClass(), resource.getKey(), resource});

	}

}
