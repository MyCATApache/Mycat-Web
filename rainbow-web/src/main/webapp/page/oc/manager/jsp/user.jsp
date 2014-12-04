<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="oc_user_datagrid" title="查询结果" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=ocUserService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#oc_user_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_user_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'name',width:100,align:'left',sortable:true">用户</th>
				<th data-options="field:'password',width:100,align:'left',sortable:true">密码</th>
				<th data-options="field:'appName',width:100,align:'left',sortable:true">应用名称</th>
				<th data-options="field:'appCode',width:100,align:'left',sortable:true">应用代码</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'schemas',width:100,align:'left',sortable:true">逻辑库</th>
			</tr>
	</thead>
</table>

<div id="oc_user_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_user_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_user_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_user_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_user_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_user_mm" style="width:90px" >
		<div data-options="name:'name'">用户</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_user_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_user_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/user.js"></script>