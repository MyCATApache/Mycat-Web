package org.mycat.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Lazy
@Service("jmxservice")
public class JMXService extends BaseService {
	private static final String NAMESPACE = "SYSJMX";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}


	public RainbowContext insert(RainbowContext context) {
		try {

//			String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
//			jrdsconfg = jrdsconfg + "JMX_" +  context.getAttr("jmxname")+ "_" + context.getAttr("ip") + "_" + context.getAttr("port")  + ".xml";
//
//			//JMX SAVE JSR
//			context.addAttr("jrdsfile", jrdsconfg);
//			context.addAttr("fileName", jrdsconfg);
//			context.addAttr("guid", new ObjectId().toString());
			String jrdsconfg = buildJrdsPath(context, new ObjectId().toString());

			super.insert(context, NAMESPACE);
			createjmxjrds(jrdsconfg, context.getAttr());
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg(e.getMessage());
		}
		return context;
	}

	/**
	 * 更新
	 * @param context
	 * @return
	 */
	public RainbowContext update(RainbowContext context) {
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
	}

	/**
	 * 构建路径及设置jrdsfile、fileName、guid的值
	 * @param context
	 * @param guid
	 * @return
	 */
	private String buildJrdsPath(RainbowContext context, Object guid) {
		String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
		jrdsconfg = jrdsconfg + "JMX_" +  context.getAttr("jmxname")+ "_" + context.getAttr("ip") + "_" + context.getAttr("port")  + ".xml";

		//JMX SAVE JSR
		context.addAttr("jrdsfile", jrdsconfg);
		context.addAttr("fileName", jrdsconfg);
		context.addAttr("guid", guid);
		return jrdsconfg;
	}
	
	private boolean createjmxjrds(String jrdsconf, Map<String, Object> paramData) {
			InputStream inputstate = null;
			Writer out = null;
		try {
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			inputstate = classLoader
					.getResourceAsStream(packagePath + "/templet/jmxjrds.ftl");
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
		
		Map<String, Object> data = super.getDao().get(NAMESPACE, "query", context.getAttr());
		super.getDao().delete(NAMESPACE, "delete", context.getAttr());
		String jrdsfile = (String)data.get("fileName");
		if(jrdsfile != null && !jrdsfile.isEmpty()){
			new File(jrdsfile).delete();
		}
		context.getAttr().clear();
		return context;
	}
}
