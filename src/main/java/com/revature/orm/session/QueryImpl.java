package com.revature.orm.session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.revature.orm.ORMQuery;
import com.revature.orm.ParsedObject;
import com.revature.orm.connection.ConnectionManager;
import com.revature.orm.connection.RevConnectionManager;
import com.revature.orm.connection.StatementWriter;
import com.revature.orm.exceptions.UnsupportedModelException;

public class QueryImpl<T> implements ORMQuery<T> {
	private final Connection conn;
	private final StatementWriter<T> writer;
	private final ParsedObject parsedObj;

	QueryImpl(Connection conn, StatementWriter<T> writer, ParsedObject parsedObj) {
		this.conn = conn;
		this.writer = writer;
		this.parsedObj = parsedObj;
	}

	@Override
	public T findById(Object id) throws SQLException {
		T obj = null;

		try (PreparedStatement stmt = writer.findById(id, conn)) {
			stmt.setObject(1, id);
			ResultSet resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				try {
					obj = (T) setValues(this.parsedObj, resultSet);
				} catch (Exception e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}

		closeConn();
		return obj;
	}

	@Override
	public T findOneBy(String field, Object value) throws SQLException {
		T obj = null;

		try (PreparedStatement stmt = writer.findBy(field, value, conn)) {
			stmt.setObject(1, value);
			ResultSet resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				try {
					obj = (T) setValues(this.parsedObj, resultSet);
				} catch (IllegalAccessException | InstantiationException e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}

		closeConn();
		return obj;
	}

	@Override
	public List<T> findAllBy(String field, Object value) throws SQLException {
		List<T> list = new ArrayList<>();

		try (PreparedStatement stmt = writer.findBy(field, value, conn)) {
			stmt.setObject(1, value);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				try {
					T obj = (T) setValues(this.parsedObj, resultSet);
					list.add(obj);
				} catch (IllegalAccessException | InstantiationException e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}

		closeConn();
		return list;
	}

	@Override
	public List<T> findAll() throws SQLException {
		List<T> list = new ArrayList<>();

		try (Statement stmt = conn.createStatement()) {
			ResultSet resultSet = stmt.executeQuery(writer.findAll());

			while (resultSet.next()) {
				try {
					T obj = (T) setValues(this.parsedObj, resultSet);
					list.add(obj);
				} catch (IllegalAccessException | InstantiationException e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}

		closeConn();
		return list;
	}

	private void closeConn() {
		ConnectionManager mgr = RevConnectionManager.getConnectionManager();
		mgr.releaseConnection(conn);
	}

	private Object setValues(ParsedObject parsedObj, ResultSet resultSet)
			throws SQLException, InstantiationException, IllegalAccessException {
		Object obj = parsedObj.getOriginalType().newInstance();

		try {
			for (String fieldName : parsedObj.getColumns().keySet()) {
				String columnName = parsedObj.getColumns().get(fieldName);
				Object columnValue = resultSet.getObject(parsedObj.getTableName() + "_" + columnName);

				String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);

				if (fieldExists(obj, fieldName) && obj.getClass().getDeclaredField(fieldName).isAccessible()) {
					obj.getClass().getDeclaredField(fieldName).set(obj, columnValue);
				} else if (methodExists(obj, methodName, columnValue.getClass())) {
					obj.getClass().getDeclaredMethod(methodName, columnValue.getClass()).invoke(obj, columnValue);
				} else {
					for (ParsedObject relationshipField : parsedObj.getRelationships().keySet()) {
						if (fieldName.equals(getCamelCase(relationshipField.getTableName()))) {
							Object subObj = setValues(relationshipField, resultSet);
							if (methodExists(obj, methodName, subObj.getClass())) {
								obj.getClass().getDeclaredMethod(methodName, subObj.getClass()).invoke(obj, subObj);
							}
						}
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			UnsupportedModelException e1 = new UnsupportedModelException(
					"Your model has inaccessible fields (private and missing setter method).");
			e1.initCause(e);
			throw e1;
		}
		return obj;
	}

	private Object getCamelCase(String snakeCase) {
		// split the words on the capital letters
		String[] words = snakeCase.toString().split("[_]");

		// start building the new snake case string
		StringBuilder finalFieldName = new StringBuilder(words[0]);
		for (int i = 1; i < words.length; i++) {
			finalFieldName.append(words[i].toUpperCase().charAt(0) + words[i].substring(1));
		}
		return finalFieldName.toString();
	}

	private boolean fieldExists(Object object, String fieldName) {
		try {
			object.getClass().getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	private boolean methodExists(Object object, String methodName, Class<?> type) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			if (method.getName().contains(methodName)) {
				if (method.getParameterTypes().length==1 && method.getParameterTypes()[0].equals(type)) {
					return true;
				}
			}
		}
		return false;
	}

}