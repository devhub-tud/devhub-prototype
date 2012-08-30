package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(UserDaoImpl.class)
public interface UserDao extends Dao<User> {

	User findByEmail(final String email);

}
