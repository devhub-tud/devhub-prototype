package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.services.ServicesBackend;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

public class LeaveProjectTask implements Runnable {

	private final Provisioner provisioner;
	private final ProjectMembershipDao membershipDao;
	private final ServicesBackend backend;
	private final LeaveRequest request;

	@Inject
	public LeaveProjectTask(
			ProjectMembershipDao membershipDao,
			ServicesBackend backend,
			Provisioner provisioner,
			@Assisted LeaveRequest request) {

		this.membershipDao = membershipDao;
		this.backend = backend;
		this.provisioner = provisioner;
		this.request = request;
	}

	@Override
	@Transactional
	public void run() {
		User user = request.getUser();
		String netId = user.getNetId();

		provisioner.updateProjectState(netId, new State(false, false, "Preparing to leave project..."));
		membershipDao.remove(membershipDao.find(request.getProjectId(), request.getUser()));

		provisioner.updateProjectState(netId, new State(false, false, "Deregistering membership from services..."));
		backend.removeMembers(request.getProjectId(), new ServiceUser(user.getNetId(), user.getDisplayName(), user.getEmail()));

		provisioner.updateProjectState(netId, new State(true, false, "User has left project!"));
	}
}