package org.mycat.web.service.cluster;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.mycat.web.model.cluster.DataHost;
import org.mycat.web.service.AbstractConfigSevice;
import org.mycat.web.util.Constant;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("dataHostService")
public class DataHostService extends AbstractConfigSevice {
	
	private Class<DataHost> clazz = DataHost.class;
	private String menuPath = Constant.MYCAT_CLUSTER_KEY;
	private String zkPath = Constant.CLUSTER_DATAHOST;
	
	public RainbowContext queryByPage(RainbowContext context){
		super.queryByPage(context, clazz);
	    return context;
	}
	
	public RainbowContext queryAll(RainbowContext context){
		super.queryAll(context, clazz);
	    return context;
	}

	public RainbowContext insert (RainbowContext context) throws Exception{
		super.insert(context, clazz);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		super.update(context, clazz);
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		super.delete(context);
		return context;
	}
	
	public String getPath(String zkId,String guid){
		String Path = ZKPaths.makePath(menuPath, zkId,zkPath,guid);
		return Path;
	}
}
