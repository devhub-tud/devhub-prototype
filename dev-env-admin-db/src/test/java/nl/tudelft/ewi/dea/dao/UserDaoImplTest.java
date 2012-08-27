package nl.tudelft.ewi.dea.dao;

import static com.google.inject.Guice.createInjector;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;
import nl.tudelft.ewi.dea.di.DatabaseModule;
import nl.tudelft.ewi.dea.model.User;

import org.junit.Test;
import org.slf4j.Logger;

import com.google.inject.Injector;

public class UserDaoImplTest {

	private static final Logger LOG = getLogger(UserDaoImplTest.class);

	@Test
	public void whenAUserIsSavedItCanAlsoBeFound() {
		LOG.debug("### Initializing Guice");
		Injector injector = createInjector(new DatabaseModule("test-h2"));

		LOG.debug("### Initializing Entity manager");
		injector.getInstance(DatabaseStartStopHandler.class).start();

		LOG.debug("### Getting the DAO");
		UserDao dao = injector.getInstance(UserDao.class);

		User user = new User(0, "Alex", "alex@nederlof.com");

		dao.persist(user);

		User foundUser = dao.findByEmail("alex@nederlof.com");

		assertThat(foundUser, is(user));

	}
}
