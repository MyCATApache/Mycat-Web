<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_datahost_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=datahostService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#oc_datahost_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:oc_datahost_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'name',width:100,align:'left',sortable:true">物理节点</th>
					<th data-options="field:'minCon',width:100,align:'left',sortable:true">最小链接数</th>
					<th data-options="field:'maxCon',width:100,align:'left',sortable:true">最大链接数</th>
					<th data-options="field:'balance',width:100,align:'left',sortable:true">均衡策略</th>
					<th data-options="field:'hearbeat',width:100,align:'left',sortable:true">心跳检查</th>
					<th data-options="field:'dbdriver',width:100,align:'left',sortable:true">链接驱动</th>
					<th data-options="field:'dbtype',width:100,align:'left',sortable:true">数据库类型</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="oc_datahost_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_datahost_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datahost_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datahost_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_datahost_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_datahost_mm" style="width:90px" >
		<div data-options="name:'name'">物理节点</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_datahost_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_datahost_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/manager/js/datahost.js"></script>