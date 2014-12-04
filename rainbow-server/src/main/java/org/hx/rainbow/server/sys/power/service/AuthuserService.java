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
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.hx.rainbow.common.core.service.BaseService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthuserService extends BaseService {
	private static final String NAMESPACE = "SYSAUTHUSER";
	private static final String QUERYNOTINROLE = "queryNotInRole";
	private static final String COUNTNOTINROLE = "countNotInRole";
	private static final String QUERYNOTINUSER = "queryNotInUser";
	private static final String COUNTNOTINUSER = "countNotInUser";
	private static final String INSERT = "insert";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insertByUser(RainbowContext context) {
		try{
			String userGuid = (String)context.getAttr("userGuid");
			String userCode = (String)context.getAttr("userCode");
			for(Map<String,Object> data : context.getRows()){
				String guid = (String)data.get("guid");
				data.put("userGuid", userGuid);
				data.put("userCode", userCode);
				data.put("roleGuid", guid);
				data.put("guid", new ObjectId().toString());
				data.put("createTime", new Date());
				data.put("createUser", RainbowSession.getUserName());
				getDao().insert(NAMESPACE, INSERT,  data);
				context.setMsg("用户授权成功!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			context.setMsg("用户授权失败!,系统异常!");
			context.setSuccess(false);
		}
		return context;
	}
	
	public RainbowContext insertByRole(RainbowContext context) {
		try{
			String roleGuid = (String)context.getAttr("roleGuid");
			String roleCode = (String)context.getAttr("roleCode");
			for(Map<String,Object> data : context.getRows()){
				String guid = (String)data.get("guid");
				String loginId = (String)data.get("loginId");
				data.put("userGuid", guid);
				data.put("userCode", loginId);
				data.put("roleGuid", roleGuid);
				data.put("roleCode", roleCode);
				data.put("guid", new ObjectId().toString());
				data.put("createTime", new Date());
				data.put("createUser", RainbowSession.getUserName());
				getDao().insert(NAMESPACE, INSERT,  data);
				context.setMsg("用户授权成功!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			context.setMsg("用户授权失败!,系统异常!");
			context.setSuccess(false);
		}
		return context;
	}
	
	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext queryNotInRole(RainbowContext context) {
		super.queryByPage(context, NAMESPACE, QUERYNOTINROLE, COUNTNOTINROLE);
		return context;
	}
	public RainbowContext queryNotInUser(RainbowContext context) {
		super.queryByPage(context, NAMESPACE, QUERYNOTINUSER, COUNTNOTINUSER);
		return context;
	}
}