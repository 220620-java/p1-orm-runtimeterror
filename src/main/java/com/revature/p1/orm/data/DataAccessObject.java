package com.revature.p1.orm.data;

import java.util.List;


public interface DataAccessObject<T> {

	public T create(T t);
	
	public T findById(T t);
	
	public List<T> findAll(Class<?> t);
	
	public void update(T t);

	public void delete(T t);
}