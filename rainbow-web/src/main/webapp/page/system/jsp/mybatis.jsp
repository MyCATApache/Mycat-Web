<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="sys_mybatis_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=mybatisService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,pageSize:20,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_mybatis_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_mybatis_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'nsCode',width:100,align:'left',sortable:true">NAMESPACE代码</th>
					<th data-options="field:'nsName',width:120,align:'left',sortable:true">NAMESPACE描述</th>
					<th data-options="field:'stCode',width:100,align:'left',sortable:true">STATEMENT代码</th>
					<th data-options="field:'stName',width:120,align:'left',sortable:true">STATEMENT描述</th>
					<th data-options="field:'createTime',width:80,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:80,align:'left',sortable:true">创建人</th>
					<th data-options="field:'remark',width:100,align:'left',sortable:true">备注</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_mybatis_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_mybatis_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_mybatis_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_mybatis_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_mybatis_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_mybatis_mm" style="width:90px" >
		<div data-options="name:'nsName'">NAMESPACE描述</div>
		<div data-options="name:'stName'">STATEMENT描述</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_mybatis_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_mybatis_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/mybatis.js"></script>