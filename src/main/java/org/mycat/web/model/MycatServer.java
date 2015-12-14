package org.mycat.web.model;

import com.alibaba.fastjson.JSON;

public class MycatServer {
	
	private String guid;

	private String name;

	private String hostId;

	private String host;

	private String zone;

	private String clusterName;

	private String weigth;

	private String leader;

	private String state;

	private String systemParams;


	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
    
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getWeigth() {
		return weigth;
	}

	public void setWeigth(String weigth) {
		this.weigth = weigth;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSystemParams() {
		return systemParams;
	}

	public void setSystemParams(String systemParams) {
		this.systemParams = systemParams;
	}

	public static void main(String[] args) {
		MycatServer node = new MycatServer();
		node.setGuid("/cluster/mycatServer1");
		node.setState("red");
		node.setHost("host1");
		node.setWeigth("1");
		node.setSystemParams("system.xml");
		node.setLeader("1");
		node.setHostId("hostId1");
		node.setClusterName("myCatCluster");
		node.setName("mycatServer");
		node.setZone("bj");
		System.out.println(JSON.toJSONString(node));
	}
}
