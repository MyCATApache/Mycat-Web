package org.mycat.web.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.util.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service; 

@Lazy
@Service
public class ShowService extends BaseService { 
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowService.class);
	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";  

	public RainbowContext base(RainbowContext context,String cmd) {
		String datasource = (String)context.getAttr("ds");
		if(datasource ==  null || datasource.isEmpty()){
			return context;
		}
		try {
			if(!DataSourceUtils.getInstance().register(datasource)){
				context.setSuccess(false);
				context.setMsg("数据源["+datasource+"]连接失败!");
				return context;
			}
		} catch (Exception e) {
			
		}
		LOGGER.info("数据源["+datasource+"]");
		context.setDs(datasource + "9066");
		if (cmd.equals("sqlslow")){
			String threshold = (String)context.getAttr("threshold");
			if (!(threshold ==  null || threshold.isEmpty())){
				super.query(context, SYSPARAM_NAMESPACE, "setsqlslow");
			}			
		} 
		super.query(context, SYSPARAM_NAMESPACE, cmd); 
		return context;
	}
	
	
	public RainbowContext baseQuery(RainbowContext context,String cmd) {    
		super.queryByPage(context, "SYSPARAM", cmd, cmd+"Count"); 
		return context;
	}
	
	
	public RainbowContext sysparam(RainbowContext context) {
		return base(context,"sysparam");
	}
	
	public RainbowContext sql(RainbowContext context) {
		return baseQuery(context,"sql");
	}
	
	public RainbowContext sqlslow(RainbowContext context) {
		return base(context,"sqlslow");
	}
	public RainbowContext sqlhigh(RainbowContext context) {
		return base(context,"sqlhigh");
	}	
	public RainbowContext sqlsum(RainbowContext context) {
		return base(context,"sqlsum");
	}
	public RainbowContext sqlsumtable(RainbowContext context) {
		return base(context,"sqlsumtable");
	}	
	public RainbowContext syslog(RainbowContext context) {
		return base(context,"syslog");
	}	
	public RainbowContext heartbeat(RainbowContext context) {
		context = base(context,"heartbeat");
		System.out.println(context.getRows().toString());
		return context;
	}
	
	public RainbowContext heartbeatDetail(RainbowContext context) {
		return base(context,"heartbeatDetail");
	}
	
	public RainbowContext dataSouceSynstatus(RainbowContext context) {
		return base(context,"dataSouceSynstatus");
	}
	
	public RainbowContext dataSouceDetail(RainbowContext context) {
		return base(context,"dataSouceDetail");
	}
	
}
