<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_datanode_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=datanodeService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#oc_datanode_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_datanode_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'name',width:100,align:'left',sortable:true">分片节点</th>
					<th data-options="field:'datahost',width:100,align:'left',sortable:true">物理节点</th>
					<th data-options="field:'database',width:100,align:'left',sortable:true">物理库(数据库名)</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="oc_datanode_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_datanode_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datanode_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datanode_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datanode_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_datanode_mm" style="width:90px" >
		<div data-options="name:'name'">分片节点</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_datanode_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_datanode_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/datanode.js"></script>