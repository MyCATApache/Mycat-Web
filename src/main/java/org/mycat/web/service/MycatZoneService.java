package org.mycat.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.MycatZone;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Lazy
@Service("mycatZoneService")
public class MycatZoneService {
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context) throws Exception{
		String path = ZKPaths.makePath("/", Constant.MYCAT_ZONE_KEY);
		int pageNo = context.getPage();
		int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(path, MycatZone.class, pageNo, pageSize,null);
		if (data != null && data.size() > 0) {
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryAllZone(RainbowContext context) throws Exception{
		String path = ZKPaths.makePath("/", Constant.MYCAT_ZONE_KEY);
		Map<String,Object> data = zkHander.getChildNodeData(path, MycatZone.class);
		if (data != null && data.size() > 0) {
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	public RainbowContext query(RainbowContext context) throws Exception {
		MycatZone mycatZone = null;
		String path = null;
		Map<String, Object> params = context.getAttr();
		if (params.containsKey("guid")) {
			path = (String) params.get("guid");
		}
		String data = zkHander.getNodeData(path);
		JSON json = JSON.parseObject(data);
		mycatZone = JSON.toJavaObject(json, MycatZone.class);
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("Path", path);
		attr.put("Data", mycatZone);
		context.addRow(attr);

		return context;

	}

	public RainbowContext insert(RainbowContext context) throws Exception {
		
		Map<String, Object> params = context.getAttr();
		String  parentPath = Constant.MYCAT_ZONE_KEY;
		String path = zkHander.createSeqNode(parentPath, "");
		String json_new = (String)params.get("value");
		MycatZone mycatZone = JSON.parseObject(json_new, MycatZone.class);
		mycatZone.setGuid(path);
		String data = JSON.toJSONString(mycatZone);
		zkHander.setNodeData(path, data);

		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		if(params.containsKey("guid")){
			String json_new = (String)params.get("value");
			String path =  String.valueOf(params.get("guid"));
			MycatZone zone_new = JSON.parseObject(json_new, MycatZone.class);
			String json_old = zkHander.getNodeData(path);
			MycatZone zone_old = JSON.parseObject(json_old, MycatZone.class);
			JavaBeanUtil.copyProperties(zone_new, zone_old);
			String data = JSON.toJSONString(zone_old);
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
