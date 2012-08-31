package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.Project;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectDaoImpl.class)
public interface ProjectDao extends Dao<Project> {

}
