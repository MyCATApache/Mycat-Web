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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.security.md5.Md5;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class UserService extends BaseService{
	private static final String NAMESPACE = "SYSUSER";
	private static final String QUERYAUTHERUSER = "queryAutherUser";
	private static final String QUERYAUTHERUSERCOUNT = "queryAutherUserCount";
	
	public RainbowContext queryAllUser(RainbowContext context){
		return context;
	}
	
	public RainbowContext checkLogin(RainbowContext context){
		return context;
	}
	
	public RainbowContext query(RainbowContext context){
		return super.query(context, NAMESPACE);
	}
	
	public RainbowContext queryByPage(RainbowContext context){
		return super.queryByPage(context, NAMESPACE);
	}
	public RainbowContext queryAutherUser(RainbowContext context){
		String roleCode = (String) context.getAttr("roleCode");
		if(roleCode == null || roleCode.trim().isEmpty()){
			context.setMsg("角色代码不能为空");
			return context;
		}
		return super.queryByPage(context, NAMESPACE,QUERYAUTHERUSER,QUERYAUTHERUSERCOUNT);
	}
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("password", Md5.getInstance().encrypt((String)context.getAttr().get("loginId")));
		context.addAttr("createUser", RainbowSession.getUserName());
		context.addAttr("createTime", new Date());
		super.insert(context, NAMESPACE);
		context.getAttr().clear();
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
	
	public RainbowContext changepwd(RainbowContext context){
		
		String password = (String)context.getAttr("password");
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("loginId", RainbowSession.getLoginId());
		paramData.put("password", Md5.getInstance().encrypt(password));
		List<Map<String,Object>> users = getDao().query(NAMESPACE, "query", paramData);
		if(users.size() != 1){
			context.setMsg("原始密码不正确!");
			return context;
		}else{
			String newPassword = (String)context.getAttr("newPassword");
			Map<String,Object> user = users.get(0);
			user.put("password", Md5.getInstance().encrypt(newPassword));
			getDao().update(NAMESPACE, "update",  user);
			context.setMsg("密码修改成功!");
			return context;
		}
		
	}
}