package org.mycat.web.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/lb")
public class LBServer {

	@ResponseBody
	@RequestMapping("/loadAllZone")
	public JSON loadAllZone(String path) {
		if (path == null || path.isEmpty())
			path = "mycat-zones";

		List<String> zones = loadZoneNames();
		List<JSONObject> clusters = loadAllCluster(zones);
		JSONArray arr = new JSONArray();
		arr.addAll(clusters);
		return arr;
	}

	private List<JSONObject> loadAllCluster(List<String> zones) {
		String zoneIcon = "css/zone_24px.png";
		List<JSONObject> allClusters = new ArrayList<JSONObject>();
		for (String name : zones) {
			List<JSONObject> hosts = loadHostByZone(name);
			List<JSONObject> lbGroups = loadLbGroupByZone(name);
			List<JSONObject> clusters = loadClusterByZone(name);
			List<JSONObject> mysql = loadMysqlGroupByZone(name);

			clusters.addAll(lbGroups);
			clusters.addAll(mysql);
			clusters.addAll(hosts);

			JSONObject root = createTreeNode(name, zoneIcon, clusters);
			allClusters.add(root);
		}
		return allClusters;
	}

	private List<JSONObject> loadMysqlGroupByZone(String name) {
		String hostIcon = "css/grp_24px.png";
		List<JSONObject> mysqls = new ArrayList<JSONObject>();
		List<String> gp1Child = new ArrayList<String>();
		gp1Child.add("mysql0");
		JSONObject node = createTreeNode("MysqlGroup", hostIcon, gp1Child);
		mysqls.add(node);
		return mysqls;
	}

	private List<JSONObject> loadLbGroupByZone(String name) {
		String hostIcon = "css/grp_24px.png";
		List<JSONObject> hosts = new ArrayList<JSONObject>();
		List<String> gp1Child = new ArrayList<String>();
		gp1Child.add("group-1");
		JSONObject node = createTreeNode("LB Group", hostIcon, gp1Child);
		hosts.add(node);
		return hosts;
	}

	private List<JSONObject> loadHostByZone(String name) {
		String hostIcon = "css/host_24px.png";
		List<JSONObject> hosts = new ArrayList<JSONObject>();
		List<String> host1Child = new ArrayList<String>();
		host1Child.add("host1");
		JSONObject node = createTreeNode("hosts", hostIcon, host1Child);
		hosts.add(node);
		return hosts;
	}

	private List<JSONObject> loadClusterByZone(String name) {
		String clusterIcon = "css/cluster_24px.png";
		List<JSONObject> allClusters = new ArrayList<JSONObject>();
		String clusterName = "MycatCluster";
		List<JSONObject> nodes = loadNodeByClusterName(clusterName);
		allClusters.add(createTreeNode(clusterName, clusterIcon, nodes));
		return allClusters;
	}

	private List<JSONObject> loadNodeByClusterName(String clusterName) {
		String instIcon = "css/mycat_16px.png";
		List<JSONObject> names = new ArrayList<JSONObject>();
		JSONObject node = createTreeNode("node1", instIcon, null);
		names.add(node);
		return names;
	}

	private List<String> loadZoneNames() {
		List<String> zones = new ArrayList<String>();
		zones.add("成都中心[default]");
		zones.add("北京中心");
		return zones;
	}

	private JSONObject createTreeNode(String text, String icon, List<?> childs) {
		JSONObject node = new JSONObject();
		node.put("text", text);
		node.put("icon", icon);
		node.put("children", childs);
		return node;
	}

	public static void main(String[] args) {
		System.out.println(new LBServer().loadAllZone(null));
	}
}
