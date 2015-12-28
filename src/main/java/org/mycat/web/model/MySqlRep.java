package org.mycat.web.model;

import java.util.List;
import java.util.Map;

public class MySqlRep {
	
	
	private String guid;
	private String name;
	private Integer repType;
	private String zone;
	
	private String servers;
	
	private String cur_write_server;
	
	private boolean auto_write_switch;
	
	private String heartbeatSQL;
	
	
	
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
	public String getServers() {
		return servers;
	}
	public void setServers(String servers) {
		this.servers = servers;
	}
	
	public String getCur_write_server() {
		return cur_write_server;
	}
	public void setCur_write_server(String cur_write_server) {
		this.cur_write_server = cur_write_server;
	}
	public boolean isAuto_write_switch() {
		return auto_write_switch;
	}
	public void setAuto_write_switch(boolean auto_write_switch) {
		this.auto_write_switch = auto_write_switch;
	}
	public String getHeartbeatSQL() {
		return heartbeatSQL;
	}
	public void setHeartbeatSQL(String heartbeatSQL) {
		this.heartbeatSQL = heartbeatSQL;
	}
	
}
