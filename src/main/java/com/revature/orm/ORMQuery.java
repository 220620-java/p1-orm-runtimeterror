package com.revature.orm;

import java.sql.SQLException;
import java.util.List;

public interface ORMQuery<T> {
	/**
	 * Retrieves an object by the primary key column.
	 * 
	 * @param id the primary key value
	 * @return the object with that primary key
	 * @throws SQLException 
	 */
	public T findById(Object id) throws SQLException;
	/**
	 * Retrieves an object which has the specified field matching 
	 * the specified value. This method is designed for use with unique 
	 * fields; if it is used elsewhere, it will retrieve the first result.
	 * <br><br>
	 * The field name is object-oriented, so it should be a field on the 
	 * owning class or a subfield, like so:
	 * <table>
	 * <tr><td><b>name</b></td><td>the name field of the owning class</td></tr>
	 * <tr><td><b>address.street</b></td><td>the street field of the address field of the owning class</td></tr>
	 * </table>
	 * 
	 * @param field the name of the field to search by
	 * @param value the expected value of the specified field
	 * @return an object with the specified field matching the specified value
	 * @throws SQLException 
	 */
	public T findOneBy(String field, Object value) throws SQLException;
	/**
	 * Retrieves a list of objects which have the specified field matching 
	 * the specified value.
	 * <br><br>
	 * The field name is object-oriented, so it should be a field on the 
	 * owning class or a subfield, like so:
	 * <table>
	 * <tr><td><b>name</b></td><td>the name field of the owning class</td></tr>
	 * <tr><td><b>address.street</b></td><td>the street field of the address field of the owning class</td></tr>
	 * </table>
	 * 
	 * @param field the name of the field to search by
	 * @param value the expected value of the specified field
	 * @return a list of objects with the specified field matching the specified value
	 * @throws SQLException 
	 */
	public List<T> findAllBy(String field, Object value) throws SQLException;
	/**
	 * Retrieves a list of all objects of the query type.
	 * 
	 * @return a list of all objects of the query type
	 * @throws SQLException 
	 */
	public List<T> findAll() throws SQLException;
}