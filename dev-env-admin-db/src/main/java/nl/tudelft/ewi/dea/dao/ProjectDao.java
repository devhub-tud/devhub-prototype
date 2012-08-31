package nl.tudelft.ewi.dea.dao;

import java.util.List;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectDaoImpl.class)
public interface ProjectDao extends Dao<Project> {

	List<Project> findByUser(final User user);

}