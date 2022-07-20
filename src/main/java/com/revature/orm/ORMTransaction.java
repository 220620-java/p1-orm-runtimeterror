package com.revature.orm;

import java.sql.SQLException;
import java.util.List;

public interface ORMTransaction<T> {

	public ORMTransaction<T> addStatement(String keyword, Object obj) throws SQLException;

	public int execute() throws SQLException;

	public List<Object> getGeneratedKeys();

	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void rollbackToSavepoint(String name) throws SQLException;

	public ORMTransaction<T> addSavepoint(String name);

	public void close() throws SQLException;
}