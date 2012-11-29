package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.metrics.MetricGroup;

import com.google.inject.persist.Transactional;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.MetricsRegistry;

public class StatisticDao {

	static final String COUNT_UNREGISTERED = "SELECT count(c.id) FROM RegistrationToken c";
	static final String COUNT_COURSES = "SELECT count(c.id) FROM Course c";
	static final String COUNT_PROJECTS = "SELECT count(p.id) FROM Project p";
	static final String COUNT_USERS = "SELECT count(u.id) FROM User u";

	private Provider<EntityManager> em;

	@Inject
	StatisticDao(final Provider<EntityManager> em, MetricsRegistry registry) {
		this.em = em;
		registry.newGauge(MetricGroup.APP.newName("Number of users"), new Gauge<Long>() {

			@Override
			public Long value() {
				return runCountQuery(COUNT_USERS);
			}
		});

		registry.newGauge(MetricGroup.APP.newName("Number of projects"), new Gauge<Long>() {
			@Override
			public Long value() {
				return runCountQuery(COUNT_PROJECTS);
			}

		});

		registry.newGauge(MetricGroup.APP.newName("Number of courses"), new Gauge<Long>() {
			@Override
			public Long value() {
				return runCountQuery(COUNT_COURSES);
			}

		});

		registry.newGauge(MetricGroup.APP.newName("Unconfirmed registrations"), new Gauge<Long>() {
			@Override
			public Long value() {
				return runCountQuery(COUNT_UNREGISTERED);
			}
		});
	}

	@Transactional
	Long runCountQuery(String query) {
		Long count = (Long) em.get().createQuery(query).getSingleResult();
		return count;
	}

}
