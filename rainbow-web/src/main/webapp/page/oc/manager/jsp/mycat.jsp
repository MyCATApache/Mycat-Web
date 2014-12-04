<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="oc_mycat_datagrid" title="系统参数列表" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=mycatService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#oc_mycat_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_mycat_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'paramName',width:100,align:'left',sortable:true">属性名称</th>
				<th data-options="field:'paramValue',width:100,align:'left',sortable:true">属性值</th>
				<th data-options="field:'ramark',width:100,align:'left',sortable:true">功能描述</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>

<div id="oc_mycat_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_mycat_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_mycat_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_mycat_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_mycat_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<a href="./myCatAction/createMyCat.do" class="easyui-linkbutton" data-options="iconCls:'icon-download',plain:true" style="float: left;">导出server.xml</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_mycat_mm" style="width:90px" >
		<div data-options="name:'paramName'">属性名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_mycat_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_mycat_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/mycat.js"></script>