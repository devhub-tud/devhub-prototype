package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.PasswordResetToken;

import com.google.inject.ImplementedBy;
import com.google.inject.persist.Transactional;

@ImplementedBy(PasswordResetTokenDaoImpl.class)
public interface PasswordResetTokenDao extends Dao<PasswordResetToken> {

	PasswordResetToken findByToken(String token);

}