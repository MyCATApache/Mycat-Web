package org.mycat.web.service;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("mycatService")
public class MycatService extends BaseService {
	private static final String NAMESPACE = "SYSMYCAT";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}


	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}

	public synchronized RainbowContext insert(RainbowContext context) throws Exception {

		RainbowContext query = new RainbowContext();
		query.addAttr("mycatName", context.getAttr("mycatName"));
		query = super.query(query, NAMESPACE);

		if (query.getRows() != null && query.getRows().size() > 0) {
			context.setMsg("名称已存在");
			context.setSuccess(false);
			return context;
		}

		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		super.insert(context, NAMESPACE);
		return context;
	}

	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}

	public RainbowContext delete(RainbowContext context) {
		
		Map<String, Object> data = super.getDao().get(NAMESPACE, "load", context.getAttr());
		super.getDao().delete(NAMESPACE, "delete", context.getAttr());
		String jrdsfile = (String)data.get("jrdsfile");
		if(jrdsfile != null && !jrdsfile.isEmpty()){
			new File(jrdsfile).delete();
		}
		context.getAttr().clear();
		return context;
	}
}
