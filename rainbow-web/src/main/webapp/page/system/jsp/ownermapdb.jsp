<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="sys_ownermapdb_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=ownermapdbService&method=queryByPage',pagination:true,striped:true,idField:'goodsownerid',
					pageSize:20,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_ownermapdb_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_ownermapdb_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'gomacode',width:100,align:'left',sortable:true">货主代码</th>
					<th data-options="field:'goodsownername',width:200,align:'left',sortable:true">货主名称</th>
					<th data-options="field:'dbName',width:100,align:'left',sortable:true">数据源名称</th>
					<th data-options="field:'goodsownerid',width:100,align:'left',sortable:true">货主ID</th>
					<th data-options="field:'createdate',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createname',width:100,align:'left',sortable:true">创建人</th>
					<th data-options="field:'status',width:100,align:'left',sortable:true">状态</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_ownermapdb_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_ownermapdb_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_ownermapdb_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_ownermapdb_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_ownermapdb_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_ownermapdb_mm" style="width:90px" >
		<div data-options="name:'gomacode'">货主代码</div>
		<div data-options="name:'goodsownername'">货主名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_ownermapdb_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_ownermapdb_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/ownermapdb.js"></script>