package org.mycat.web.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.util.DataSourceUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class ExplainService extends BaseService{
	private static final String NAMESPACE = "EXPLAINSQL";
	
	public RainbowContext base(RainbowContext context,String cmd) {
		String datasource = (String)context.getAttr("ds");
		if(datasource ==  null || datasource.isEmpty()){
			return context;
		}
		try {
			if(!DataSourceUtils.getInstance().register(datasource, "8066")){
				context.setSuccess(false);
				context.setMsg("数据源连接失败!");
				return context;
			}
		} catch (Exception e) {
			
		}
		context.setDs(datasource + "8066");
		super.query(context, NAMESPACE, cmd);
		return context;
	}
	
	public RainbowContext explainMycat(RainbowContext context) {
		return base(context,"explainMycat");
	}
	
	public RainbowContext explainMysql(RainbowContext context) {
		
		return base(context,"explainMysql");
	}
}
