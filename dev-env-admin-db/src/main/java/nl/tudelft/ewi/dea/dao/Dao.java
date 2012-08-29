package nl.tudelft.ewi.dea.dao;

interface Dao<T> {

	T findById(long id) throws UserNotFoundException;

	void persist(T t);
}
