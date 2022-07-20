package com.revature.orm.connection;

import java.sql.Connection;

public interface ConnectionManager {
	/**
	 * Returns a Connection if one is available, or null 
	 * if there are currently no connections available.
	 * @return a Connection or null
	 */
	public Connection getConnection();
	/**
	 * Returns the given Connection to the connection pool 
	 * so that it can be used elsewhere.
	 * @param conn the Connection to be released
	 * @return true if the connection was released, false if it was not 
	 * returned or if it didn't exist in the pool
	 */
	public boolean releaseConnection(Connection conn);
}