<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_org_treegrid" title="组织机构信息维护" class="easyui-treegrid" 
						data-options="url:'${pageContext.request.contextPath}/dispatcherAction/queryTree.do?service=orgService&method=queryGridTree',striped:true,idField:'orgCode',treeField:'orgName',textField:'orgName',parentField:'parentCode',
								fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar :'#sys_org_toolbar',
								frozenColumns:[[
							        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_org_caozuo,frozen:true,title:'操作'}
							    ]]">
	<thead>
		<tr>
			<th data-options="field:'orgName',width:200,align:'left',sortable:true">机构名称</th>
			<th data-options="field:'orgCode',width:100,align:'left',sortable:true">机构代码</th>
			<th data-options="field:'parentCode',width:100,align:'left',sortable:true">上级机构代码</th>
			<th data-options="field:'aliveFlag',width:100,align:'left',sortable:true,formatter:orgAliveFormatter">是否有效</th>
			<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
		</tr>
	</thead>
 </table>

<div id="sys_org_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_org_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_org_redo();" class="easyui-linkbutton" data-options="iconCls:'icon-redo',plain:true" style="float: left;">展开</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_org_undo();" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true" style="float: left;">折叠</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_org_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_org_mm" style="width:90px" >
		<div data-options="name:'orgCode'">机构代码</div>
		<div data-options="name:'orgName'">机构名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_org_query,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_org_mm'" style="width:300px;" />
</div>
	
<script type="text/javascript" src="./page/system/js/org.js"></script>