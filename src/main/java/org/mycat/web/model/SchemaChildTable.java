package org.mycat.web.model;

public class SchemaChildTable {
	
	private String name;
	private String joinkey;
	private String parentkey;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJoinkey() {
		return joinkey;
	}
	public void setJoinkey(String joinkey) {
		this.joinkey = joinkey;
	}
	public String getParentkey() {
		return parentkey;
	}
	public void setParentkey(String parentkey) {
		this.parentkey = parentkey;
	}
} 

