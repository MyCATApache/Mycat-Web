<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_page_datagrid" title="页面信息维护" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=pageService&method=query',pagination:true,striped:true,idField:'guid',
				pageSize:20,pageList:[20,50,100,150,200],fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar :'#sys_page_toolbar'">
	<thead>
			<tr>
				<th data-options="field:'guid',width:80,checkbox:true">guid</th>
				<th data-options="field:'caozuo',width:40,align:'center',sortable:true,formatter:sys_page_caozuo">操作</th>
				<th data-options="field:'name',width:100,align:'left',sortable:true">页面名称</th>
				<th data-options="field:'code',width:100,align:'left',sortable:true">页面代码</th>
				<th data-options="field:'url',width:150,sortable:true,editor:'text'">页面路径</th>
				<th data-options="field:'isCache',width:40,align:'left',sortable:true,formatter:sys_pager_isCacheFormatter">是否缓存</th>
				<th data-options="field:'createUser',width:100,align:'left'">创建人</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
			</tr>
	</thead>
</table>

<div id="sys_page_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_page_add();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_page_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_page_datchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_page_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_page_mm" style="width:90px" >
		<div data-options="name:'code'">页面代码</div>
		<div data-options="name:'name'">页面名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_page_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_page_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/page.js"></script>