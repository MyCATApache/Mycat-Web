<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_threadpool_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=monitorService&method=queryThreadpool',pagination:true,striped:true,idField:'processor',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'NAME',width:100,align:'left',sortable:true">name</th>
					<th data-options="field:'POOL_SIZE',width:100,align:'left',sortable:true">pool_size</th>
					<th data-options="field:'ACTIVE_COUNT',width:100,align:'left',sortable:true">active_count</th>
					<th data-options="field:'TASK_QUEUE_SIZE',width:100,align:'left',sortable:true">task_queue_size</th>
					<th data-options="field:'COMPLETED_TASK',width:100,align:'left',sortable:true">completed_task</th>
					<th data-options="field:'TOTAL_TASK',width:100,align:'left',sortable:true">total_task</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<script type="text/javascript" src="./page/oc/monitor/js/threadpool.js"></script>