<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_modatanode_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=monitorService&method=queryDatanode',pagination:true,striped:true,idField:'processor',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'NAME',width:100,align:'left',sortable:true">name</th>
					<th data-options="field:'DATHOST',width:100,align:'left',sortable:true">dathost</th>
					<th data-options="field:'INDEX',width:100,align:'left',sortable:true">index</th>
					<th data-options="field:'TYPE',width:100,align:'left',sortable:true">type</th>
					<th data-options="field:'ACTIVE',width:100,align:'left',sortable:true">active</th>
					<th data-options="field:'IDLE',width:100,align:'left',sortable:true">idle</th>
					<th data-options="field:'SIZE',width:100,align:'left',sortable:true">size</th>
					<th data-options="field:'EXECUTE',width:100,align:'left',sortable:true">execute</th>
					<th data-options="field:'TOTAL_TIME',width:100,align:'left',sortable:true">total_time</th>
					<th data-options="field:'MAX_TIME',width:100,align:'left',sortable:true">max_time</th>
					<th data-options="field:'MAX_SQL',width:100,align:'left',sortable:true">max_sql</th>
					<th data-options="field:'RECOVERY_TIME',width:100,align:'left',sortable:true">recovery_time</th>
				</tr>
		</thead>
	</table>
	</div>
</div>


<script type="text/javascript" src="./page/oc/monitor/js/datanode.js"></script>