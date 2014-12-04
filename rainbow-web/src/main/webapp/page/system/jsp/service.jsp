<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_service_datagrid" title="按钮信息维护" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=serverService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,pageSize:20,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_service_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_service_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'serviceName',width:100,align:'left',sortable:true">服务描述</th>
				<th data-options="field:'serviceCode',width:100,align:'left',sortable:true">服务名</th>
				<th data-options="field:'methodName',width:100,align:'left',sortable:true">方法描述</th>
				<th data-options="field:'methodCode',width:100,align:'left',sortable:true">方法名</th>
				<th data-options="field:'package',width:200,align:'left',sortable:true">所属包</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>


<div id="sys_service_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_service_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_service_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_service_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_service_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_service_mm" style="width:90px" >
		<div data-options="name:'serviceCode'">服务代码</div>
		<div data-options="name:'serviceName'">服务名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_service_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_service_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/service.js"></script>