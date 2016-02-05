package org.mycat.web.service;

import java.io.File;
import java.util.Date;
import java.util.List;
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
@Service("mycatService")
public class MycatService extends BaseService {
	//private static final String NAMESPACE = "SYSMYCAT";
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	public RainbowContext query(RainbowContext context) throws Exception{
		//super.query(context, NAMESPACE);
		return queryByPage(context);
	}


	public RainbowContext queryByPage(RainbowContext context) throws Exception {
		//super.queryByPage(context, NAMESPACE);
		String mycatName=(String)context.getAttr("mycatName");	
		
		List<Map<String, Object>> mycatlist = zkHander.getChildNodeData(Constant.MYCATS);
		for (int i = 0; i < mycatlist.size(); i++) {
			Map<String, Object> dbinfo = mycatlist.get(i);
			String db = (String) dbinfo.get("mycatName");
			if (db !=null && db.equals(mycatName)) {
				mycatlist.clear();
				mycatlist.add(dbinfo);
				break;
			}
		}
		context.addRows(mycatlist);
		context.setTotal(context.getRows().size());
		CreateJrdsFiles(context.getRows());
		return context;
	}

	public synchronized RainbowContext insert(RainbowContext context) throws Exception {
       /*
		RainbowContext query = new RainbowContext();
		query.addAttr("mycatName", context.getAttr("mycatName"));
		query = super.query(query, NAMESPACE);

		if (query.getRows() != null && query.getRows().size() > 0) {
			context.setMsg("名称已存在");
			context.setSuccess(false);
			return context;
		}
        */ 
		String guid=new ObjectId().toString();
		context.addAttr("guid", guid);
		context.addAttr("createTime", new Date());
		try{
			//ZookeeperService.getInstance().insertMycat(guid,context.getAttr());
			String path = ZKPaths.makePath(Constant.MYCATS, guid);
			zkHander.createNode(path, JSON.toJSONString(context.getAttr()));
			context.setMsg("新增成功!");
			context.setSuccess(true);
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("新增失败,系统异常!case:" + e.getMessage(), e.getCause());
		}
		return context;
	}

	public RainbowContext update(RainbowContext context) {
		//super.update(context, NAMESPACE);
		String guid=(String)context.getAttr("guid");
		try{
			String jrdsfile = (String)context.getAttr("jrdsfile");
			if(jrdsfile != null && !jrdsfile.isEmpty()){
				new File(jrdsfile).delete();
			}
			//ZookeeperService.getInstance().insertMycat(guid,context.getAttr());
			String path = ZKPaths.makePath(Constant.MYCATS, guid);
			zkHander.updateNodeData(path, JSON.toJSONString(context.getAttr()));
			if (jrdsfile == null){
				jrdsfile = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
				jrdsfile = jrdsfile + "D_" + context.getAttr("ip") + "_" + context.getAttr("mangerPort") + ".xml";				
			}
			JrdsUtils.getInstance().newJrdsFile("/templet/mycatjrds.ftl", jrdsfile, context.getAttr());
			context.setMsg("更新成功!");
			context.setSuccess(true);
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("更新失败,系统异常!case:" + e.getMessage(), e.getCause());
		}		
		context.getAttr().clear();
		return context;
	}

	public RainbowContext delete(RainbowContext context) {		
		//Map<String, Object> data = super.getDao().get(NAMESPACE, "load", context.getAttr());
		//super.getDao().delete(NAMESPACE, "delete", context.getAttr());
		String jrdsfile ="";
		try{
			String guid=(String)context.getAttr("guid");
			//Map<String, Object> data =ZookeeperService.getInstance().getMycatNode(guid);
			//ZookeeperService.getInstance().delMycat(guid);
			String path = ZKPaths.makePath(Constant.MYCATS, guid);
			Map<String, Object> data = zkHander.getNodeDataForMap(path);
			zkHander.deleteNode(path);
			context.setMsg("删除成功!");
			context.setSuccess(true);
			jrdsfile = (String)data.get("jrdsfile");
			if(jrdsfile != null && !jrdsfile.isEmpty()){
				new File(jrdsfile).delete();
			}
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("删除失败,系统异常!case:" + e.getMessage(), e.getCause());
		}				
				
		context.getAttr().clear();
		return context;
	}
	
	private void CreateJrdsFiles(List<Map<String, Object>> mycatlist){
	  if (!Constant.Mycat_JRDS){
		for (int i = 0; i < mycatlist.size(); i++) {	
			Map<String, Object> dbinfo = mycatlist.get(i);
			String jrdsfile = (String)dbinfo.get("jrdsfile");
			if (jrdsfile == null){
				jrdsfile = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
				jrdsfile = jrdsfile + "D_" + dbinfo.get("ip") + "_" + dbinfo.get("mangerPort") + ".xml";				
			}
			File file = new File(jrdsfile);
			System.out.println("CreateJrdsFile Mycat:"+jrdsfile);
			if (!file.exists()) {			
			  JrdsUtils.getInstance().newJrdsFile("/templet/mycatjrds.ftl", jrdsfile, dbinfo);		
			}
		  }
		Constant.Mycat_JRDS=true;	
	  }	
	}
}
