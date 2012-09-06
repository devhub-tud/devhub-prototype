package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * @param <T> The JPA entity type.
 */
interface Dao<T> {

	List<T> findAll();

	/**
	 * Find the object of type T with the given identifier.
	 * 
	 * @param id The identifier to search for.
	 * 
	 * @throws NoResultException If no object was found for the given identifier.
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
