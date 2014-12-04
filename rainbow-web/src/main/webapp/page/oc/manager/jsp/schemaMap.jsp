<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="oc_schemaMap_datagrid" title="查询结果" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=schemaMapService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#oc_schemaMap_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_schemaMap_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'schemaName',width:100,align:'left',sortable:true">逻辑库名</th>
				<th data-options="field:'tableName',width:100,align:'left',sortable:true">表名</th>
			</tr>
	</thead>
</table>

<div id="oc_schemaMap_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_schemaMap_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_schemaMap_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_schemaMap_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_schemaMap_mm" style="width:90px" >
		<div data-options="name:'schemaName'">逻辑库名</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_schemaMap_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_schemaMap_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/schemaMap.js"></script>