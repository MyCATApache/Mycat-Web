package org.mycat.web.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.util.DataSourceUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ShowService extends BaseService {
	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";

	public RainbowContext base(RainbowContext context,String cmd) {
		String datasource = (String)context.getAttr("ds");
		try {
			if(!DataSourceUtils.getInstance().register(datasource)){
				context.setSuccess(false);
				context.setMsg("数据源连接失败!");
				return context;
			}
		} catch (Exception e) {
			
		}
		context.setDs(datasource);
		super.query(context, SYSPARAM_NAMESPACE, cmd);
		return context;
	}
	
	public RainbowContext sysparam(RainbowContext context) {
		return base(context,"sysparam");
	}
	
	public RainbowContext sql(RainbowContext context) {
		return base(context,"sql");
	}
	
	public RainbowContext sqlslow(RainbowContext context) {
		return base(context,"sqlslow");
	}	
}
