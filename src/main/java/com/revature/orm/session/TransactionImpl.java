package com.revature.orm.session;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.revature.orm.ORMTransaction;
import com.revature.orm.ParsedObject;
import com.revature.orm.annotations.Relationship;
import com.revature.orm.connection.ConnectionManager;
import com.revature.orm.connection.RevConnectionManager;
import com.revature.orm.connection.StatementWriter;
import com.revature.orm.exceptions.InvalidKeywordException;
import com.revature.orm.exceptions.UnsupportedModelException;

public class TransactionImpl<T> implements ORMTransaction<T> {
	private Connection conn;
	private final StatementWriter<T> writer;
	// objects will be savepoint names (strings) and preparedstatements
	private List<Object> stmts;
	private List<Savepoint> savepoints = new LinkedList<>();
	private List<Object> generatedKeys = new LinkedList<>();

	TransactionImpl(Connection conn, StatementWriter<T> writer, List<Object> stmts) {
		this.conn = conn;
		this.writer = writer;
		this.stmts = stmts;
	}

	@Override
	public ORMTransaction<T> addStatement(String keyword, Object obj) throws SQLException {
		keyword = keyword.toUpperCase();
		PreparedStatement stmt = null;
		switch (keyword) {
		case "INSERT":
			stmt = writer.insert((T) obj, conn);
			stmt = setInsertValues(obj, stmt);
			break;
		case "UPDATE":
			stmt = writer.update((T) obj, conn);
			stmt = setUpdateValues(obj, stmt);
			break;
		case "DELETE":
			stmt = writer.delete((T) obj, conn);
			Object primaryKeyValue2 = getPkValue(obj);
			stmt.setObject(1, primaryKeyValue2);
			break;
		default:
			throw new InvalidKeywordException();
		}
		if (stmt != null) {
			stmts.add(stmt);
		}

		return new TransactionImpl<T>(conn, writer, stmts);
	}

	private PreparedStatement setUpdateValues(Object obj, PreparedStatement stmt) throws SQLException {
		int parameterIndex = 1;
		ParsedObject parsedObj = new ParsedObject(obj.getClass());
		try {
			for (String fieldName : parsedObj.getColumns().keySet()) {
				if (!fieldName.equals(parsedObj.getPrimaryKeyField())) {
					stmt = setValue(fieldName, obj, stmt, parameterIndex++, parsedObj);
				}
			}
			stmt.setObject(parameterIndex++, getPkValue(obj));
		} catch (Exception e) {
			UnsupportedModelException e1 = new UnsupportedModelException(
					"Your model has inaccessible fields (private and missing getter method).");
			e1.initCause(e);
			throw e1;
		}

		return stmt;
	}

	private PreparedStatement setInsertValues(Object obj, PreparedStatement stmt) throws SQLException {
		int parameterIndex = 1;
		ParsedObject parsedObj = new ParsedObject(obj.getClass());
		try {
			for (String fieldName : parsedObj.getColumns().keySet()) {
				if (!fieldName.equals(parsedObj.getPrimaryKeyField())) {
					stmt = setValue(fieldName, obj, stmt, parameterIndex++, parsedObj);
				}
			}
		} catch (Exception e) {
			UnsupportedModelException e1 = new UnsupportedModelException(
					"Your model has inaccessible fields (private and missing getter method).");
			e1.initCause(e);
			throw e1;
		}

		return stmt;
	}

	private PreparedStatement setValue(String fieldName, Object obj, PreparedStatement stmt, int parameterIndex,
			ParsedObject parsedObj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, InvocationTargetException, NoSuchMethodException, SQLException {
		Object value = null;
		if (fieldExists(obj, fieldName) && obj.getClass().getDeclaredField(fieldName).isAccessible()) {
			value = obj.getClass().getDeclaredField(fieldName).get(obj);
		} else {
			String methodName = "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
			value = obj.getClass().getDeclaredMethod(methodName).invoke(obj);
		}

		if (fieldExists(obj, fieldName)
				&& obj.getClass().getDeclaredField(fieldName).isAnnotationPresent(Relationship.class)) {
			value = getPkValue(value);
		}

		stmt.setObject(parameterIndex, value);
		return stmt;
	}

	private Object getPkValue(Object obj) {
		Object primaryKeyValue = null;

		try {
			ParsedObject parsedObj = new ParsedObject(obj.getClass());
			String pkFieldName = parsedObj.getPrimaryKeyField();
			if (fieldExists(obj, pkFieldName) && obj.getClass().getDeclaredField(pkFieldName).isAccessible()) {
				primaryKeyValue = obj.getClass().getDeclaredField(pkFieldName).get(obj);
			} else {
				String methodName = "get" + pkFieldName.toUpperCase().charAt(0) + pkFieldName.substring(1);
				primaryKeyValue = obj.getClass().getDeclaredMethod(methodName).invoke(obj);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			UnsupportedModelException e1 = new UnsupportedModelException(
					"Your model has inaccessible fields (private and missing getter method).");
			e1.initCause(e);
			throw e1;
		}

		return primaryKeyValue;
	}

	@Override
	public int execute() throws SQLException {
		conn.setAutoCommit(false);

		int rowsUpdated = 0;
		for (Object stmtOrSvpt : stmts) {
			if (stmtOrSvpt instanceof PreparedStatement) {
				PreparedStatement stmt = (PreparedStatement) stmtOrSvpt;
				rowsUpdated += stmt.executeUpdate();
				ResultSet resultSet = stmt.getGeneratedKeys();
				if (resultSet.next()) {
					generatedKeys.add(resultSet.getObject(1));
				}
			} else if (stmtOrSvpt instanceof String) {
				Savepoint svpt = conn.setSavepoint(stmtOrSvpt.toString());
				savepoints.add(svpt);
			}
		}

		return rowsUpdated;
	}

	@Override
	public List<Object> getGeneratedKeys() {
		return generatedKeys;
	}

	@Override
	public void commit() throws SQLException {
		conn.commit();
		closeConn();
	}

	@Override
	public void rollback() throws SQLException {
		conn.rollback();
		stmts = new LinkedList<>();
	}

	@Override
	public void rollbackToSavepoint(String name) throws SQLException {
		int index = stmts.indexOf(name);
		for (int i = index; i < stmts.size(); i++) {
			stmts.remove(i);
		}

		for (Savepoint svpt : savepoints) {
			if (svpt.getSavepointName().equals(name)) {
				conn.rollback(svpt);
				return;
			}
		}
		throw new SQLException("No savepoint found with that name.");
	}

	@Override
	public ORMTransaction<T> addSavepoint(String name) {
		stmts.add(name);
		return new TransactionImpl<T>(conn, writer, stmts);
	}

	private void closeConn() {
		ConnectionManager mgr = RevConnectionManager.getConnectionManager();
		mgr.releaseConnection(conn);
		conn = null;
	}

	@Override
	public void close() throws SQLException {
		closeConn();
	}

	private boolean fieldExists(Object object, String fieldName) {
		try {
			object.getClass().getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

}