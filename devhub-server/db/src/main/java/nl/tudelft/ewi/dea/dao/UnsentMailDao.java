package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import com.google.inject.ImplementedBy;

@ImplementedBy(UnsentMailDaoImpl.class)
public interface UnsentMailDao extends Dao<UnsentMailAsJson> {

	UnsentMailAsJson persist(String emailAsJson);

	void remove(long id);

}