package org.mycat.web.model;

public class Schema {
	
	private String name;
	private Boolean checkSQLSchema;
	private Integer defaultMaxLimit;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getCheckSQLSchema() {
		return checkSQLSchema;
	}

	public void setCheckSQLSchema(Boolean checkSQLSchema) {
		this.checkSQLSchema = checkSQLSchema;
	}

	public Integer getDefaultMaxLimit() {
		return defaultMaxLimit;
	}

	public void setDefaultMaxLimit(Integer defaultMaxLimit) {
		this.defaultMaxLimit = defaultMaxLimit;
	}
}
