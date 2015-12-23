package org.mycat.web.service;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.mycat.web.model.MycatZone;
import org.mycat.web.util.Constant;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("myCatZoneService")
public class MyCatZoneService  extends AbstractConfigSevice{

	private Class<MycatZone> clazz = MycatZone.class;
	private String menuPath = Constant.MYCAT_ZONE_KEY;
	private String zkPath = "";

	@Override
	public String getPath(String zkId,String guid){
		String Path = ZKPaths.makePath(menuPath, zkId,zkPath,guid);
		return Path;
	}
	
	public RainbowContext queryByPage(RainbowContext context){
		super.queryByPage(context, clazz);
	    return context;
	}
	
	public RainbowContext queryAll(RainbowContext context){
		super.queryAll(context, clazz);
	    return context;
	}

	public RainbowContext query(RainbowContext context){
		super.query(context, clazz);
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
}
