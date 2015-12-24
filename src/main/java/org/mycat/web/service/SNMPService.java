package org.mycat.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Lazy
@Service("snmpservice")
public class SNMPService extends BaseService {
	//private static final String NAMESPACE = "SYSSNMP";
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();

	public RainbowContext query(RainbowContext context) throws Exception {
	//	super.query(context, NAMESPACE);
	//	return context;
		//context.addRows(ZookeeperService.getInstance().getSnmp());
		context.addRows(zkHander.getChildNodeData(Constant.MYCAT_SNMP));
		context.setTotal(context.getRows().size());		
		return context;
	}



	public RainbowContext insert(RainbowContext context) {
		try {

			String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
			jrdsconfg = jrdsconfg + "SNMP_" + context.getAttr("ip") + "_" + context.getAttr("port")  + ".xml";
			
			context.addAttr("jrdsfile", jrdsconfg);
			context.addAttr("fileName", jrdsconfg);
			context.addAttr("guid", new ObjectId().toString());
			//super.insert(context, NAMESPACE);
			String guid=new ObjectId().toString();
			//ZookeeperService.getInstance().insertSnmp(guid,context.getAttr());
			zkHander.createNode(ZKPaths.makePath(Constant.MYCAT_SNMP, guid), JSON.toJSONString(context.getAttr()));
			context.setMsg("新增成功!");
			context.setSuccess(true);			
			createsnmpjrds(jrdsconfg, context.getAttr());
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg(e.getMessage());
		}
		return context;
	}
	
	private boolean createsnmpjrds(String jrdsconf, Map<String, Object> paramData) {
			InputStream inputstate = null;
			Writer out = null;
		try {
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			inputstate = classLoader.getResourceAsStream(packagePath + "/templet/snmpjrds.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate), new Configuration());
			tempState.setEncoding("UTF-8");
			
			File file = new File(jrdsconf);
			if (!file.exists()) {
				file.getParentFile().mkdir();
				file.createNewFile();
			}
			out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
			tempState.process(paramData, out);
			out.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(inputstate != null){
					inputstate.close();
				}
				if(out != null){
					out.close();
				}
			} catch (IOException e) {
			}
		}
	}


	public RainbowContext delete(RainbowContext context) {
		String jrdsfile ="";
		try{
			String guid=(String)context.getAttr("guid");
			/*Map<String, Object> data =ZookeeperService.getInstance().getSnmpNode(guid);
			ZookeeperService.getInstance().delSnmp(guid);*/
			String path = ZKPaths.makePath(Constant.MYCAT_SNMP, guid);
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
	}
}
