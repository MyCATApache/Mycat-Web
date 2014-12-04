<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="rsa_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=rsaService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#rsa_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:80,align:'center',sortable:true,formatter:caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'code',width:60,align:'left',sortable:true">密钥代码</th>
					<th data-options="field:'name',width:60,align:'left',sortable:true">密钥名称</th>
					<th data-options="field:'publicKey',width:60,align:'left',sortable:true">公钥</th>
					<th data-options="field:'privateKey',width:200,align:'left',sortable:true">私钥</th>
					<th data-options="field:'modulus',width:200,align:'left',sortable:true">模数</th>
				</tr>
		</thead>
</table>

<div id="rsa_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="add_rsa()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="batch_delete_rsa();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="rsa_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="rsa_mm" style="width:90px" >
		<div data-options="name:'code'">密钥代码</div>
		<div data-options="name:'name'">密钥名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:rsa_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#rsa_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/sa/js/rsa.js"></script>