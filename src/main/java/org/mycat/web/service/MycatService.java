package org.mycat.web.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.DateUtil;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.DataSourceUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("mycatService")
public class MycatService extends BaseService {
	private static final String NAMESPACE = "SYSMYCAT";
	private static final String MANAGER_NAMESPACE = "MYCATMANAGER";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}

	public RainbowContext monitor(RainbowContext context) throws Exception {
		String con = (String) context.getAttr("con");
		DataSourceUtils.register(con);
		context.setDs(con);
		
		String monitor=(String) context.getAttr("monitor");
		
		context = super.query(context, MANAGER_NAMESPACE, "monitor");

		if (context.getRows().size() > 0) {
			
			if(monitor.startsWith("time.")){
				
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATEDETAIL_PATTERN);
				for(Map<String, Object> row : context.getRows()){
					String key = row.keySet().iterator().next();
					String t= row.get(key).toString();
					if(NumberUtils.isNumber(t)){
						row.put("NOW_TIME", sdf.format(new Date(Long.valueOf(t))));
					}
				}
			}
			
			Map<String, Object> row = context.getRow(0);
			List<String> keys = new ArrayList<String>(row.size());
			for (String key : row.keySet()) {
				keys.add(key);
			}
			context.addAttr("keys", keys);
		}
		
		
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
		DataSourceUtils.remove((String)context.getAttr("mycatName"));
		context.getAttr().clear();
		return context;
	}
}
