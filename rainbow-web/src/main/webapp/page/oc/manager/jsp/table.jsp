<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_table_datagrid" title="表信息维护" class="easyui-treegrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/queryTree.do?service=tableService&method=queryTree',idField:'guid',treeField:'name',textField:'name',parentField:'parentName',
				fit:true,selectOnCheck:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar :'#oc_table_toolbar',frozenColumns:[[
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_table_caozuo,frozen:true,title:'操作'}
			    ]]">
		<thead>
				<tr>
					<th data-options="field:'name',width:150,align:'left',sortable:true">表名称</th>
					<th data-options="field:'type',width:100,align:'left',sortable:true,formatter:oc_table_typeFormatter">表类型</th>
					<th data-options="field:'datanode',width:100,align:'left',sortable:true">分片节点</th>
					<th data-options="field:'rule',width:100,align:'left',sortable:true">分片规则</th>
					<th data-options="field:'parentName',width:100,align:'left',sortable:true,hidden:true">父表</th>
					<th data-options="field:'joinkey',width:100,align:'left',sortable:true">关联KEY</th>
					<th data-options="field:'parentkey',width:100,align:'left',sortable:true">父级KEY</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="oc_table_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_table_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_table_redo();" class="easyui-linkbutton" data-options="iconCls:'icon-redo',plain:true" style="float: left;">展开</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_table_undo();" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true" style="float: left;">折叠</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_table_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_table_mm" style="width:90px" >
		<div data-options="name:'name'">表名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_table_query,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_table_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/table.js"></script>