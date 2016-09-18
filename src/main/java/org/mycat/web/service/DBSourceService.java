package org.mycat.web.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.JrdsUtils;
import org.springframework.stereotype.Service;

@Service("dBSourceService")
public class DBSourceService extends BaseService {

	public RainbowContext addMycat(RainbowContext context) {
		try {
			String mycatName = (String)context.getAttr("mycatName"); 
			if(!DataSourceUtils.getInstance().register(context.getAttr(), mycatName)){
				context.setSuccess(false);
				context.setMsg("配置信息错误,连接服务失败!");
				return context;
			}
			RainbowContext mycatContext = new RainbowContext("mycatService", "insert");
			String jrdsconfg = System.getProperty("webapp.root") + "/WEB-INF/jrdsconf/hosts/";
			jrdsconfg = jrdsconfg + "D_" + context.getAttr("ip") + "_" + context.getAttr("mangerPort") + ".xml";
			context.addAttr("jrdsfile", jrdsconfg);
			mycatContext.setAttr(context.getAttr());
			context = SoaManager.getInstance().invoke(mycatContext);
			context.addAttr("jrdsName", new ObjectId().toString());
			JrdsUtils.getInstance().newJrdsFile("/templet/mycatjrds.ftl", jrdsconfg, context.getAttr());
		} catch (Exception e) {
			context.setSuccess(false);
			context.setMsg(e.getMessage());
		}
		return context;
	}
	


	
}
