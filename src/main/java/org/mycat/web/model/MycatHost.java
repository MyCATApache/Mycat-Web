package org.mycat.web.model;

import com.alibaba.fastjson.JSON;

public class MycatHost {
	
	private String hostname;
	
	private String ip;
	
	private String port;
	
	private String password;
	
	private String root;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public static void main(String[] args) {
		MycatHost h = new MycatHost();
		h.setHostname("localhost1");
		h.setIp("127.0.0.1");
		h.setPort("8080");
		h.setPassword("xxxx");
		System.out.println(JSON.toJSON(h));
	}
	
}
