<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 95px;overflow: hidden;" align="center">
			<form id="sys_whereex_queryForm" >
				<table class="queryForm" style="width: 100%">
					<tr>
						<th style="width: 60px;">条件名称</th>
						<td><input name="whereName" style="width: 230PX;" /></td>
						<th style="width: 60px;">字段名称</th>
						<td><input name="code" style="width: 230PX;" /></td>
						<th style="width: 80px;">字段中文描述</th>
						<td><input name="describe" style="width: 230PX;" /></td>
					</tr>
					<tr>
						<th style="width: 60px;"></th>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<th style="width: 60px;"></th>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<th style="width: 60px;"></th>
						<td><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="sys_whereex_query();">过滤条件</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="sys_whereex_queryClear();">清空条件</a>
						</td>
					</tr>
					
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="sys_whereex_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=whereexService&method=queryByPage',pagination:true,pageSize:20,striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:false,autoRowHeight:false,toolbar : '#sys_whereex_toolbar',
					onUnselect:sys_whereex_endEdite,frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'},
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_whereex_caozuo,frozen:true,title:'操作'}
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'whereCode',width:120,align:'left',sortable:true">条件代码</th>
					<th data-options="field:'whereName',width:120,align:'left',sortable:true">条件名称</th>
					<th data-options="field:'code',width:150,align:'left',sortable:true">字段名称</th>
					<th data-options="field:'describe',width:150,align:'left',sortable:true">字段中文描述</th>
					<th data-options="field:'sortIndex',width:150,align:'left',sortable:true,editor:'numberbox'">排序位置</th>
					<th data-options="field:'valueType',width:100,align:'left',sortable:true">值类型</th>
					<th data-options="field:'valueCode',width:100,align:'left',sortable:true">值代码</th>
					<th data-options="field:'valueService',width:100,align:'left',sortable:true">取值服务</th>
					<th data-options="field:'valueMethod',width:100,align:'left',sortable:true">取值方法</th>
					<th data-options="field:'bizComboxId',width:100,align:'left',sortable:true">数据表格值域</th>
					<th data-options="field:'bizComboxText',width:100,align:'left',sortable:true">数据表格文本</th>
					<th data-options="field:'bizColumns',width:200,align:'left',sortable:true">业务字段</th>
					<th data-options="field:'remark',width:100,align:'left',sortable:true">备注</th>
					<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
					<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="sys_whereex_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_whereex_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_whereex_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_whereex_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_whereex_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_whereex_update()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">修改排序</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_whereex_save()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">保存</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_whereex_mm" style="width:90px" >
		<div data-options="name:'whereCode'">条件代码</div>
		<div data-options="name:'whereName'">条件名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_whereex_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_whereex_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/system/js/whereex.js"></script>