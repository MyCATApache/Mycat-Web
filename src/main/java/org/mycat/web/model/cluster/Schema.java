package org.mycat.web.model.cluster;

public class Schema {
	private String name;
	private boolean checkSQLSchema;
	private String defaultMaxLimit;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCheckSQLSchema() {
		return checkSQLSchema;
	}
	public void setCheckSQLSchema(boolean checkSQLSchema) {
		this.checkSQLSchema = checkSQLSchema;
	}
	public String getDefaultMaxLimit() {
		return defaultMaxLimit;
	}
	public void setDefaultMaxLimit(String defaultMaxLimit) {
		this.defaultMaxLimit = defaultMaxLimit;
	}
	
	
	
}
