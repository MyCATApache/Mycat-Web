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
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class RoleService extends BaseService {
	private static final String NAMESPACE = "SYSROLE";
	private static final String[] AUTHS = {"deleteAuthButton","deleteAuthresouce","deleteAuthservice","deleteAuthuser"};

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	
	public RainbowContext insert(RainbowContext context) {
		try{
			List<Map<String,Object>> roleTypeList = context.getRows();
			String orgId = (String)context.getAttr("orgId");
			String orgName = (String)context.getAttr("orgName");
			for(Map<String,Object> map : roleTypeList){
				map.put("roleCode",orgId+"_" + map.get("roleTypeCode"));
				map.put("roleName",orgName+"_" + map.get("roleTypeName"));
				map.put("guid", new ObjectId().toString());
				map.put("createTime", new Date());
				map.put("orgCode", orgId);
				context.addAttr("createUser", RainbowSession.getUserName());
				getDao().insert(NAMESPACE, "insert", map);
			}
			context.clearRows();
			context.clearAttr();
			context.setMsg("新增成功!");
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("新增失败!");
		}
		
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		try{
			List<Map<String,Object>> roleNameList = context.getRows();
			for(Map<String,Object> map : roleNameList){
				map.put("roleName",map.get("roleName"));
				map.put("guid", map.get("guid"));
				getDao().update(NAMESPACE, "update", map);
			}
			context.setMsg("成功修改,"+roleNameList.size()+"条记录!");
			context.clearRows();
			context.clearAttr();
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("修改失败!");
		}
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		for(Map<String,Object> map : context.getRows()){				
			for(String auth : AUTHS){
				getDao().delete(NAMESPACE, auth, map);
			}
		}
		return context;
	}
}