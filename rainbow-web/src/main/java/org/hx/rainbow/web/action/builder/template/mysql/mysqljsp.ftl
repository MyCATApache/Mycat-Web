<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 95px;overflow: hidden;" align="center">
			<form id="${jspName}_queryForm" >
				<table class="tableForm" style="width: 100%">
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
					</tr>
				</table>
			</form>
			<a href="javascript:void(0);" class="easyui-linkbutton"
			data-options="iconCls:'icon-search',plain:true"
			onclick="${jspName}_query();">过滤条件</a> <a
			href="javascript:void(0);" class="easyui-linkbutton"
			data-options="iconCls:'icon-cancel',plain:true"
			onclick="${jspName}_query_clear();">清空条件</a>
	</div>
	<div data-options="region:'center',border:false">
	<table id="${jspName}_datagrid" title="查询结果" class="easyui-datagrid" style="height:320px"
			data-options="url:'$${r"{"}pageContext.request.contextPath${r"}"}/dispatcherAction/query.do?service=${jspName}Service&method=query',pagination:true,striped:true,idField:'guid',
					remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : [ {
						text : '增加',
						iconCls : 'icon-add',
						handler : function() {
							add_${jspName}();
						}
					},'-', {
						text : '复制新增',
						iconCls : 'icon-add',
						handler : function() {
							copy_add_${jspName}();
						}
					}, '-', {
						text : '批量删除',
						iconCls : 'icon-remove',
						handler : function() {
							batch_delete_${jspName}();
						}
					},'-', {
						text : '刷新',
						iconCls : 'icon-reload',
						handler : function() {
							$('#${jspName}_datagrid').datagrid('reload');
						}
					} ],frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:caozuo,frozen:true,title:'操作'}
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
	
<script type="text/javascript" src="./page/${modelName}/js/${jspName}.js"></script>