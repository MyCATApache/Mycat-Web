package org.mycat.web.service.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.cluster.DataHost;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import org.mycat.web.model.cluster.Sequence;

@Service("sequenceService")
public class SequenceService {
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkPath");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_SEQUENCE);
		int pageNo = context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, Sequence.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryAll(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkPath");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_SEQUENCE);
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, Sequence.class);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	public RainbowContext query(RainbowContext context){
		String path = (String)context.getAttr("guid");
		String data  = zkHander.getNodeData(path);
		Sequence sequence = JSON.parseObject(data, Sequence.class);
		Map<String,Object> attr = new HashMap<String, Object>();
		attr.put("datahost",sequence);
		context.setAttr(attr);
	    return context;
	}
	
	public RainbowContext insert (RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		String clusterPath = (String)context.getAttr("zkPath");
		String zkid = (String) context.getAttr("zkid");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_SEQUENCE,zkid);
		String datahostPath = ZKPaths.makePath(datahostParentPath , zkid);
		
		String mycatJson_new = (String)params.get("value");
		DataHost datahost = JSON.parseObject(mycatJson_new, DataHost.class);
		
		List<String> childrenPath =  zkHander.getChildNode(datahostParentPath);
		for (String cpath : childrenPath) {
			if(datahost.getName().equals(cpath)){
				context.setSuccess(false);
				context.setMsg("名称已存在");
				return context;
			}
		}
		String data = JSON.toJSONString(datahost);
		zkHander.createNode(datahostPath, data);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		if(params.containsKey("guid")){
			String json_newStr = (String)params.get("value");
			String path =  String.valueOf(params.get("guid"));
			DataHost _new = JSON.parseObject(json_newStr, DataHost.class);
			String  json_oldStr = zkHander.getNodeData(path);
			DataHost _old = JSON.parseObject(json_oldStr, DataHost.class);
			JavaBeanUtil.copyProperties(_new, _old);
			String data = JSON.toJSONString(_old);
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
