package nl.tudelft.ewi.dea.dao;

import java.util.List;

import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(SshKeyDaoImpl.class)
public interface SshKeyDao extends Dao<SshKey> {

	List<SshKey> list(User user);

}
