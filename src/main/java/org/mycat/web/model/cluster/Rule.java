package org.mycat.web.model.cluster;

public class Rule {
	private String name;
	private String functionName;
	private String column;
	private String defaultNode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getDefaultNode() {
		return defaultNode;
	}
	public void setDefaultNode(String defaultNode) {
		this.defaultNode = defaultNode;
	}
	
}
