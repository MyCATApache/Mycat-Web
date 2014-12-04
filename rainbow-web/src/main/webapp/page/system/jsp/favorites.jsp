<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 95px;overflow: hidden;" align="center">
			<form id="system_favorites_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
						<th style="width: 90px;">guid</th>
						<td><input name="guid" style="width: 200px;" /></td>
						<th style="width: 90px;">收藏人</th>
						<td><input name="loginId" style="width: 200px;" /></td>
						<th style="width: 90px;">收藏的页面代码</th>
						<td><input name="pageCode" style="width: 200px;" /></td>
						<th style="width: 90px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="system_favorites_query();">过滤条件</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="system_favorites_queryClear();">清空条件</a>
						</td>
					</tr>
					
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="system_favorites_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=favoritesService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#system_favorites_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:system_favorites_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'guid',width:100,align:'left',sortable:true">guid</th>
					<th data-options="field:'loginId',width:100,align:'left',sortable:true">收藏人</th>
					<th data-options="field:'pageCode',width:100,align:'left',sortable:true">收藏的页面代码</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">收藏时间</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="system_favorites_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="system_favorites_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="system_favorites_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="system_favorites_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="system_favorites_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="system_favorites_mm" style="width:90px" >
		<div data-options="name:'code'">代码</div>
		<div data-options="name:'name'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:system_favorites_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#system_favorites_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/favorites.js"></script>