package org.mycat.web.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/lb")
public class LBServer {

	class Node {
		String text;
		String icon;
		Object obj;
		List<?> children;

		public Object getObj() {
			return obj;
		}

		public String getText() {
			return text;
		}

		public List<?> getChildren() {
			return children;
		}

		public String getIcon() {
			return icon;
		}

	}

	class Zone {
		int id;
		String text;
		String icon;
		List<Node> children = new ArrayList<Node>();

		void add(Node node) {
			children.add(node);
		}

		public int getId() {
			return id;
		}

		public String getText() {
			return text;
		}

		public List<Node> getChildren() {
			return children;
		}

		public String getIcon() {
			return icon;
		}

	}

	class Mycat {
		int id;
		int port;
		int clusterId;
		String host;
		String text;

		public int getId() {
			return id;
		}

		public int getPort() {
			return port;
		}

		public int getClusterId() {
			return clusterId;
		}

		public String getHost() {
			return host;
		}

		public String getText() {
			return text;
		}

	}

	class LBGroup {
		int id;
		int zoneId;
		String text;

		public int getId() {
			return id;
		}

		public int getZoneId() {
			return zoneId;
		}

		public String getText() {
			return text;
		}

	}

	class Mysql {
		int id;
		int port;
		int zoneId;
		String host;
		String text;

		public int getId() {
			return id;
		}

		public int getPort() {
			return port;
		}

		public int getZoneId() {
			return zoneId;
		}

		public String getHost() {
			return host;
		}

		public String getText() {
			return text;
		}

	}

	class Host {
		int id;
		int port;
		int zoneId;
		String host;
		String text;
	}

	@ResponseBody
	@RequestMapping("/loadAllZone")
	public JSON loadAllZone(String path) {
		if (path == null || path.isEmpty())
			path = "mycat-zones";

		String zoneIcon = "css/zone_24px.png";

		int zoneId = 1;
		Node lbs = loadLBGroup(zoneId, null);
		Node mycats = loadMycats(zoneId);
		Node mysqls = loadMysql(zoneId);
		Node host = loadHost(zoneId);

		Zone zone = new Zone();
		zone.id = zoneId;
		zone.text = "成都中心";
		zone.icon = zoneIcon;
		zone.add(lbs);
		zone.add(mycats);
		zone.add(mysqls);
		zone.add(host);

		return (JSON) JSON.toJSON(zone);
	}

	private Node loadHost(int zoneId) {
		return build("css/host_24px.png", "hosts", null, null);
	}

	private Node loadMysql(int id) {
		List<Node> mysqls = new ArrayList<Node>();
		Mysql sql = new Mysql();
		sql.id = 0;
		sql.port = 3306;
		sql.zoneId = id;
		sql.text = "mysql";
		sql.host = "127.0.0.1";
		mysqls.add(build("", sql.text, null, sql));

		return build("css/grp_24px.png", "MysqlGroup", mysqls, null);
	}

	private Node loadLBGroup(int id, List<Mycat> nodes) {

		List<Node> lbs = new ArrayList<Node>();
		LBGroup gp = new LBGroup();
		gp.zoneId = id;
		gp.text = "group1";
		lbs.add(build("", gp.text, null, gp));

		return build("css/grp_24px.png", "LBGroup", lbs, null);
	}

	private Node loadMycats(int id) {
		List<Node> mycats = new ArrayList<Node>();
		Mycat m = new Mycat();
		m.id = 1;
		m.port = 8066;
		m.clusterId = 2;
		m.text = "node1";
		m.host = "127.0.0.1";
		mycats.add(build("", m.text, null, m));
		return build("css/cluster_24px.png", "MycatCluster", mycats, null);
	}

	private Node build(String icon, String text, List<?> clds, Object obj) {
		Node nodes = new Node();
		nodes.text = text;
		nodes.children = clds;
		nodes.icon = icon;
		nodes.obj = obj;
		return nodes;

	}

	public static void main(String[] args) {
		System.out.println(new LBServer().loadAllZone(null));
	}
}
