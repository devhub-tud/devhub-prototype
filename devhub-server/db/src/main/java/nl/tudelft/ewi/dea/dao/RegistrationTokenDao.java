package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.RegistrationToken;

import com.google.inject.ImplementedBy;

@ImplementedBy(RegistrationTokenDaoImpl.class)
public interface RegistrationTokenDao extends Dao<RegistrationToken> {

	RegistrationToken findByToken(final String token);

	RegistrationToken findByEmail(final String email);

}
