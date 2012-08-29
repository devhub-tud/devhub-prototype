package nl.tudelft.ewi.dea;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.UserFactory;

public class TestDataLoader {

	private static final Logger LOG = LoggerFactory.getLogger(TestDataLoader.class);

	private final UserFactory userFactory;
	private final UserDao userDao;

	@Inject
	TestDataLoader(UserDao userDao, UserFactory userFactory) {
		this.userDao = userDao;
		this.userFactory = userFactory;
	}

	public void insertTestData() {
		createAdminUser();
	}

	private void createAdminUser() {
		LOG.debug("Creating admin user");
		User adminUser = userFactory.createUser("test@test.com", "Administrator", "test");
		userDao.persist(adminUser);
	}

}
