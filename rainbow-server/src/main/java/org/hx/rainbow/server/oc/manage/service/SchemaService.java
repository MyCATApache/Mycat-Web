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

import java.util.ArrayList;
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
public class SchemaService extends BaseService {
	private static final String SCHEMANAMESPACE = "OCSCHEMA";
	private static final String SCHEMAMAPNAMESPACE = "OCSCHEMAMAP";
	private static final String SCHEMATABLENAMESPACE = "OCTABLE";
	private static final String SCHEMADATANODENAMESPACE = "OCDATANODE";
	private static final String SCHEMADATAHOSTNAMESPACE = "OCDATAHOST";
	private static final String SCHEMADATAHOSTMAPNAMESPACE = "OCDATAHOSTMAP";
	private static final String SCHEMAOCHOSTNAMESPACE = "OCHOST";
	private String statement="querySchemaTableList";

	public RainbowContext querySchema(RainbowContext context) {
		Map<String, Object> schema = new HashMap<String, Object>();
		List<Map<String, Object>> schemaList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dataNodelist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> dataHostlist = new ArrayList<Map<String, Object>>();
		/** find schema name */
		super.query(context, SCHEMANAMESPACE);
		schemaList.addAll(context.getRows());
		/** find table */
		clear(context);
		
		for (Map<String, Object> map : schemaList) {
			context.addAttr("schemaName", map.get("name"));
			super.query(context, SCHEMAMAPNAMESPACE,statement);
			/** find table child */
			List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
			tableList.addAll(context.getRows());
			for (Map<String, Object> table : tableList) {
				searchTableChildList(table, context);
			}
			map.put("tableList", tableList);
		}
		schema.put("schemaList", schemaList);
		
		/** find dataNode */
		clear(context);
		super.query(context, SCHEMADATANODENAMESPACE);
		dataNodelist.addAll(context.getRows());
		schema.put("dataNode", dataNodelist);
		
		clear(context);
		/** find datahost */
		super.query(context, SCHEMADATAHOSTNAMESPACE);
		dataHostlist.addAll(context.getRows());
		searchHostChildList(dataHostlist, context);
		schema.put("dataHost", dataHostlist);
		
		clear(context);
		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		params.add(schema);
		context.setRows(params);
		return context;
	}

	/**
	 * 递归查询子节点
	 * 
	 * @param table
	 */
	private void searchTableChildList(Map<String, Object> table,
			RainbowContext context) {
		String guid = table.get("guid") == null ? "" : table.get("guid")
				.toString();
		if (guid != null && !"".equals(guid)) {
			clear(context);
			List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
			context.addAttr("parentName", guid);
			super.query(context, SCHEMATABLENAMESPACE);
			childList.addAll(context.getRows());
			table.put("childlist", childList);
			for (Map<String, Object> child : childList) {
				searchTableChildList(child, context);
			}
		}
	}

	/**
	 * 查询主机关联主从库
	 * 
	 * @param host
	 * @param context
	 */
	private void searchHostChildList(List<Map<String, Object>> host,
			RainbowContext context) {
		if (host != null && host.size() > 0) {
			for (Map<String, Object> map : host) {
				clear(context);
				context.addAttr("dataHost", map.get("name"));
				super.query(context, SCHEMADATAHOSTMAPNAMESPACE);
				if (context.getRows() != null && context.getRows().size() > 0) {
					List<Map<String, Object>> writeTable = new ArrayList<Map<String, Object>>();
					writeTable.addAll(context.getRows());
					clear(context);
					searchWriteChildList(writeTable, context);
					map.put("writeList", writeTable);
				}
			}
		}
	}

	/**
	 * 查询只读节点
	 * 
	 * @param writeList
	 * @param context
	 */
	private void searchWriteChildList(List<Map<String, Object>> writeList,
			RainbowContext context) {
		if (writeList != null && writeList.size() > 0) {
			for (Map<String, Object> map : writeList) {
				clear(context);
				context.addAttr("parentHost", map.get("guid"));
				super.query(context, SCHEMAOCHOSTNAMESPACE);
				if (context.getRows() != null && context.getRows().size() > 0) {
					List<Map<String, Object>> readList = new ArrayList<Map<String, Object>>();
					readList.addAll(context.getRows());
					map.put("readList", readList);
				}
			}
		}
	}

	private void clear(RainbowContext context) {
		if (context.getRows().size() > 0) {
			context.clearRows();
		}
		if (context.getAttr().size() > 0) {
			context.clearAttr();
		}
	}

	public RainbowContext query(RainbowContext context) {
		super.query(context, SCHEMANAMESPACE);
		return context;
	}

	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, SCHEMANAMESPACE);
		return context;
	}

	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		context.addAttr("createUser", RainbowSession.getUserName());
		super.insert(context, SCHEMANAMESPACE);
		context.getAttr().clear();
		return context;
	}

	public RainbowContext update(RainbowContext context) {
		super.update(context, SCHEMANAMESPACE);
		context.getAttr().clear();
		return context;
	}

	public RainbowContext delete(RainbowContext context) {
		super.delete(context, SCHEMANAMESPACE);
		context.getAttr().clear();
		return context;
	}
}