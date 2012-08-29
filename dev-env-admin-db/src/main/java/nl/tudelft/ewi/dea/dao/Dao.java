package nl.tudelft.ewi.dea.dao;

interface Dao<T> {

	T getById(long id) throws UserNotFoundException;

	void persist(T t);
}
