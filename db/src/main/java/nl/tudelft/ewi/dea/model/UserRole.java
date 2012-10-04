package nl.tudelft.ewi.dea.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * The security role a {@link User} can have.
 */
public enum UserRole {

	ADMIN("Administrator"),
	USER("User");

	private final String displayName;

	private UserRole(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static final String ROLE_USER = "USER";
	public static final String ROLE_ADMIN = "ADMIN";

	public static final ImmutableSet<String> ALL_ROLES = ImmutableSet.of(ADMIN.name(), USER.name());
	public static final ImmutableSet<String> ADMIN_ROLES = ALL_ROLES;
	public static final ImmutableSet<String> USER_ROLES = ImmutableSet.of(USER.name());

	public static Set<String> getRolesFor(final User user) {
		if (user.isAdmin()) {
			return ADMIN_ROLES;
		} else {
			return USER_ROLES;
		}
	}
}
