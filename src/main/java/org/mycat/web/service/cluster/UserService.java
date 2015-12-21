package org.mycat.web.service.cluster;

import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.cluster.User;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Lazy
@Service("userService")
public class UserService {
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context){
		String clusterPath = (String)context.getAttr("zkId");
	    String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath , Constant.CLUSTER_USER );
		int pageNo = context.getPage() == 0 ?1:  context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(path, User.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryAll(RainbowContext context){
		String clusterPath = (String)context.getAttr("zkId");
	    String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath , Constant.CLUSTER_USER );
		Map<String,Object> data = zkHander.getChildNodeData(path, User.class);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	public RainbowContext insert (RainbowContext context) throws Exception{
		Map<String, Object> params = context.getAttr();
		String mycatJson_new = JSON.toJSONString(params);
		User user = JSON.parseObject(mycatJson_new, User.class);
		String name = (String)params.get("name");
		String clusterPath=(String)context.getAttr("zkId");
		String parentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_USER);
		String path = ZKPaths.makePath(parentPath, name);
		if(user.getName()!=null){
			List<String> childrenPath =  zkHander.getChildNode(parentPath);
			for (String cpath : childrenPath) {
				if(user.getName().equals(cpath)){
					context.setSuccess(false);
					context.setMsg("名称已存在");
					return context;
				}
			}
		}else{
			context.setSuccess(false);
		}
		zkHander.createNode(path);
		String data = JSON.toJSONString(user);
		zkHander.setNodeData(path, data);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		String clusterPath=(String)context.getAttr("zkId");
		String guid=(String)context.getAttr("guid");
		String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_USER,guid);
		if(params.containsKey("name")){
			String json_newStr = JSON.toJSONString(params);
			User _new = JSON.parseObject(json_newStr, User.class);
			String  json_oldStr = zkHander.getNodeData(path);
			User _old = JSON.parseObject(json_oldStr, User.class);
			JavaBeanUtil.copyProperties(_new, _old);
			String data = JSON.toJSONString(_old);
			zkHander.setNodeData(path, data);
		}else{
			context.setMsg("操作失败！");
			context.setSuccess(false);
		}
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		String clusterPath=(String)context.getAttr("zkId");
		String guid=(String)context.getAttr("guid");
		String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_USER,guid);
		if (zkHander.existsNode(path)) {
			zkHander.deleteNode(path);
		}else{
			context.setMsg("zk路径不存在！");
			context.setSuccess(false);
		}
		return context;
	}
}
