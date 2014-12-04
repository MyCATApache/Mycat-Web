<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 95px;overflow: hidden;" align="center">
			<form id="sys_setup_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
						<th style="width: 90px;">guid</th>
						<td><input name="guid" style="width: 200px;" /></td>
						<th style="width: 90px;">功能名称</th>
						<td><input name="funName" style="width: 200px;" /></td>
						<th style="width: 90px;">功能代码</th>
						<td><input name="funCode" style="width: 200px;" /></td>
					</tr>
					<tr>
						<th style="width: 90px;">功能描述</th>
						<td><input name="funDesc" style="width: 200px;" /></td>
						<th style="width: 90px;">备注</th>
						<td><input name="remark" style="width: 200px;" /></td>
						<th style="width: 90px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="sys_setup_query();">过滤条件</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="sys_setup_queryClear();">清空条件</a>
						</td>
					</tr>
					
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="sys_setup_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=setupService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_setup_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_setup_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'funName',width:250,align:'left',sortable:true">功能名称</th>
					<th data-options="field:'funCode',width:100,align:'left',sortable:true">功能代码</th>
					<th data-options="field:'isDisplay',width:80,align:'left',sortable:true,formatter:sys_setup_isDisplayFormatter">是否可见</th>
					<th data-options="field:'funDesc',width:250,align:'left',sortable:true">功能描述</th>
					<th data-options="field:'remark',width:100,align:'left',sortable:true">备注</th>
					<th data-options="field:'createUser',width:80,align:'left',sortable:true">创建人</th>
					<th data-options="field:'createTime',width:150,align:'left',sortable:true">创建时间</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_setup_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_setup_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_setup_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_setup_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_setup_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_setup_mm" style="width:90px" >
		<div data-options="name:'funCode'">代码</div>
		<div data-options="name:'funName'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_setup_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_setup_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/setup.js"></script>