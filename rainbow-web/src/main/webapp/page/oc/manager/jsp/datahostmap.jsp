<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="oc_datahostmap_datagrid" title="物理机列表" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=datahostmapService&method=queryByPage&datahost=${param.datahost}',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar:'#oc_datahostmap_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_datahostmap_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'datahost',width:100,align:'left',sortable:true">物理节点名称</th>
				<th data-options="field:'host',width:200,align:'left',sortable:true">物理机</th>
				<th data-options="field:'url',width:100,align:'left',sortable:true">数据库连接</th>
				<th data-options="field:'dbType',width:100,align:'left',sortable:true">数据库类型</th>
				<th data-options="field:'dbDriver',width:100,align:'left',sortable:true">数据库驱动</th>
			</tr>
	</thead>
</table>


<div id="oc_datahostmap_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_datahostmap_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datahostmap_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datahostmap_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_datahostmap_mm" style="width:90px" >
		<div data-options="name:'datahost'">物理节点名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_datahostmap_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_datahostmap_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/datahostmap.js"></script>