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
package org.mycat.web.action;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.asm.ClassVisitor;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.JsonUtil;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.serializer.SerializerFeature;

@Controller
@RequestMapping("/dispatcherAction")
public class DispatcherAction {
	private static final List<String> keyList =Arrays.asList("service","method","rows","page"); 
 	private static final String SERVICE = "service";
	private static final String METHOD = "method";
	@RequestMapping("/execute")
	@ResponseBody
	public RainbowContext execute(RainbowContext context,HttpServletRequest request){
		long begin = System.currentTimeMillis();
		String service = null;
		String method = null;
		try{
			service = request.getParameter(SERVICE);
			if(service == null || service.length() == 0){
				context.setMsg("服务名为空,请求失败!");
				context.setSuccess(false);
				return context;
			}
		    method = request.getParameter(METHOD);
			if(method == null || method.length() == 0){
				context.setMsg("方法名为空,请求失败!");
				context.setSuccess(false);
				return context;
			}
			
			RainbowSession.web2Service(request);
			context = SoaManager.getInstance().invoke(context);
		}catch (Exception e) {
			if ((e instanceof AppException)){	
				context.setMsg(e.getMessage());
			}else{
				context.setMsg("系统异常!");
			}
			context.setSuccess(false);
		}finally{
			System.out.println("DispatcherAction call:["+service + "." + method + "] to spend:"+(System.currentTimeMillis() - begin)+"ms");
		}
		return context;
	}
	
	@RequestMapping("/query")
	@ResponseBody
	public RainbowContext query(HttpServletRequest request){
		long begin = System.currentTimeMillis();
		RainbowContext context = new RainbowContext();
		String service = null;
		String method = null;
		try{
			service = request.getParameter(SERVICE);
			if(service == null || service.length() == 0){
				context.setMsg("服务名为空,请求失败!");
				context.setSuccess(false);
				return context;
			}
			method = request.getParameter(METHOD);
			if(method == null || method.length() == 0){
				context.setMsg("方法名为空,请求失败!");
				context.setSuccess(false);
				return context;
			}
			
			context.setService(service);
			context.setMethod(method);
			context.setLimit(Integer.parseInt(request.getParameter("rows")==null?"10":request.getParameter("rows")));
			context.setPage(Integer.parseInt(request.getParameter("page")==null?"0":request.getParameter("page")));
			setAttr(context,request);
			RainbowSession.web2Service(request);
			
			context = SoaManager.getInstance().callNoTx(context);
		}catch (Exception e) {
			if ((e instanceof AppException)){	
				context.setMsg(e.getMessage());
			}else{
				context.setMsg("系统异常!");
			}
			context.setSuccess(false);
		}finally{
			System.out.println("DispatcherAction call:["+service + "." + method + "] to spend:"+(System.currentTimeMillis() - begin)+"ms");
		}
		return context;
	}
	
	@RequestMapping("/queryTree")
	@ResponseBody
	public String queryTree(HttpServletRequest request){
		long begin = System.currentTimeMillis();
		RainbowContext context = new RainbowContext();
		String service = null;
		String method = null;
		try{
			service = request.getParameter(SERVICE);
			if(service == null || service.length() == 0){
				context.setMsg("服务名为空,请求失败!");
				context.setSuccess(false);
				return "[]";
			}
			method = request.getParameter(METHOD);
			if(method == null || method.length() == 0){
				context.setMsg("方法名为空,请求失败!");
				context.setSuccess(false);
				return "[]";
			}
			
			context.setService(service);
			context.setMethod(method);
			context.setLimit(Integer.parseInt(request.getParameter("rows")==null?"10":request.getParameter("rows")));
			context.setPage(Integer.parseInt(request.getParameter("page")==null?"10":request.getParameter("page")));
			setAttr(context,request);
			RainbowSession.web2Service(request);
			
			context = SoaManager.getInstance().callNoTx(context);
			return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
		}catch (Exception e) {
			return "[]";
		}finally{
			System.out.println("DispatcherAction call:["+service + "." + method + "] to spend:"+(System.currentTimeMillis() - begin)+"ms");
		}
	}
	
	@RequestMapping("/comboxCode")
	@ResponseBody
	public String comboxCode(HttpServletRequest request){
		long begin = System.currentTimeMillis();
		RainbowContext context = new RainbowContext();
		String service = null;
		String method = null;
		try{
			String code = request.getParameter("code");
			service = request.getParameter(SERVICE);
			
			if(service == null || service.length() == 0){
				service = "codeService";
			}
			method = request.getParameter(METHOD);
			if(method == null || method.length() == 0){
				method = "getCode";
			}
			if((service.equals("codeService") && method.equals("getCode")) && (code == null || code.length() == 0)){
				return "[]";
			}
			
			context.setService(service);
			context.setMethod(method);
			context.addAttr("code", code);
			
			context = SoaManager.getInstance().callNoTx(context);
			if(context.isSuccess()){
				return JsonUtil.getInstance().object2JSON(context.getRows());
			}else{
				return "[]";
			}
		}catch (Exception e) {
			return "[]";
		}finally{
			System.out.println("DispatcherAction call:["+service + "." + method + "] to spend:"+(System.currentTimeMillis() - begin)+"ms");
		}
	}
	
	@RequestMapping("/queryComboxTree")
	@ResponseBody
	public String queryComboxTree(HttpServletRequest request){
		long begin = System.currentTimeMillis();
		RainbowContext context = new RainbowContext();
		String service = null;
		String method = null;
		try{
			service = request.getParameter(SERVICE);
			if(service == null || service.length() == 0){
				context.setMsg("服务名为空,请求失败!");
				context.setSuccess(false);
				return "[]";
			}
			method = request.getParameter(METHOD);
			if(method == null || method.length() == 0){
				context.setMsg("方法名为空,请求失败!");
				context.setSuccess(false);
				return "[]";
			}
			
			context.setService(service);
			context.setMethod(method);
			context.addAttr("id", request.getParameter("id"));
			setAttr(context,request);
			
			context = SoaManager.getInstance().invoke(context);
			return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
		}catch (Exception e) {
			return "[]";
		}finally{
			System.out.println("DispatcherAction call:["+service + "." + method + "] to spend:"+(System.currentTimeMillis() - begin)+"ms");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setAttr(RainbowContext context ,HttpServletRequest request){
		Enumeration<String> names =  request.getParameterNames();		
		while(names.hasMoreElements()){
			String key = names.nextElement();
			if(!keyList.contains(key)){
				context.addAttr(key, request.getParameter(key));
			}
		}
	}
}