<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 95px;overflow: hidden;" align="center">
			<form id="${modelName}_${jspName}_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
						<#list columns as property>
				<#if property.propertyName != "createTime" && property.propertyName != "createUser">
						<#if property_index != 0 && (property_index )% 3 == 0>
					</tr>
					<tr>
						</#if>
						<th style="width: 90px;">${property.columnComment}</th>
						<td><input name="${property.propertyName}" style="width: 200px;" /></td>
				</#if>
						</#list>
						<th style="width: 90px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="${modelName}_${jspName}_query();">过滤条件</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="${modelName}_${jspName}_queryClear();">清空条件</a>
						</td>
					</tr>
					
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="${modelName}_${jspName}_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'$${r"{"}pageContext.request.contextPath${r"}"}/dispatcherAction/query.do?service=${jspName}Service&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#${modelName}_${jspName}_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:${modelName}_${jspName}_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
				<#list columns as property>
					<th data-options="field:'${property.propertyName}',width:100,align:'left',sortable:true">${property.columnComment}</th>
				</#list>	
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="${modelName}_${jspName}_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="${modelName}_${jspName}_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="${modelName}_${jspName}_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="${modelName}_${jspName}_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="${modelName}_${jspName}_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="${modelName}_${jspName}_mm" style="width:90px" >
		<div data-options="name:'code'">代码</div>
		<div data-options="name:'name'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:${modelName}_${jspName}_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#${modelName}_${jspName}_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/${modelName}/js/${jspName}.js"></script>