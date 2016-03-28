package org.mycat.web.task.common;

import java.util.Map;

import org.hx.rainbow.common.dao.Dao;

public class SqliteStore {
	private volatile static SqliteStore sqliteStore = null;
	private SqliteStore(){};
	
	public static SqliteStore getInstance(){
		if(sqliteStore == null){
			synchronized (SqliteStore.class) {
				if(sqliteStore == null){
					sqliteStore = new SqliteStore(); 
				}
			}
		}
		return sqliteStore;
	}
	
	public synchronized void insert(Dao dao, String namespace,String statement, Map<String,Object> paramData){
		dao.insert(namespace, statement, paramData);
	}
	
	public synchronized void delete(Dao dao, String namespace,String statement, Map<String,Object> paramData){
		dao.delete(namespace, statement, paramData);
	}
}
