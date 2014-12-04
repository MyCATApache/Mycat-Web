<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'center',border:false">
	<table id="oc_backend_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=monitorService&method=queryBackend',pagination:true,striped:true,idField:'processor',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'processor',width:100,align:'left',sortable:true">processor</th>
					<th data-options="field:'id',width:100,align:'left',sortable:true">id</th>
					<th data-options="field:'host',width:100,align:'left',sortable:true">host</th>
					<th data-options="field:'port',width:100,align:'left',sortable:true">port</th>
					<th data-options="field:'l_port',width:100,align:'left',sortable:true">l_port</th>
					<th data-options="field:'net_in',width:100,align:'left',sortable:true">net_in</th>
					<th data-options="field:'net_out',width:100,align:'left',sortable:true">net_out</th>
					<th data-options="field:'life',width:100,align:'left',sortable:true">life</th>
					<th data-options="field:'closed',width:100,align:'left',sortable:true">closed</th>
					<th data-options="field:'auth',width:100,align:'left',sortable:true">auth</th>
					<th data-options="field:'quit',width:100,align:'left',sortable:true">quit</th>
					<th data-options="field:'checking',width:100,align:'left',sortable:true">checking</th>
					<th data-options="field:'stop',width:100,align:'left',sortable:true">stop</th>
					<th data-options="field:'status',width:100,align:'left',sortable:true">status</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<script type="text/javascript" src="./page/oc/monitor/js/backend.js"></script>