<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_menu_datagrid" title="菜单信息维护" class="easyui-treegrid" 
					data-options="url:'${pageContext.request.contextPath}/dispatcherAction/queryTree.do?service=menuService&method=queryTree',idField:'code',treeField:'name',textField:'name',parentField:'parentCode',
							fit:true,selectOnCheck:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar :'#sys_menu_toolbar',frozenColumns:[[
						        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_menu_caozuo,frozen:true,title:'操作'}
						    ]]">
	<thead>
			<tr>
				<th data-options="field:'guid',width:80,hidden:true">guid</th>
				<th data-options="field:'name',width:200,align:'left',sortable:true">菜单名称</th>
				<th data-options="field:'code',width:180,align:'left',sortable:true">菜单代码</th>
				<th data-options="field:'parentCode',width:100,sortable:true">上级代码</th>
				<th data-options="field:'pageCode',width:120,align:'left',sortable:true">页面代码</th>
				<th data-options="field:'sortIndex',width:60,align:'center',sortable:true">排序位置</th>
				<th data-options="field:'createUser',width:100,align:'center'">创建人</th>
				<th data-options="field:'createTime',width:100,align:'center',sortable:true">创建时间</th>
			</tr>
	</thead>
</table>

<div id="sys_menu_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_menu_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_menu_redo();" class="easyui-linkbutton" data-options="iconCls:'icon-redo',plain:true" style="float: left;">展开</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_menu_undo();" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true" style="float: left;">折叠</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_menu_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_menu_mm" style="width:90px" >
		<div data-options="name:'code'">菜单代码</div>
		<div data-options="name:'name'">菜单名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_menu_query,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_menu_mm'" style="width:300px;" />
</div>

<script type="text/javascript" src="./page/system/js/menu.js"></script>