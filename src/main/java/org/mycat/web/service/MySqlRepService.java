package org.mycat.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.model.MySqlRep;
import org.mycat.web.model.MySqlServer;
import org.mycat.web.model.ZtreeModel;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;

@Lazy
@Service("mySqlRepService")
public class MySqlRepService extends BaseService {

	private ZookeeperCuratorHandler handler = ZookeeperCuratorHandler
			.getInstance();
	private final static String MyCAT_MYSQLS = "/mycat-mysqls";
	private final static String MyCAT_MYSQLGROUP = "/mycat-mysqlgroup";
	private final static String MyCAT_MYSQLREP = "/mysql-rep-";
	private static final String MySqlServer = null;

	public RainbowContext query(RainbowContext context) throws Exception {
		// handler.getChildNodeData("/mycat-mysqlgroup");
		//
		// String nodeData =
		// handler.getNodeData("/mycat-mysqlgroup/Mysql-rep-1");
		// System.out.println(nodeData);
		//
		// MySqlRep parseObject = JSONArray.parseObject(nodeData,
		// MySqlRep.class);
		// context.addAttr("rep", parseObject);
		return context;
	}

	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		Map<String, Object> childNodeData = handler.getChildNodeData(
				MyCAT_MYSQLS, MySqlServer.class, context.getPage(),
				context.getLimit(),attr);
		context.setRows((List<Map<String, Object>>) childNodeData.get("rows"));
		context.setTotal((Integer) childNodeData.get("total"));
		return context;
	}

	@SuppressWarnings("unchecked")
	public RainbowContext queryAll(RainbowContext context) {

		List<String> node = handler.getChildNode(MyCAT_MYSQLS);
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("list", node);
		context.setAttr(attr);
		context.setSuccess(true);
		return context;
	}

	@SuppressWarnings("unchecked")
	public RainbowContext queryByRepPage(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		Map<String, Object> childNodeData = handler.getChildNodeData(
				MyCAT_MYSQLGROUP, MySqlRep.class, context.getPage(),
				context.getLimit(),attr);
		context.setRows((List<Map<String, Object>>) childNodeData.get("rows"));
		context.setTotal((Integer) childNodeData.get("total"));
		return context;
	}

	public RainbowContext addMySQL(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		MySqlServer m = JSONArray.parseObject(
				String.valueOf(attr.get("value")), MySqlServer.class);
		if (StringUtils.isEmpty(m.getName())) {
			context.setSuccess(false);
			context.setMsg("mysql名称为空");
			return context;
		}
		try {
			boolean existsNode = handler.existsNode(MyCAT_MYSQLS + "/"
					+ m.getName());
			String guid_bak = null;
			if (existsNode && (StringUtils.isEmpty(attr.get("guid"))||!m.getName().equals(String.valueOf(attr.get("guid"))))) {
				context.setSuccess(false);
				context.setMsg("mysql实例已经存在");
				return context;
			}
			if ((!StringUtils.isEmpty(attr.get("guid"))))
				guid_bak = String.valueOf(attr.get("guid"));
			m.setGuid(m.getName());
			handler.updateNodeData(MyCAT_MYSQLS + "/" + m.getName(),
					JSONArray.toJSONString(m));
			if (!m.getName().equals(guid_bak)
					&& (!StringUtils.isEmpty(guid_bak))) {
				handler.deleteNode(MyCAT_MYSQLS + "/" + guid_bak);
			}
			context.setSuccess(true);
			return context;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.setSuccess(false);
			e.printStackTrace();
		}
		context.setSuccess(false);
		context.setMsg("违法操作");
		return context;
	}

	public RainbowContext addMyRep(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		String valueOf = String.valueOf(attr.get("value"));
		String ztree = String.valueOf(attr.get("ztree"));
		MySqlRep m = JSONArray.parseObject(String.valueOf(attr.get("value")),
				MySqlRep.class);
		if (StringUtils.isEmpty(m.getName())) {
			context.setSuccess(false);
			context.setMsg("mysql-rep名称为空");
			return context;
		}

		try {
			boolean existsNode = handler.existsNode(MyCAT_MYSQLGROUP + "/"
					+ m.getName());
			String guid_bak = null;
			if (existsNode && (StringUtils.isEmpty(attr.get("guid"))||
					!m.getName().equals(String.valueOf(attr.get("guid"))))) {
				context.setSuccess(false);
				context.setMsg("mysql实例已经存在");
				return context;
			}

			List<ZtreeModel> list = JSONArray.parseArray(ztree,
					ZtreeModel.class);
			List<Map<String, String>> rep = new ArrayList<Map<String, String>>();
			for (ZtreeModel z : list) {
				Map<String, String> map = new HashMap<String, String>();
				String writeHost = z.getName();
				String readHosts = "";
				List<ZtreeModel> children = z.getChildren();
				if (children == null)
					continue;
				for (ZtreeModel ztreeModel : children) {
					readHosts = readHosts + "," + ztreeModel.getName();
				}
				map.put("writeHost", writeHost);
				if (!StringUtils.isEmpty(readHosts)) {
					map.put("readHosts", readHosts.substring(1));
				}
				rep.add(map);
			}
			if ((!StringUtils.isEmpty(attr.get("guid"))))
				guid_bak = String.valueOf(attr.get("guid"));
			m.setRep(rep);
			m.setGuid(m.getName());
			handler.updateNodeData(MyCAT_MYSQLGROUP + "/" + m.getName(),
					JSONArray.toJSONString(m));

			if (!m.getName().equals(guid_bak)
					&& (!StringUtils.isEmpty(guid_bak))) {
				handler.deleteNode(MyCAT_MYSQLGROUP + "/" + guid_bak);
			}
			context.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.setSuccess(false);
			e.printStackTrace();
		}

		return context;
	}

	public RainbowContext deleteMysql(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		try {
			handler.deleteNode(MyCAT_MYSQLS + "/" + attr.get("guid"));
			context.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.setSuccess(false);
			e.printStackTrace();
		}

		return context;
	}

	public RainbowContext deleteMyRep(RainbowContext context) {
		Map<String, Object> attr = context.getAttr();
		try {
			handler.deleteNode(MyCAT_MYSQLGROUP + "/" + attr.get("guid"));
			context.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.setSuccess(false);
			e.printStackTrace();
		}

		return context;
	}
}
