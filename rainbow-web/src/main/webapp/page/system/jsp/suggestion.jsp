<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 65px;overflow: hidden;" align="center">
			<form id="sys_suggestion_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
					
						<th style="width: 90px;">意见标题</th>
						<td><input name="title" style="width: 200px;" /></td>
						<th style="width: 90px;">意见类型</th>
						<td><input name="type" style="width: 200px;" /></td>
						<th style="width: 90px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="sys_suggestion_query();">过滤条件</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="sys_suggestion_queryClear();">清空条件</a>
						</td>
					</tr>
					
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="sys_suggestion_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=suggestionService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_suggestion_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_suggestion_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'title',width:100,align:'left',sortable:true">意见标题</th>
					<th data-options="field:'type',width:100,align:'left',sortable:true,formatter:sys_suggestion_typeFormatter">意见类型</th>
					<th data-options="field:'sugLevel',width:100,align:'left',sortable:true,formatter:sys_suggestion_sugLevelFormatter">级别</th>
					<th data-options="field:'sugStatus',width:100,align:'left',sortable:true,formatter:sys_suggestion_sugStatusFormatter">状态</th>
					<th data-options="field:'funModules',width:100,align:'left',sortable:true">意见涉及模块</th>
					<th data-options="field:'suggestion',width:100,align:'left',sortable:true">意见内容</th>
					<th data-options="field:'reply',width:100,align:'left',sortable:true">回复</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">提出时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">提出人</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_suggestion_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_suggestion_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_suggestion_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_suggestion_mm" style="width:90px" >
		<div data-options="name:'title'">标题</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_suggestion_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_suggestion_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/suggestion.js"></script>