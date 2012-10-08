package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectInvitationDaoImpl.class)
public interface ProjectInvitationDao extends Dao<ProjectInvitation> {

	ProjectInvitation findByProjectAndUser(final Project project, final User user) throws NoResultException;

	List<ProjectInvitation> findByProject(final Project project);

	List<ProjectInvitation> findByUser(final User user);

}
