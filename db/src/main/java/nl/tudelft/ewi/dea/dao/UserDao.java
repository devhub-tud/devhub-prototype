package nl.tudelft.ewi.dea.dao;

import java.util.List;

import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(UserDaoImpl.class)
public interface UserDao extends Dao<User> {

	User findByEmail(final String email);

	List<User> findByEmailSubString(final String email);

}
