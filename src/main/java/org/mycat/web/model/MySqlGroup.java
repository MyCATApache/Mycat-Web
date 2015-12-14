package org.mycat.web.model;

import java.util.LinkedList;
import java.util.List;

public class MySqlGroup {
	
	private Integer id;
	private List<MySqlRep> list=new LinkedList<MySqlRep>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<MySqlRep> getList() {
		return list;
	}
	public void setList(List<MySqlRep> list) {
		this.list = list;
	}

}
