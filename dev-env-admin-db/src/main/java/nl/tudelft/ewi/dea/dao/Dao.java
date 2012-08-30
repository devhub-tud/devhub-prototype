package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * @param <T> The JPA entity.
 */
interface Dao<T> {

	List<T> findAll();

	/**
	 * @see EntityManager#find(Class, Object)
	 */
	T findById(final long id);

	/**
	 * @see EntityManager#persist(Object)
	 */
	void persist(final T object);

	/**
	 * @see EntityManager#remove(Object)
	 */
	void remove(final T object);

}
