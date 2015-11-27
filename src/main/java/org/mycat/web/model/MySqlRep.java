package org.mycat.web.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MySqlRep {
	
	
	private String guid;
	private String name;
	private Integer repType;
	private String zone;
	private List<Map<String, String>> rep; 
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public Integer getRepType() {
		return repType;
	}
	public void setRepType(Integer repType) {
		this.repType = repType;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public List<Map<String, String>> getRep() {
		return rep;
	}
	public void setRep(List<Map<String, String>> rep) {
		this.rep = rep;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
