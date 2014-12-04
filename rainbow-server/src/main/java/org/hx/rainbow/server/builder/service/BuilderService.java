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
package org.hx.rainbow.server.builder.service;


import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class BuilderService extends BaseService{

	private static final String NAMESPACE = "BUILDER";
	private static final String QUERYCOMBOX = "queryCombox";
	private static final String QUERYCOMBOXCOUNT = "queryComboxCount";
	private static final String QUERYVIEWCOMBOX = "queryViewCombox";
	private static final String QUERYVIEWCOMBOXCOUNT = "queryViewComboxCount";
	private static final String QUERYDBLINK = "queryDBLink";
	private static final String COUNTDBLINK = "countDBLink";

	public RainbowContext queryCombox(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.queryByPage(context, NAMESPACE,QUERYCOMBOX,QUERYCOMBOXCOUNT);
		return context;
	}
	public RainbowContext queryViewCombox(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.queryByPage(context, NAMESPACE,QUERYVIEWCOMBOX,QUERYVIEWCOMBOXCOUNT);
		return context;
	}
	public RainbowContext queryDBLink(RainbowContext context) {
		String value = (String)context.getAttr("q");
		if(value == null || value.trim().isEmpty()){
			context.removeAttr("q");
		}else{
			context.addAttr("q", value.toUpperCase());
		}
		super.queryByPage(context, NAMESPACE,QUERYDBLINK,COUNTDBLINK);
		return context;
	}
	public RainbowContext query(RainbowContext context) {
		String tableName = (String) context.getAttr("tableName");
		if(tableName == null || tableName.trim().isEmpty()){
			context.setMsg("请选择要查看的表");
			return context;
		}
		String dblinkName = (String) context.getAttr("dblinkName");
		if(dblinkName==null||dblinkName.trim().isEmpty()){
			context.removeAttr("dblinkName");
		}
		super.query(context, NAMESPACE);		
		return context;
	}
	
}