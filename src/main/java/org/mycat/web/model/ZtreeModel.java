package org.mycat.web.model;

import java.util.List;

public class ZtreeModel {
	
	private Integer id;
	private Integer pid;
	private String name;
	
	private List<ZtreeModel> children;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ZtreeModel> getChildren() {
		return children;
	}
	public void setChildren(List<ZtreeModel> children) {
		this.children = children;
	}
	
	

}
