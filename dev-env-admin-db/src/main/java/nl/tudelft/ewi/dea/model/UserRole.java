package nl.tudelft.ewi.dea.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * The security role a {@link User} can have.
 */
public enum UserRole {

	ADMIN, USER;

	public static final ImmutableSet<String> ALL_ROLES = ImmutableSet.of(ADMIN.name(), USER.name());
	public static final ImmutableSet<String> ADMIN_ROLES = ALL_ROLES;
	public static final ImmutableSet<String> USER_ROLES = ImmutableSet.of(USER.name());

	public static Set<String> getRolesFor(User user) {
		if (user.isAdmin()) {
			return ADMIN_ROLES;
		} else {
			return USER_ROLES;
		}
	}
}
