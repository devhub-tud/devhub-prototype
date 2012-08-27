package nl.tudelft.ewi.dea.dao;

interface Dao<T> {

	T getById(long id);

	void persist(T t);
}
