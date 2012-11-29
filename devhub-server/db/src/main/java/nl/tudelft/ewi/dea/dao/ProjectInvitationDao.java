package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectInvitationDaoImpl.class)
public interface ProjectInvitationDao extends Dao<ProjectInvitation> {

	ProjectInvitation findByProjectAndEMail(Project project, String user) throws NoResultException;

	List<ProjectInvitation> findByProject(Project project);

	List<ProjectInvitation> findByUser(User user);

	/**
	 * If the user was invited before it was known to devhub, the id is set to
	 * <code>null</code>. This function replaces null with the actual user.
	 * 
	 * @param u The user that is now known to the system.
	 */
	void updateInvitesForNewUser(User u);

}
