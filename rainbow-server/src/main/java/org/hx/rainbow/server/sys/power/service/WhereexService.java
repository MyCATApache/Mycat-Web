/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.hx.rainbow.server.sys.power.service;
import java.util.Date;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WhereexService extends BaseService {
	private static final String NAMESPACE = "SYSWHEREEX";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		for (Map<String, Object> row : context.getRows()) {
			context.setAttr(row);
			if(row.get("valueType") == null){
				row.put("valueType", "text");
			}
			context.addAttr("guid", new ObjectId().toString());
			context.addAttr("createTime", new Date());
			context.addAttr("createUser", RainbowSession.getUserName());
			super.insert(context, NAMESPACE);
			context.getAttr().clear();
		}
		return context;
	}
	
	public RainbowContext insertBatch(RainbowContext context) {
		for (Map<String, Object> row : context.getRows()) {
			context.setAttr(row);
			context.addAttr("guid", new ObjectId().toString());
			context.addAttr("createTime", new Date());
			context.addAttr("createUser", RainbowSession.getUserName());
			context.addAttr("whereName", context.getAttr("whereName"));
			context.addAttr("whereCode", context.getAttr("whereCode"));
			super.insert(context, NAMESPACE);
			context.getAttr().clear();
		}
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		try{
			for (Map<String, Object> row : context.getRows()) {
				context.setAttr(row);
				super.update(context, NAMESPACE);
				context.getAttr().clear();
			}
			context.setMsg("成功修改" + context.getRows().size() + "条记录!");
		}catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("修改失败,系统异常!");
		}
		return context;
	}
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
}