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
package org.hx.rainbow.web.action.builder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.util.JsonUtil;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
@RequestMapping("/builderAction")
public class BuilderAction {

	private static final String TABLENAME = "tableName";
	private static final String MODELNAME = "modelName";
	private static final String ROWS = "colunms";
	private static final String JAVA = "java";
	private static final String JSP = "jsp";
	private static final String JS = "js";
	private static final String SERVICE = "Service";
	private static final String XML = "xml";
	private static final String MAPPER = "Mapper";
	private static final String FORM = "Form";
	private static final Map<String,Object> jdbcType = new HashMap<String,Object>();
	static{
		initJdbcType();
	}
	@RequestMapping("/jsp")
	public void buildJsp(HttpServletResponse response, HttpServletRequest request) {
		try {
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			String tableName = request.getParameter(TABLENAME);
			Map<String, Object> tableMap = getTableMap(response,request,getJspName(tableName),tableName,JSP);
			createJsp(tableMap, toClient);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/jspForm")
	public void buildJspForm(HttpServletResponse response, HttpServletRequest request) {
		try {
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			String tableName = request.getParameter(TABLENAME);
			Map<String, Object> tableMap = getTableMap(response,request,getJspName(tableName)+FORM,tableName,JSP);
			createJspForm(tableMap, toClient);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/js")
	public void buildJs(HttpServletResponse response, HttpServletRequest request) {
		try {
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			String tableName = request.getParameter(TABLENAME);
			Map<String, Object> tableMap = getTableMap(response,request,getJspName(tableName),tableName,JS);
			createJs(tableMap, toClient);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/service")
	public void buildService(HttpServletResponse response, HttpServletRequest request) {
		try {
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			String tableName = request.getParameter(TABLENAME);
			Map<String, Object> tableMap = getTableMap(response,request,getServiceName(tableName)+SERVICE,tableName,JAVA);
			createService(tableMap, toClient);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/mapper")
	public void buildMapper(HttpServletResponse response, HttpServletRequest request) {
		try {
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			String tableName = request.getParameter(TABLENAME);
			Map<String, Object> tableMap = getTableMap(response,request,getJspName(tableName)+MAPPER,tableName,XML);
			createSqlMap(tableMap, toClient);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("/whereex")
	@ResponseBody
	public RainbowContext insertIntoWhereEx(RainbowContext context,HttpServletResponse response, HttpServletRequest request) {
		RainbowSession.web2Service(request);
		String rows = request.getParameter(ROWS);
		String tableName = request.getParameter(TABLENAME);
		String modelName = request.getParameter(MODELNAME);
		JsonUtil jsonUtil = JsonUtil.getInstance();
		List<?> rowsList = jsonUtil.json2Object(rows, List.class);
		Map<String,Object> columnsrs = null;
		Map<String,Object> attr = null;
		List<Map<String,Object>> attrs = new ArrayList<Map<String,Object>>();
		//for(Map<String,Object> dataMap : rowsList.)
		for (int i = 0; i < rowsList.size(); i++) {
			columnsrs = jsonUtil.json2Object((rowsList.get(i)).toString(), Map.class);
			String columnComment = (String) columnsrs.get("columnComment");
			if (columnComment.indexOf("(") != -1) {
				columnComment = columnComment.substring(0,columnComment.indexOf("("));
			}
			attr = new HashMap<String,Object>();
			attr.put("whereCode", getJspName(tableName));
			attr.put("whereName", modelName);
			attr.put("code", (String) columnsrs.get("columnName"));
			attr.put("describe", columnComment);
			attrs.add(attr);
		}
		context.setService("whereexService");
		context.setMethod("insert");
		context.setRows(attrs);
		context = SoaManager.getInstance().invoke(context);
		return context;
	}
	

	private String getJspName(String tableName) {
		String[] names = tableName.toLowerCase().split("_");
		StringBuffer sb = new StringBuffer("");
		for (int i = 2; i < names.length; i++) {
			String name = names[i];
			if (i > 2) {
				name = name.toUpperCase();
				name = name.charAt(0)
						+ name.substring(1, name.length()).toLowerCase();
			}
			sb.append(name);
		}
		return sb.toString();
	}

	private String changeName(String param) {
		String[] names = param.toLowerCase().split("_");
		String reName = "";

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (i > 0) {
				name = name.toUpperCase();
			}
			reName += name.charAt(0)
					+ name.substring(1, name.length()).toLowerCase();
		}
		return reName;
	}

	private String getServiceName(String serviceName) {
		
		String[] names = serviceName.toLowerCase().split("_");
		StringBuffer sb = new StringBuffer("");
		if(names.length > 2){
			for (int i = 2; i < names.length; i++) {
				String name = names[i];
				if (i > 2) {
					name = name.toUpperCase();
					name = name.charAt(0)
							+ name.substring(1, name.length()).toLowerCase();
				}
				sb.append(name);
			}
		}else if(names.length == 2){
			String name = names[1];
			name = name.toUpperCase();
			name = name.charAt(0)
					+ name.substring(1, name.length()).toLowerCase();
			sb.append(name);
		}
		serviceName = sb.toString();
		serviceName = serviceName.substring(0, 1).toUpperCase()
				+ serviceName.substring(1, serviceName.length());
		return serviceName;
	}

	private String getNameSpace(String serviceName,String modelName) {
		String[] names = serviceName.split("_");
		StringBuffer sb = new StringBuffer("");
		sb.append(modelName.toUpperCase());
		if(names.length > 2){
			for (int i = 2; i < names.length; i++) {
				String name = names[i];
				sb.append(name.toUpperCase());
			}
		}else if(names.length == 2){
			sb.append(names[1].toUpperCase());
		}
		return sb.toString();
	}

	private void createJsp(Map<String, Object> map, OutputStream toClient) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(toClient,"iso8859-1");
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath
							+ "/template/oracle/oraclejsp.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate,"iso8859-1"), new Configuration());
			tempState.process(map, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createJs(Map<String, Object> map, OutputStream toClient) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(toClient,"iso8859-1");
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath
							+ "/template/oracle/oraclejs.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate,"iso8859-1"), new Configuration());
			tempState.process(map, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void createSqlMap(Map<String, Object> map, OutputStream toClient) {
		Writer out = null;
		try{
			out = new OutputStreamWriter(toClient,"iso8859-1");
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader.getResourceAsStream(packagePath
					+ "/template/oracle/oracle.ftl");
			Template tempState = new Template("",
					new InputStreamReader(inputstate,"iso8859-1"), new Configuration());
			tempState.process(map, out);
			out.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createJspForm(Map<String, Object> map, OutputStream toClient) {
		Writer out = null;
		try {
			 out = new OutputStreamWriter(toClient,"iso8859-1");
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath
							+ "/template/oracle/oraclejspForm.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate,"iso8859-1"), new Configuration());
			tempState.process(map, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createService(Map<String, Object> map, OutputStream toClient) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(toClient,"iso8859-1");
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath
							+ "/template/oracle/oracleService.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate,"iso8859-1"), new Configuration());
			tempState.process(map, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getTableMap(HttpServletResponse response, HttpServletRequest request,String fileName,String tableName,String prix){
		
		RainbowSession.web2Service(request);
		String json = request.getParameter(ROWS);
		String modelName = request.getParameter(MODELNAME).toLowerCase();
		JsonUtil jsonUtil = JsonUtil.getInstance();
		List rowsList = jsonUtil.json2Object(json, List.class);
		Map<String, Object> tableMap = new HashMap<String, Object>();
		try {
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String((fileName+"."+prix).getBytes("UTF-8"), "ISO8859-1"));
			List<Map<String, Object>> columon = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> pkcolumon = new ArrayList<Map<String, Object>>();
			Map<String, Object> columnMap = null;
			Map<String, Object> columnsrs = null;
			Map<String, Object> pkcolumnMap = null;
			for (int i = 0; i < rowsList.size(); i++) {
				columnsrs = jsonUtil.json2Object((rowsList.get(i)).toString(), Map.class);
				columnMap = new HashMap<String, Object>();
				String column = (String) columnsrs.get("columnName");
				if (!column.equals("guid")) {
					String propertyName = changeName((String) columnsrs
							.get("columnName"));
					columnMap.put("columnName", column);
					String dataType = (String) columnsrs.get("dataType");
					columnMap.put("jdbcType", (String)jdbcType.get(dataType));
					if (dataType.indexOf("(") != -1) {
						columnMap.put("dataType",
								dataType.substring(0, dataType.indexOf("(")));
					} else {
						columnMap.put("dataType", dataType);
					}
					String columnComment = (String) columnsrs
							.get("columnComment");
					if (columnComment.indexOf("(") != -1) {
						columnMap.put(
								"columnComment",
								columnComment.substring(0,
										columnComment.indexOf("(")));
					} else {
						columnMap.put("columnComment", columnComment);
					}
					columnMap.put("propertyName", propertyName);
					String columnKey = (String) columnsrs.get("columnKey");
					if (columnKey != null && columnKey.equals("PRI")) {
						pkcolumnMap = new HashMap<String, Object>();
						pkcolumnMap.put("pk", column);
						pkcolumnMap.put("propertyName", propertyName);
						pkcolumon.add(pkcolumnMap);
					}
					columon.add(columnMap);
				}
				tableMap.put("tableName", tableName);
				tableMap.put("columns", columon);
				tableMap.put("jspName", getJspName(tableName));
				tableMap.put("pks", pkcolumon);
				tableMap.put("modelName", modelName);
				tableMap.put("service", getServiceName(tableName));
				tableMap.put("namespace", getNameSpace(tableName,modelName));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return tableMap;
	}
	
	private static void initJdbcType(){
		jdbcType.put("NUMBER", "NUMERIC,typeHandler=NumberHandler");
		jdbcType.put("VARCHAR2", "NVARCHAR");
		jdbcType.put("DATE", "DATE,typeHandler=DateHandler");
		jdbcType.put("CHAR", "CHAR");
		jdbcType.put("VARCHAR", "VARCHAR");
		jdbcType.put("TIMESTAMP(6)", "TIMESTAMP,typeHandler=DateHandler");
		jdbcType.put("NVARCHAR2", "NVARCHAR");
		jdbcType.put("CLOB", "CLOB");
	}
}