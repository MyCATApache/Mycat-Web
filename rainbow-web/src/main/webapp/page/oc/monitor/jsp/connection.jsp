<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_connection_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=monitorService&method=queryConnection',pagination:true,striped:true,idField:'processor',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'PROCESSOR',width:120,align:'left',sortable:true">processor</th>
					<th data-options="field:'ID',width:100,align:'left',sortable:true">id</th>
					<th data-options="field:'HOST',width:100,align:'left',sortable:true">host</th>
					<th data-options="field:'PORT',width:100,align:'left',sortable:true">port</th>
					<th data-options="field:'LOCAL_PORT',width:100,align:'left',sortable:true">local_port</th>
					<th data-options="field:'SCHEMA',width:100,align:'left',sortable:true">schema</th>
					<th data-options="field:'CHARSET',width:100,align:'left',sortable:true">charset</th>
					<th data-options="field:'NET_OUT',width:100,align:'left',sortable:true">net_out</th>
					<th data-options="field:'NET_IN',width:100,align:'left',sortable:true">net_in</th>
					<th data-options="field:'ALIVE_TIME',width:120,align:'left',sortable:true">alive_time(s)</th>
					<th data-options="field:'WRITE_ATTEMPTS',width:120,align:'left',sortable:true">write_attempts</th>
					<th data-options="field:'RECV_BUFFER',width:100,align:'left',sortable:true">recv_buffer</th>
					<th data-options="field:'SEND_QUEUE',width:100,align:'left',sortable:true">send_queue</th>
					<th data-options="field:'CHANNELS',width:100,align:'left',sortable:true">channels</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="oc_connection_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="oc_connection_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_connection_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_connection_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="oc_connection_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="oc_connection_mm" style="width:90px" >
		<div data-options="name:'name'">物理节点</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:oc_connection_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#oc_connection_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/oc/monitor/js/connection.js"></script>