package org.mycat.web.model.cluster;

public class User {
	
	private String name;
	
	private String password;
	
	private boolean readyOnly;
	
	private String  schemas;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isReadyOnly() {
		return readyOnly;
	}

	public void setReadyOnly(boolean readyOnly) {
		this.readyOnly = readyOnly;
	}

	public String getSchemas() {
		return schemas;
	}

	public void setSchemas(String schemas) {
		this.schemas = schemas;
	}

}
