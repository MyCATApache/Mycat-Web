<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="customer_datagrid" title="查询结果" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=customerService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#customer_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:80,align:'center',sortable:true,formatter:caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'code',width:100,align:'left',sortable:true">客户编号</th>
				<th data-options="field:'name',width:200,align:'left',sortable:true">客户名称</th>
				<th data-options="field:'linkman',width:100,align:'left',sortable:true">联系人</th>
				<th data-options="field:'telephony',width:100,align:'left',sortable:true">联系电话</th>
				<th data-options="field:'eMail',width:200,align:'left',sortable:true">邮箱地址</th>
			</tr>
	</thead>
</table> 

<div id="customer_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="add_customer()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="copy_add_customer();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="batch_delete_customer();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="customer_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="customer_mm" style="width:90px" >
		<div data-options="name:'code'">代码</div>
		<div data-options="name:'name'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:customer_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#customer_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/sa/js/customer.js"></script>