<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 60px;overflow: hidden;" align="center">
			<form id="sys_roletype_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
						<th style="width: 90px;">角色类型代码</th>
						<td><input name="roleTypeCode" style="width: 200px;" /></td>
						<th style="width: 90px;">角色类型名称</th>
						<td><input name="roleTypeName" style="width: 200px;" /></td>
						<th style="width: 90px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="sys_roletype_query();">条件过滤</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="sys_roletype_queryClear();">清空条件</a></td>
					</tr>
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="sys_roletype_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roletypeService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_roletype_toolbar',frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_roletype_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'roleTypeCode',width:100,align:'left',sortable:true">角色类型代码</th>
					<th data-options="field:'roleTypeName',width:100,align:'left',sortable:true">角色类型名称</th>
					<th data-options="field:'roleTypeGroup',width:100,align:'left',sortable:true,formatter:sys_roletype_groupFormatter">角色类型组</th>
					<th data-options="field:'status',width:100,align:'left',sortable:true,formatter:sys_roletype_status">状态</th>
					<th data-options="field:'sortIndex',width:100,align:'left',sortable:true">排序索引</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
					<th data-options="field:'remark',width:100,align:'left',sortable:true">备注</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_roletype_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_roletype_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_roletype_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_roletype_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_roletype_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_roletype_mm" style="width:90px" >
		<div data-options="name:'roleTypeCode'">代码</div>
		<div data-options="name:'roleTypeName'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_roletype_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_roletype_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/roletype.js"></script>