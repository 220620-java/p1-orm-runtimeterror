package com.revature.orm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RevConnectionManager implements ConnectionManager {
	private static RevConnectionManager connMgr;
	private Map<Connection, Boolean> connectionPool = new HashMap<>();
	private final int POOL_SIZE = 10;
	
	private RevConnectionManager(String url, String user, String pass) throws SQLException {
		for (int i = 0; i<POOL_SIZE; i++) {
			Connection conn = DriverManager.getConnection(url, user, pass);
			connectionPool.put(conn, false);
		}
	}
	
	public static synchronized RevConnectionManager getConnectionManager(String url, String user, String pass) throws SQLException {
		if (connMgr==null) {
			connMgr = new RevConnectionManager(url, user, pass);
		}
		return connMgr;
	}
	
	public static synchronized RevConnectionManager getConnectionManager() {
		return connMgr;
	}

	@Override
	public Connection getConnection() {
		for (Connection conn : connectionPool.keySet()) {
			// if the value is false, the connection is not in use
			if (!connectionPool.get(conn)) {
				connectionPool.put(conn, true);
				return conn;
			}
		}
		return null;
	}

	@Override
	public boolean releaseConnection(Connection conn) {
		if (connectionPool.containsKey(conn)) {
			connectionPool.put(conn, false);
		}
		return false;
	}
	
}