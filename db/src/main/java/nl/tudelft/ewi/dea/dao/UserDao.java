package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(UserDaoImpl.class)
public interface UserDao extends Dao<User> {

	/**
	 * Find the user that has the given email, or throw an exception.
	 * 
	 * @param email The email address to lookup. Must be non-<code>null</code>,
	 *           non-empty.
	 * 
	 * @return The corresponding {@link User}.
	 * 
	 * @throws NoResultException If no {@link User} corresponds to the given
	 *            email.
	 */
	User findByEmail(final String email);

	/**
	 * Find all {@link User}s which have given substring in one of their fields.
	 * 
	 * @param substring The substring to search for. Must be non-
	 *           <code>null</code> and non-empty, and may not contain '%'.
	 * 
	 * @return A {@link List} of {@link User}s that match the substring query.
	 */
	List<User> findBySubString(final String substring);

	/**
	 * Find all {@link User}s which have given email substring in their email
	 * field.
	 * 
	 * @param email The email substring to search for. Must be non-null and
	 *           non-empty, and may not contain '%'.
	 * 
	 * @return A {@link List} of matching {@link User}s.
	 */
	List<User> findByEmailSubString(final String email);

}
