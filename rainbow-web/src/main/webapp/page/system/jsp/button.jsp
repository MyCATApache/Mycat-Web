<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_button_datagrid" title="按钮信息维护" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=buttonService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_button_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_button_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'buttonCode',width:100,align:'left',sortable:true">按钮代码</th>
				<th data-options="field:'buttonName',width:100,align:'left',sortable:true">按钮名称</th>
				<th data-options="field:'pageCode',width:100,align:'left',sortable:true">页面代码</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>


<div id="sys_button_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_button_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_button_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_button_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_button_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_button_mm" style="width:90px" >
		<div data-options="name:'pageCode'">页面代码</div>
		<div data-options="name:'buttonCode'">按钮代码</div>
		<div data-options="name:'buttonName'">按钮名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_button_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_button_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/button.js"></script>