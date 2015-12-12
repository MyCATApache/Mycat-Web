package org.mycat.web.service;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("mycatService")
public class MycatService extends BaseService {
	private static final String NAMESPACE = "SYSMYCAT";

	public RainbowContext query(RainbowContext context) {
		//super.query(context, NAMESPACE);
		return queryByPage(context);
	}


	public RainbowContext queryByPage(RainbowContext context) {
		//super.queryByPage(context, NAMESPACE);
		String mycatName=(String)context.getAttr("mycatName");	
		context.addRows(ZookeeperService.getInstance().getMycat("mycatName",mycatName));
		context.setTotal(context.getRows().size());
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
			ZookeeperService.getInstance().insertMycat(guid,context.getAttr());
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
			ZookeeperService.getInstance().insertMycat(guid,context.getAttr());
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
			Map<String, Object> data =ZookeeperService.getInstance().getMycatNode(guid);
			ZookeeperService.getInstance().delMycat(guid);
			context.setMsg("删除成功!");
			context.setSuccess(true);
			jrdsfile = (String)data.get("jrdsfile");
	
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
	}
}
