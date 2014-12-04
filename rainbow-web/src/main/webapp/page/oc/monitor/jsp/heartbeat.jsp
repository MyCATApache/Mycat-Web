<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_heartbeat_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=monitorService&method=queryHeartbeat',pagination:true,striped:true,idField:'processor',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'NAME',width:100,align:'left',sortable:true">name</th>
					<th data-options="field:'TYPE',width:100,align:'left',sortable:true">type</th>
					<th data-options="field:'HOST',width:100,align:'left',sortable:true">host</th>
					<th data-options="field:'PORT',width:100,align:'left',sortable:true">port</th>
					<th data-options="field:'RS_CODE',width:100,align:'left',sortable:true">rs_code</th>
					<th data-options="field:'RETRY',width:100,align:'left',sortable:true">retry</th>
					<th data-options="field:'STATUS',width:100,align:'left',sortable:true">status</th>
					<th data-options="field:'TIMEOUT',width:100,align:'left',sortable:true">timeout</th>
					<th data-options="field:'EXECUTE_TIME',width:100,align:'left',sortable:true">execute_time</th>
					<th data-options="field:'LAST_ACTIVE_TIME',width:120,align:'left',sortable:true">last_active_time</th>
					<th data-options="field:'STOP',width:100,align:'left',sortable:true">stop</th>
				</tr>
		</thead>
	</table>
	</div>
</div>


<script type="text/javascript" src="./page/oc/monitor/js/heartbeat.js"></script>