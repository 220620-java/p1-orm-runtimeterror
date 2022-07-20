package com.revature.orm.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface StatementWriter<T> {
	public PreparedStatement insert(T obj, Connection conn) throws SQLException;
	public PreparedStatement findById(Object id, Connection conn) throws SQLException;
	public PreparedStatement findBy(String field, Object value, Connection conn) throws SQLException;
	public String findAll();
	public PreparedStatement update(T obj, Connection conn) throws SQLException;
	public PreparedStatement delete(T obj, Connection conn) throws SQLException;
}