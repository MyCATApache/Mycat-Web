package org.mycat.web.service;

import java.io.File;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.Constant;
import org.mycat.web.util.JrdsUtils;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Lazy
@Service("jmxservice")
public class JMXService extends BaseService {
	//private static final String NAMESPACE = "SYSJMX";
    private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	public RainbowContext query(RainbowContext context) throws Exception {
		//super.query(context, NAMESPACE);
		//context.addRows(ZookeeperService.getInstance().getJmx());
		context.addRows(zkHander.getChildNodeData(Constant.MYCAT_JMX));
		context.setTotal(context.getRows().size());		
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context)  throws Exception {
		//super.queryByPage(context, NAMESPACE);
		//context.addRows(ZookeeperService.getInstance().getJmx());
		context.addRows(zkHander.getChildNodeData(Constant.MYCAT_JMX));
		context.setTotal(context.getRows().size());
		return context;
	}


	public RainbowContext insert(RainbowContext context) {
		String guid=new ObjectId().toString();
		try{
			String jrdsconfg = buildJrdsPath(context, guid);
			
			//ZookeeperService.getInstance().insertJmx(guid,context.getAttr());
			String path = ZKPaths.makePath(Constant.MYCAT_JMX, guid);
			zkHander.createNode(path, JSON.toJSONString(context.getAttr()));
			context.setMsg("新增成功!");
			context.setSuccess(true);
			JrdsUtils.getInstance().newJrdsFile("/templet/jmxjrds.ftl", jrdsconfg, context.getAttr());
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("新增失败,系统异常!case:" + e.getMessage(), e.getCause());
		}
		return context;		
		/*
		try {
			String jrdsconfg = buildJrdsPath(context, new ObjectId().toString());

			super.insert(context, NAMESPACE);
			createjmxjrds(jrdsconfg, context.getAttr());
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg(e.getMessage());
		}
		return context;
		*/
	}

	/**
	 * 更新
	 * @param context
	 * @return
	 */
	public RainbowContext update(RainbowContext context) {
		try{		
		   String guid=(String)context.getAttr("guid");
		   //Map<String, Object> data =ZookeeperService.getInstance().getJmxNode(guid);
		   String path =  ZKPaths.makePath(Constant.MYCAT_JMX, guid);
		   Map<String, Object> data = zkHander.getNodeDataForMap(path);
			String jrdsfile = (String)data.get("fileName");
			if(jrdsfile != null && !jrdsfile.isEmpty()){
				new File(jrdsfile).delete();
			}
			String jrdsconfg = buildJrdsPath(context,guid);
			//ZookeeperService.getInstance().insertJmx(guid,context.getAttr());	
			zkHander.updateNodeData(path, JSON.toJSONString(context.getAttr()));
			context.setMsg("更新成功!");
			context.setSuccess(true);
			JrdsUtils.getInstance().newJrdsFile("/templet/jmxjrds.ftl", jrdsconfg, context.getAttr());
		} catch (Exception e) {
			logger.error("execute error, {} /r/n cause:{}", e.getMessage(), e.getCause());
			context.setSuccess(Boolean.FALSE);
			context.setMsg(e.getMessage());
		}
		return context;
		/*
		try {
			Map<String, Object> queryMap = new HashMap<>(1);
			queryMap.put("guid", context.getAttr("guid"));
			Map<String, Object> data = super.getDao().get(NAMESPACE, "query", queryMap);
			String jrdsfile = (String)data.get("fileName");
			if(jrdsfile != null && !jrdsfile.isEmpty()){
				new File(jrdsfile).delete();
			}

			String jrdsconfg = buildJrdsPath(context, context.getAttr("guid"));
			update(context, NAMESPACE);
			createjmxjrds(jrdsconfg, context.getAttr());
		} catch (Exception e) {
			logger.error("execute error, {} /r/n cause:{}", e.getMessage(), e.getCause());
			context.setSuccess(Boolean.FALSE);
			context.setMsg(e.getMessage());
		}
		return context;
		*/		
	}

	/**
	 * 构建路径及设置jrdsfile、fileName、guid的值
	 * @param context
	 * @param guid
	 * @return
	 */
	private String buildJrdsPath(RainbowContext context, Object guid) {
		String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
		jrdsconfg = jrdsconfg + "JMX_" + context.getAttr("ip") + "_" + context.getAttr("port")  + ".xml";

		//JMX SAVE JSR
		context.addAttr("jrdsfile", jrdsconfg);
		context.addAttr("fileName", jrdsconfg);
		context.addAttr("guid", guid);
		return jrdsconfg;
	}


	public RainbowContext delete(RainbowContext context) {
		/*
		Map<String, Object> data = super.getDao().get(NAMESPACE, "query", context.getAttr());
		super.getDao().delete(NAMESPACE, "delete", context.getAttr());
		String jrdsfile = (String)data.get("fileName");
		if(jrdsfile != null && !jrdsfile.isEmpty()){
			new File(jrdsfile).delete();
		}
		context.getAttr().clear();
		return context;
		*/
		String jrdsfile ="";
		try{
			String guid=(String)context.getAttr("guid");
			//Map<String, Object> data =ZookeeperService.getInstance().getJmxNode(guid);
			//ZookeeperService.getInstance().delJmx(guid);
			String path = ZKPaths.makePath(Constant.MYCAT_JMX, guid);
			Map<String, Object> data = zkHander.getNodeDataForMap(path);
			zkHander.deleteNode(path);
			context.setMsg("删除成功!");
			context.setSuccess(true);
			jrdsfile = (String)data.get("fileName");
	
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("删除失败,系统异常!case:" + e.getMessage(), e.getCause());
		}				
		if(jrdsfile != null && !jrdsfile.isEmpty()){
			new File(jrdsfile).delete();
		}		
		context.getAttr().clear();
		return context;
	}		

}
