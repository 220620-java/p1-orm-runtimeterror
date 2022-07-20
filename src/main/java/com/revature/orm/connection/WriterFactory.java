package com.revature.orm.connection;

import com.revature.orm.ParsedObject;
import com.revature.orm.connection.postgres.PostgresWriter;

public class WriterFactory {
	/**
	 * 
	 * @param driver
	 * @param url
	 * @param user
	 * @param pass
	 * @return
	 */
	public static <T> StatementWriter<T> getSQLWriter(String driver, Class<T> type) {
		switch (driver) {
		case "org.postgresql.Driver":
			ParsedObject parsedObject = new ParsedObject(type);
			return new PostgresWriter<>(parsedObject);
		default:
			return null;
		}
	}
}