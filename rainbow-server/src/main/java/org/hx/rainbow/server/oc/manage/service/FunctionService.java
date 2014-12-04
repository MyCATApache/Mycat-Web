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
package org.hx.rainbow.server.oc.manage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FunctionService extends BaseService {
	private static final String NAMESPACE = "OCFUNCTION";
	private static final String OCFUNCTIONPARAM = "OCFUNCTIONPARAM";
	private static final String INSERT = "insert";
	private static final String DELETE = "delete";
	private static final String QUERY_COMBOX = "queryCombox";

	
	public RainbowContext queryCombox(RainbowContext context) {
		super.query(context, NAMESPACE, QUERY_COMBOX);
		return context;
	}
	
	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		context.addAttr("createUser", RainbowSession.getUserName());
		super.insert(context, NAMESPACE);
		
		if(context.isSuccess()){
			
			insertParam(context);
		}
		
		context.getAttr().clear();
		
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		
		super.update(context, NAMESPACE);
		
		if(context.isSuccess()){
			
			deleteParam(context);
			
			insertParam(context);
		}
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		
		super.delete(context, NAMESPACE);
		
		if(context.isSuccess()){
			
			deleteParam(context);
		}
		
		context.getAttr().clear();
		
		return context;
	}
	
	private RainbowContext insertParam(RainbowContext context){
		
		try{
			
			String paramKeyStr = (String) context.getAttr("paramKey");
			String paramValueStr = (String) context.getAttr("paramValue");
			String functionName = (String) context.getAttr("name");
			
			String paramKeys[] = paramKeyStr.split(",");
			String paramValues[] = paramValueStr.split(",");
			
			context.getAttr().clear();
			
			for(int i = 0; i < paramKeys.length; i++){
				
				context.setService("functionParamService");
				context.setMethod("insert");
				context.addAttr("functionName",functionName);
				context.addAttr("paramKey", paramKeys[i]);
				context.addAttr("paramValue", paramValues[i]);
				this.getDao().insert(OCFUNCTIONPARAM, INSERT, context.getAttr());
			}
			
			context.setSuccess(true);
			
		}catch (Exception e) {
			e.printStackTrace();
			context.setSuccess(false);
			context.setMsg("操作失败，系统异常");
		}
		
		return context;
	}
	
	private RainbowContext deleteParam(RainbowContext context){
		
		try{
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("functionName",context.getAttr("name"));
			this.getDao().delete(OCFUNCTIONPARAM, DELETE, param);
			
			context.setSuccess(true);
			
		}catch (Exception e) {
			e.printStackTrace();
			context.setSuccess(false);
			context.setMsg("操作失败，系统异常");
		}
		
		 return context;
	}
}