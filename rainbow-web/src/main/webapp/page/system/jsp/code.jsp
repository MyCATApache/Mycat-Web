<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_code_datagrid" title="系统代码维护" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=codeService&method=query',pagination:true,striped:true,idField:'guid',
					pageSize:20,pageList:[20,50,100,150,200],fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_code_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_code_caozuo,frozen:true,title:'操作'}
				    ]]">
	<thead>
			<tr>
				<th data-options="field:'value',width:100,align:'left',sortable:true">值</th>
				<th data-options="field:'code',width:100,align:'left',sortable:true">代码</th>
				<th data-options="field:'text',width:150,align:'left',sortable:true">名称</th>
				<th data-options="field:'parent',width:100,align:'left',sortable:true">上级代码</th>
				<th data-options="field:'remark',width:150,align:'left',sortable:true">备注</th>
				<th data-options="field:'createtime',width:80,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createuser',width:80,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>


<div id="sys_code_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_code_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_code_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_code_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_code_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_code_mm" style="width:90px" >
		<div data-options="name:'code'">代码</div>
		<div data-options="name:'text'">名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_code_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_code_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/code.js"></script>