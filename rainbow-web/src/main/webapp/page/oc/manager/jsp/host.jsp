<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="oc_host_datagrid" title="物理机信息维护" class="easyui-treegrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/queryTree.do?service=hostService&method=queryTree',idField:'guid',treeField:'host',textField:'host',parentField:'parentHost',
				fit:true,selectOnCheck:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar :'#oc_host_toolbar',frozenColumns:[[
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_host_caozuo,frozen:true,title:'操作'}
			    ]]">
		<thead>
				<tr>
				    <th data-options="field:'guid',width:80,hidden:true">guid</th>
					<th data-options="field:'host',width:100,align:'left',sortable:true">物理节点名称</th>
					<th data-options="field:'url',width:200,align:'left',sortable:true">链接地址</th>
					<th data-options="field:'state',width:100,align:'left',sortable:true,formatter:oc_host_stateFormatter">服务类型</th>
					<th data-options="field:'dUser',width:100,align:'left',sortable:true">用户</th>
					<th data-options="field:'password',width:100,align:'left',sortable:true">密码</th>
					<th data-options="field:'parentHost',width:100,align:'left',sortable:true,hidden:true">父节点</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				</tr>
		</thead>
</table>

<div id="oc_host_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_host_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_host_redo();" class="easyui-linkbutton" data-options="iconCls:'icon-redo',plain:true" style="float: left;">展开</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_host_undo();" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true" style="float: left;">折叠</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_host_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_host_mm" style="width:90px" >
		<div data-options="name:'host'">物理节点名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_host_query,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_host_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/host.js"></script>