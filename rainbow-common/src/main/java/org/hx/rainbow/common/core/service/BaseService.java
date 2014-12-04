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
package org.hx.rainbow.common.core.service;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.dao.Dao;
import org.hx.rainbow.common.exception.AppException;

/**
 * 
 * @author huangxin
 *
 */
public class BaseService {
	private static final String QUERY = "query";
	private static final String COUNT = "count";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String INSERT = "insert";
	
	@Inject
	@Named("daoMybatis")
	private Dao dao;
	
	
	
	public Dao getDao() {
		return dao;
	}


	public RainbowContext query(RainbowContext context,String namespace){
		try{
			if(context.getAttr().size() == 0){
				context.setRows(dao.query(namespace,QUERY));
			}else{
				context.setRows(dao.query(namespace,QUERY, context.getAttr()));
			}
			context.setMsg("查询到" + context.getRows().size() + "条记录!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败,系统异常!");
		}
		return context;
	}
	
	
	public RainbowContext queryByPage(RainbowContext context,String namespace){
		try{
			if(context.getAttr().size() == 0){
				context.setRows(dao.query(namespace,QUERY,context.getLimit(),context.getPage()));
				context.setTotal(dao.count(namespace, COUNT));
			}else{
				context.setRows(dao.query(namespace,QUERY, context.getAttr(),context.getLimit(),context.getPage()));
				context.setTotal(dao.count(namespace, COUNT,context.getAttr()));
			}
			context.setMsg("查询到" + context.getRows().size() + "条记录!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败,系统异常!");
		}
		return context;
	}
	
	
	public RainbowContext query(RainbowContext context,String namespace,String statement){
		try{
			if(statement == null || statement.length() == 0){
				statement = QUERY;
			}
			context.setRows(dao.query(namespace,statement, context.getAttr()));
			context.setMsg("查询到" + context.getRows().size() + "条记录!");
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败,系统异常!");
		}
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context,String namespace,String queryStatement,String countStatement){
		try{
			if(queryStatement == null || queryStatement.length() == 0){
				queryStatement = QUERY;
			}
			if(countStatement == null || countStatement.length() == 0){
				countStatement = COUNT;
			}
			context.setRows(dao.query(namespace,queryStatement, context.getAttr(),context.getLimit(),context.getPage()));
			context.setTotal(dao.count(namespace, countStatement,context.getAttr()));
			context.setMsg("查询到" + context.getRows().size() + "条记录!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败,系统异常!");
		}
		return context;
	}
	
	public RainbowContext queryCombox(RainbowContext context,String namespace,String queryStatement,String countStatement){
		try{
			if(queryStatement == null || queryStatement.length() == 0){
				queryStatement = QUERY;
			}
			if(countStatement == null || countStatement.length() == 0){
				countStatement = COUNT;
			}
			String value = (String)context.getAttr("q");
			if(value != null && !value.trim().isEmpty()){
				if(StringUtils.isNumeric(value)){
					context.addAttr("id", value);
				}else{
					context.addAttr("name", value);
				}
			}
			context = queryByPage(context,namespace,queryStatement,countStatement);
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("查询失败,系统异常!");
		}
		return context;
	}
	
	public RainbowContext insert(RainbowContext context,String namespace){
		try{
			dao.insert(namespace, INSERT, context.getAttr());
			context.setMsg("新增成功!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			context.setSuccess(false);
			throw new AppException("新增失败,系统异常!");
		}
		return context;
	}
	
	public RainbowContext update(RainbowContext context,String namespace){
		try{
			int count = dao.update(namespace, UPDATE, context.getAttr());
			context.setMsg("成功修改" + count + "条记录!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			context.setSuccess(false);
			throw new AppException("修改失败,系统异常!");
		}
		return context;
	}
	
	public RainbowContext delete(RainbowContext context,String namespace){
		try{
			for(Map<String,Object> map : context.getRows()){				
				dao.delete(namespace, DELETE, map);
			}
			context.setMsg("成功删除" + context.getRows().size() + "条记录!");
			context.setSuccess(true);
		}catch (Exception e) {
			e.printStackTrace();
			context.setSuccess(false);
			throw new AppException("删除失败,系统异常!");
		}
		return context;
	}
}