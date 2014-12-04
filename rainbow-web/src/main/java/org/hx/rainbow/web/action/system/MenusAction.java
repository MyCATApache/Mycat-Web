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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.JsonUtil;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.hx.rainbow.web.model.ParamData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.serializer.SerializerFeature;

@Controller
@RequestMapping("/menusAction")
public class MenusAction {
	
	@RequestMapping("/query")
	@ResponseBody
	public String query(RainbowContext context,HttpServletRequest request){
		RainbowSession.web2Service(request);
		context.setService("menuService");
		context.setMethod("query");
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	@RequestMapping("/favorites")
	@ResponseBody
	public String favorites(RainbowContext context,HttpServletRequest request){
		RainbowSession.web2Service(request);
		context.setService("menuService");
		context.setMethod("queryFavorites");
		context = SoaManager.getInstance().invoke(context);
		for(Map<String,Object> data : context.getRows()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			attributes.put("url", data.get("url"));
			attributes.put("isAuth", data.get("isAuth"));
			attributes.put("isCache", data.get("isCache"));
			data.remove("url");
			data.remove("isAuth");
			data.remove("isCache");
			data.put("attributes", attributes);
		}
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	@RequestMapping("/queryTree")
	@ResponseBody
	public String queryTree(HttpServletRequest request){
		RainbowSession.web2Service(request);
		RainbowContext context = new RainbowContext();
		context.setService("menuService");
		context.setMethod("queryTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	@RequestMapping("/queryComboxTree")
	@ResponseBody
	public String queryComboxTree(HttpServletRequest request){
		RainbowSession.web2Service(request);
		RainbowContext context = new RainbowContext();
		context.setService("menuService");
		context.setMethod("queryComboxTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	
	@RequestMapping("/getMenus")
	@ResponseBody
	public String getMenus(HttpServletRequest request){
		try{
			String code = request.getParameter("code");
			String id = request.getParameter("id");
			if(id != null){
				code = null;
			}
			RainbowContext context = new RainbowContext();
			context.setService("menuService");
			context.setMethod("queryMenus");
			context.addAttr("code", code);
			context.addAttr("id", id);
			RainbowSession.web2Service(request);
			context = SoaManager.getInstance().invoke(context);
			return JsonUtil.getInstance().object2JSON(changeTree(context.getRows()), SerializerFeature.WriteDateUseDateFormat);
		}catch (Exception e) {
			e.printStackTrace();
			return "{\"result\":null}";
		}
	}
	
	@RequestMapping("/addMenu")
	@ResponseBody
	public RainbowContext addMenu(ParamData data){
		RainbowContext context = new RainbowContext();
		try{
			context.setService("menuService");
			context.setMethod("insert");
			context.setAttr(data.getAttr());
			context = SoaManager.getInstance().invoke(context);
			context.clearAttr();
		}catch (Exception e) {
			if(e instanceof AppException){
				context.setMsg(e.getMessage());
			}else{
				context.setMsg("系统异常!");
			}
			context.setSuccess(false);
		}
		return context;
	}
	
	
	@RequestMapping("/updateMenu")
	@ResponseBody
	public RainbowContext updateMenu(ParamData data){
		RainbowContext context = new RainbowContext();
		try{
			context.setService("menuService");
			context.setMethod("update");
			context.setAttr(data.getAttr());
			context = SoaManager.getInstance().invoke(context);
		}catch (Exception e) {
			if(e instanceof AppException){
				context.setMsg(e.getMessage());
			}else{
				context.setMsg("系统异常!");
			}
			context.setSuccess(false);
		}
		return context;
	}
	
	private List<Map<String,Object>> changeTree(List<Map<String,Object>> treeList){
		for(Map<String,Object> data : treeList){
			Map<String,Object> attributes = new HashMap<String,Object>();
			attributes.put("url", data.get("url"));
			attributes.put("pageName", data.get("pageName"));
			attributes.put("isCache", data.get("isCache"));
			data.remove("url");
			data.remove("pageName");
			data.remove("isCache");
			data.put("attributes", attributes);
		}
		return treeList;
	}
}