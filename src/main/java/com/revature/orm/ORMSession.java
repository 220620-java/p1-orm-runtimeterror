package com.revature.orm;

public interface ORMSession {
	public <T> ORMTransaction<T> beginTransaction(Class<T> type);
	public <T> ORMQuery<T> createQuery(Class<T> type);
}