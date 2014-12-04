<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_role_datagrid" title="查询结果" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roleService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_role_toolbar',frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_role_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'roleCode',width:150,align:'left',sortable:true">角色代码</th>
				<th data-options="field:'roleName',width:150,align:'left',sortable:true,editor:'text'">角色名称</th>
				<th data-options="field:'roleTypeCode',width:100,align:'left',sortable:true">角色类型代码</th>
				<th data-options="field:'orgCode',width:100,align:'left',sortable:true">机构代码</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>

<div id="sys_role_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_role_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">新增角色</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_role_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_role_update();" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" style="float: left;">修改角色名称</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_role_save();" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" style="float: left;">保存</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_role_cancel();" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true" style="float: left;">取消</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_role_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_role_mm" style="width:90px" >
		<div data-options="name:'roleCode'">角色代码</div>
		<div data-options="name:'roleName'">角色名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_role_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_role_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/role.js"></script>