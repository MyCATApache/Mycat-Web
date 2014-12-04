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
package org.hx.rainbow.web.action.system;

import javax.servlet.http.HttpServletRequest;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.serializer.SerializerFeature;

@Controller
@RequestMapping("/orgAction")
public class OrgAction {
	
	@RequestMapping("/query")
	@ResponseBody
	public String query(RainbowContext context){
		context.setService("orgService");
		context.setMethod("query");
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	@RequestMapping("/queryTree")
	@ResponseBody
	public String queryTree(HttpServletRequest request){
		RainbowContext context = new RainbowContext();
		context.setService("orgService");
		context.setMethod("queryTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	
	@RequestMapping("/queryComboxTree")
	@ResponseBody
	public String queryComboxTree(HttpServletRequest request){
		RainbowContext context = new RainbowContext();
		context.setService("orgService");
		context.setMethod("queryComboxTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
}