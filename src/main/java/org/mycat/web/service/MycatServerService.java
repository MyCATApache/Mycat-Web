package org.mycat.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.MycatServer;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service("mycatServerService")
public class MycatServerService  {
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	public RainbowContext queryByPage(RainbowContext context) throws Exception{
		String path = Constant.MYCAT_SERVERS;
		//Map<String,Object> params = context.getAttr();
		int pageNo = context.getPage();
		int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(path, MycatServer.class, pageNo, pageSize,null);
		if(data !=null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	public RainbowContext query(RainbowContext context) throws Exception {
		MycatServer mycatServer = null;
		String path = null;
		Map<String, Object> params = context.getAttr();
		if (params.containsKey("guid")) {
			path = (String) params.get("guid");
		}
		String data = zkHander.getNodeData(path);
		JSON json = JSON.parseObject(data);
		mycatServer = JSON.toJavaObject(json, MycatServer.class);
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("Path", path);
		attr.put("Data", mycatServer);
		context.addRow(attr);

		return context;

	}

	public RainbowContext insert(RainbowContext context) throws Exception {
		
		Map<String, Object> params = context.getAttr();
		String  parentPath =Constant.MYCAT_SERVER;
		String path = zkHander.createSeqNode(parentPath, "");
		String mycatJson_new = (String)params.get("value");
		MycatServer mycatServer = JSON.parseObject(mycatJson_new, MycatServer.class);
		mycatServer.setGuid(path);
		String data = JSON.toJSONString(mycatServer);
		zkHander.setNodeData(path, data);

		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		if(params.containsKey("guid")){
			String mycatJson_new = (String)params.get("value");
			String path =  String.valueOf(params.get("guid"));
			MycatServer mycat_new = JSON.parseObject(mycatJson_new, MycatServer.class);
			String mycatjson_old = zkHander.getNodeData(path);
			MycatServer mycat_old = JSON.parseObject(mycatjson_old, MycatServer.class);
			JavaBeanUtil.copyProperties(mycat_new, mycat_old);
			String data = JSON.toJSONString(mycat_old);
			zkHander.setNodeData(path, data);
		}
		
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) throws Exception {
		Map<String, Object> params = context.getAttr();
		if (params.containsKey("guid")) {
			String path = (String) params.get("guid");
			zkHander.deleteNode(path);
		}
		return context;
	}
}
