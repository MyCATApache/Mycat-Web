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
package org.hx.rainbow.server.oc.monitor.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.DateUtil;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class MonitorService extends BaseService{
	private static final String NAMESPACE = "OCMONITOR";
	private static final String QUERY_BACKEND = "queryBackend";
	private static final String QUERY_CONNECTION = "queryConnection";
	private static final String QUERY_THREADPOOL = "queryThreadpool";
	private static final String QUERY_HEARTBEAT = "queryHeartbeat";
	private static final String QUERY_DATANODE = "queryDatanode";
	private static final String QUERY_DATASOURCE = "queryDatasource";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_PATTERN);
	
	public RainbowContext addDataBase(RainbowContext context){
		try{
			ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)SpringApplicationContext.getApplicationContext();
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getBeanFactory();
			String serverName = (String)context.getAttr("serverName");
			if(serverName == null || serverName.isEmpty()){
				context.setMsg("服务名不能为空!");
			}
			String url = (String)context.getAttr("url");
			if(url == null || url.isEmpty()){
				context.setMsg("url不能为空!");
			}
			String userName = (String)context.getAttr("userName");
			if(userName == null || userName.isEmpty()){
				context.setMsg("用户名不能为空!");
			}
			String password = (String)context.getAttr("password");
			if(password == null || password.isEmpty()){
				context.setMsg("密码不能为空!");
			}
			beanFactory.registerBeanDefinition(serverName, getDefinition(serverName,url,userName,password));
			context.setMsg("服务创建成功!");
		}catch(Exception ex){
			ex.printStackTrace();
			context.setMsg("服务创建失败!" + ex.getMessage());
			context.setSuccess(false);
		}
		return context;
	}
	private GenericBeanDefinition getDefinition(String serverName, String url, String userName, String password){
		GenericBeanDefinition sessionFactoryDef = new GenericBeanDefinition();
		Map<String, Object> paramData = new HashMap<String, Object>();
		paramData.put("driverClassName" ,"com.mysql.jdbc.Driver");
		paramData.put("url",url);
		paramData.put("username" ,userName);
		paramData.put("password" ,password);
		paramData.put("initialSize" ,5);
		paramData.put("maxActive" ,20);
		paramData.put("maxIdle",10);
		sessionFactoryDef.setBeanClass(BasicDataSource.class);
		sessionFactoryDef.setPropertyValues(new MutablePropertyValues(paramData));
		return sessionFactoryDef;
	}
	
	
	public RainbowContext queryBackend(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_BACKEND));
		return context;
	}
	public RainbowContext queryConnection(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_CONNECTION));
		List<Map<String,Object>> list = context.getRows();
		for(Map<String,Object> dataMap : list){
			dataMap.put("ALIVE_TIME", dataMap.get("ALIVE_TIME(S)"));
			dataMap.remove("ALIVE_TIME(S)");
		}
		return context;
	}
	public RainbowContext queryThreadpool(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_THREADPOOL));
		return context;
	}
	public RainbowContext queryHeartbeat(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_HEARTBEAT));
		List<Map<String,Object>> list = context.getRows();
		for(Map<String,Object> dataMap : list){
			dataMap.put("LAST_ACTIVE_TIME",sdf.format((Date)dataMap.get("LAST_ACTIVE_TIME")));
		}
		return context;
	}
	public RainbowContext queryDatanode(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_DATANODE));
		return context;
	}
	public RainbowContext queryDatasource(RainbowContext context){
		context.setRows(super.getDao().queryMycat(NAMESPACE, QUERY_DATASOURCE));
		List<Map<String,Object>> list = context.getRows();
		for(Map<String,Object> dataMap : list){
			dataMap.put("w", dataMap.get("W/R"));
			dataMap.remove("W/R");
		}
		return context;
	}
}