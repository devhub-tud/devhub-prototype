package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Singleton
public class Provisioner {

	public static final Logger LOG = LoggerFactory.getLogger(Provisioner.class);

	private final ScheduledExecutorService executor;
	private final Cache<String, State> stateCache;
	private final ProjectMembershipDao membershipDao;
	private final ProvisionTaskFactory provisionTaskFactory;
	private final LeaveProjectTaskFactory leaveProjectTaskFactory;

	@Inject
	public Provisioner(
			ProvisionTaskFactory provisionTaskFactory,
			LeaveProjectTaskFactory leaveProjectTaskFactory,
			ProjectMembershipDao membershipDao,
			ScheduledExecutorService executor) {

		this.provisionTaskFactory = provisionTaskFactory;
		this.leaveProjectTaskFactory = leaveProjectTaskFactory;
		this.membershipDao = membershipDao;
		this.executor = executor;
		this.stateCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
	}

	public void provision(ProvisioningRequest request) {
		if (alreadyMemberOfCourseProject(request.getCreator(), request.getCourseId())) {
			throw new ProvisioningException("You're already a member of a project for this course!");
		}

		updateProjectState(request.getCreator().getNetId(), new State(false, false, "Preparing to provision project..."));
		executor.submit(provisionTaskFactory.create(request));
	}

	public void leave(LeaveRequest request) {
		if (!membershipDao.isMemberOf(request.getProjectId(), request.getUser())) {
			throw new ProvisioningException("You're not a member of the project!");
		}

		updateProjectState(request.getUser().getNetId(), new State(false, false, "Preparing to leave project..."));
		executor.submit(leaveProjectTaskFactory.create(request));
	}

	boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.hasEnrolled(course, currentUser);
	}

	void updateProjectState(String netId, State state) {
		stateCache.put(netId, state);
	}

	public State getState(String netId) {
		return stateCache.getIfPresent(netId);
	}

	public void shutdown(int timeout) throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
	}
}
