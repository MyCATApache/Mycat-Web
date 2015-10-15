package org.mycat.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.DataSourceUtils;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service("dBSourceService")
public class DBSourceService extends BaseService {

	public RainbowContext addMycat(RainbowContext context) {
		try {
			String mycatName = (String)context.getAttr("dbName"); 
			if(!DataSourceUtils.getInstance().register(context.getAttr(), mycatName)){
				context.setSuccess(false);
				context.setMsg("配置信息错误,连接服务失败!");
				return context;
			}
			RainbowContext mycatContext = new RainbowContext("mycatService", "insert");
			String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
			jrdsconfg = jrdsconfg + "D_" + context.getAttr("ip") + "_" + context.getAttr("port") + ".xml";
			context.addAttr("jrdsfile", jrdsconfg);
			mycatContext.setAttr(context.getAttr());
			context = SoaManager.getInstance().invoke(mycatContext);
			context.addAttr("jrdsName", new ObjectId().toString());
			dynamicReport(jrdsconfg, context.getAttr());
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg(e.getMessage());
		}
		return context;
	}
	
	private boolean dynamicReport(String jrdsconf, Map<String, Object> paramData) {
			InputStream inputstate = null;
			Writer out = null;
		try {
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			inputstate = classLoader
					.getResourceAsStream(packagePath + "/templet/mycatjrds.ftl");
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

	
}
