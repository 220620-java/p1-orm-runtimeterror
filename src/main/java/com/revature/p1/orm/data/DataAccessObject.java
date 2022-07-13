package com.revature.p1.orm.data;

import java.util.List;
public interface DataAccessObject<T> {
public T create(T t);
	
	public T findById(int id);
	
	public List<T> findAll();

	public void update(T t);

	public void delete(T t);

}
