package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.PasswordResetToken;

import com.google.inject.ImplementedBy;

@ImplementedBy(PasswordResetTokenDaoImpl.class)
public interface PasswordResetTokenDao extends Dao<PasswordResetToken> {

	PasswordResetToken findByToken(final String token);

	PasswordResetToken findByEmail(final String email);

}