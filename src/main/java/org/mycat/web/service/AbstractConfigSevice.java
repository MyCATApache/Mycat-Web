package org.mycat.web.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.util.JavaBeanToMapUtil;
import org.mycat.web.util.ZookeeperCuratorHandler;

import com.alibaba.fastjson.JSON;

public abstract class AbstractConfigSevice {
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public <T> RainbowContext queryByPage(RainbowContext context,Class<T> entity) {
		String searchPath = (String)context.getAttr("search");
		String zkId = (String)context.getAttr("zkId");
		String guid = (String)context.getAttr("guid");
		String parentPath = getPath(zkId,guid);
		int offset =Integer.parseInt((String)context.getAttr("offset") == null ? "0" :(String)context.getAttr("offset"));
		int limit = Integer.parseInt((String) context.getAttr("limit") == null ? "10" :(String) context.getAttr("limit") );
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = zkHander.getChildNodeDataByPage(parentPath, entity, searchPath, limit, offset);
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("查询失败！");
		}
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public <T> RainbowContext queryAll(RainbowContext context,Class<T> entity) {
		try {
			String zkId = (String)context.getAttr("zkId");
			String guid = (String)context.getAttr("guid");
			String parentPath = getPath(zkId,guid);
			Map<String,Object> data = zkHander.getChildNodeData(parentPath,entity);
			if(data != null && data.size() > 0){
				context.setRows((List<Map<String, Object>>) data.get("rows"));
			}
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("查询失败！");
		}
	    return context;
	}

	public <T> RainbowContext query(RainbowContext context,Class<T> entity) {
		String searchPath = (String)context.getAttr("search");
		String zkId = (String)context.getAttr("zkId");
		try {
			String path = getPath(zkId, searchPath);
			T t = zkHander.getBeanData(path, entity);
			Map<String, Object> beanMap = JavaBeanToMapUtil.beanToMap(t);
			if (beanMap != null && beanMap.size() > 0) {
				context.addRow(beanMap);
				context.setTotal(1);
			}
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("查询失败！");
		}
	    return context;
	}
	
	public <T> RainbowContext insert (RainbowContext context,Class<T> entity) throws Exception{
		Map<String,Object> params = context.getAttr();
		String zkId = (String)context.getAttr("zkId");
		String json_new =  JSON.toJSONString(params);
		try {
			T t = JSON.parseObject(json_new, entity);
			Field fields[] = entity.getDeclaredFields();
			Field.setAccessible(fields,   true);
			String fieldValue="";
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				
				if(fieldName.equals("name")){
					fieldValue = (String)field.get(t);
					break;
				}
			}
			if(fieldValue.equals("")){
				context.setSuccess(false);
				context.setMsg("名称不能空");
				return context;
			}
			String parentPath = getPath(zkId,"");
			List<String> childrenPath =  zkHander.getChildNode(parentPath);
			if (childrenPath != null && childrenPath.size() > 0) {
				for (String cpath : childrenPath) {
					if (fieldValue != "" && fieldValue.equals(cpath)) {
						context.setSuccess(false);
						context.setMsg("名称已存在");
						return context;
					}
				}
			}
			String creatPath = ZKPaths.makePath(parentPath, fieldValue);
			String data = JSON.toJSONString(t);
			zkHander.createNode(creatPath, data);
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("操作失败！");
		}
		return context;
	}
	
	public <T> RainbowContext update(RainbowContext context,Class<T> entity) throws Exception{
		Map<String,Object> params = context.getAttr();
		String zkId = (String)context.getAttr("zkId");
		String guid = (String)context.getAttr("guid");
		String path = getPath(zkId,guid);
		try {
			boolean exists = zkHander.existsNode(path);
			if(exists){
				String json_newStr =  JSON.toJSONString(params);
				T _newObj = JSON.parseObject(json_newStr, entity);
				String  json_oldStr = zkHander.getNodeData(path);
				T _oldObj = JSON.parseObject(json_oldStr, entity);
				JavaBeanUtil.copyProperties(_newObj, _oldObj);
				String data = JSON.toJSONString(_oldObj);
				zkHander.setNodeData(path, data);
			}else{
				context.setMsg("zk 路径不存在");
				context.setSuccess(false);
			}
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("操作失败！");
		}
			
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		String zkId = (String)context.getAttr("zkId");
		String guid = (String)context.getAttr("guid");
		String path = getPath(zkId,guid);
		try {
			if (guid != null && !guid.equals("")) {
				boolean exists = zkHander.existsNode(path);
				if (exists) {
					zkHander.deleteNode(path);
				} else {
					context.setMsg("zk路径不存在");
					context.setSuccess(false);
				}
			} else {
				context.setSuccess(false);
				context.setMsg("路径不存在！");
			}
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("操作失败！");
		}
		return context;
	}
	
	public abstract String getPath(String zkId,String guid) ;
	
}
