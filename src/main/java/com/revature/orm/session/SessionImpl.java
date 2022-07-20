package com.revature.orm.session;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.revature.orm.ORMQuery;
import com.revature.orm.ORMSession;
import com.revature.orm.ORMTransaction;
import com.revature.orm.ParsedObject;
import com.revature.orm.connection.ConnectionManager;
import com.revature.orm.connection.RevConnectionManager;
import com.revature.orm.connection.StatementWriter;
import com.revature.orm.connection.WriterFactory;

public class SessionImpl implements ORMSession {
	private Properties props;
	private ConnectionManager connMgr;
	
	public SessionImpl() throws SQLException {
		props = new Properties();
		
		InputStream propsFile = SessionImpl.class.getClassLoader()
				.getResourceAsStream("database.properties");
		try {
			props.load(propsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String dbUrl = props.getProperty("url");
		String dbUser = props.getProperty("usr");
		String dbPass = props.getProperty("psw");
		String dbDriver = props.getProperty("drv");
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		connMgr = RevConnectionManager.getConnectionManager(dbUrl, dbUser, dbPass);
	}

	@Override
	public <T> ORMTransaction<T> beginTransaction(Class<T> type) {
		StatementWriter<T> writer = WriterFactory.getSQLWriter(props.getProperty("drv"), type);
		List<Object> stmts = new LinkedList<>();
		
		return new TransactionImpl<T>(connMgr.getConnection(), writer, stmts);
	}

	@Override
	public <T> ORMQuery<T> createQuery(Class<T> type) {
		StatementWriter<T> writer = WriterFactory.getSQLWriter(props.getProperty("drv"), type);
		ParsedObject parsedObj = new ParsedObject(type);
		
		return new QueryImpl<T>(connMgr.getConnection(), writer, parsedObj);
	}

}