package org.mycat.web.model;

public class SchemaTable {
	
	
	private String name;
	private String primaryKey;
	private String datanode;
	private String ruleName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getDatanode() {
		return datanode;
	}
	public void setDatanode(String datanode) {
		this.datanode = datanode;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

}