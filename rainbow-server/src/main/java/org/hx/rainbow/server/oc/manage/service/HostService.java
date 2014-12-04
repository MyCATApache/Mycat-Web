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
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class HostService extends BaseService {
	private static final String NAMESPACE = "OCHOST";
	private static final String QUERY_TREE = "queryTree";
	private static final String QUERY_COMBOX = "queryCombox";
	private static final String QUERY_LINK = "queryLink";
	private static final String COUNT_LINK = "countLink";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryLink(RainbowContext context) {
		String datahost = (String)context.getAttr("datahost");
		if(datahost ==  null || datahost.isEmpty()){
			return context;
		}
		super.queryByPage(context, NAMESPACE,QUERY_LINK,COUNT_LINK);
		return context;
	}

	public RainbowContext queryTree(RainbowContext context){
		super.query(context, NAMESPACE, QUERY_TREE);
		return context;
	}
	
	public RainbowContext queryCombox(RainbowContext context){
		super.query(context, NAMESPACE, QUERY_COMBOX);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		context.addAttr("createUser", RainbowSession.getUserName());
		super.insert(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context){
		List<Map<String,Object>> list = context.getRows();
		if(list.size() > 0){
			String code = (String)list.get(0).get("guid");
			queryChilds(NAMESPACE,QUERY_TREE,code,context.getRows());
			super.delete(context, NAMESPACE);
		}
		context.getRows().clear();
		return context;
	}

	public void queryChilds(String namespace,String statement,String guid,List<Map<String,Object>> list){
		if(guid == null || guid.isEmpty()){
			return;
		}
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("id", guid);
		List<Map<String,Object>> dataList = getDao().query(namespace, statement, paramData);
		if(dataList.size() > 0){
			list.addAll(dataList);
			for(Map<String,Object> map : dataList){
				String parentCode = (String)map.get("code");
				queryChilds(namespace,statement,parentCode,list);
			}
		}
	}
}