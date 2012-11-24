package nl.tudelft.ewi.dea.dao;

import static nl.tudelft.ewi.dea.dao.StatisticDao.COUNT_COURSES;
import static nl.tudelft.ewi.dea.dao.StatisticDao.COUNT_PROJECTS;
import static nl.tudelft.ewi.dea.dao.StatisticDao.COUNT_UNREGISTERED;
import static nl.tudelft.ewi.dea.dao.StatisticDao.COUNT_USERS;

import org.junit.Before;
import org.junit.Test;

public class StatisticDaoTest extends DatabaseTest {

	private StatisticDao dao;

	@Before
	public void getDao() {
		dao = getInstance(StatisticDao.class);
	}

	@Test
	public void verifyCountUnregisteredWorks() {
		dao.runCountQuery(COUNT_UNREGISTERED);
	}

	@Test
	public void verifyCountCourses() {
		dao.runCountQuery(COUNT_COURSES);
	}

	@Test
	public void verifyCountProjects() {
		dao.runCountQuery(COUNT_PROJECTS);
	}

	@Test
	public void verifyCountUsersWorks() {
		dao.runCountQuery(COUNT_USERS);
	}

}
