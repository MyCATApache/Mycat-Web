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
package org.hx.rainbow.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询参数的封装
 * @author hx
 *
 */
public class QueryInfo<T> implements Serializable {

	private static final long serialVersionUID = 6286119125103168413L;

	private String orderBy;

	private int offset;

	private int limit;
	
	private int count;

	private Map<String,Object> param = new HashMap<String,Object>();
	
	private List<T> result = null;
	
	private QueryMode queryMode = QueryMode.BUSINESS;
	
	public  enum QueryMode {	
			/**
			 * 返回数量
			 */
		  	COUNT,
			/**
			 * 返回记录
			 */
			QUERY,
			/**
			 * 执行业务操作
			 */
			BUSINESS
	  }


	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	

	public Map<String,Object> getParam() {
		return param;
	}

	public void setParam(Map<String,Object> param) {
		this.param = param;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void setResult(List<T> list){
		this.result = list;
	}

	public List<T> getResult(){
		return this.result;
	}
	

	public void markQuery(QueryInfo.QueryMode queryMode) {
		this.queryMode = queryMode;
	}

	public QueryMode getMode() {
		return queryMode;
	}

}