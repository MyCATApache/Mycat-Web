<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'west',split:true,title:'逻辑库列表'"
			style="width: 250px;">
			<table id="oc_schemamap_schema_datagrid"  class="easyui-datagrid" 
					data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=schemaService&method=query',pagination:false,striped:true,idField:'guid',
							onClickRow:oc_schemamap_schema_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
				<thead>
						<tr>
							<th data-options="field:'name',width:100,align:'left',sortable:true">逻辑库名</th>
						</tr>
				</thead>
			</table>
		</div>
		<div data-options="region:'center',title:'添加表'">
			<div class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'north'" style="height: 40px;">
					<table class="queryForm" style="width: 100%">
						<tr>
							<th><font color="red">当前逻辑库名</font></th>
							<td><input id="oc_schemamap_schema_name" name="schemaName"
								readonly="readonly" type="text" title="请在选择右边逻辑库"
								style="width: 200px;" /></td>
						</tr>
					</table>
				</div>
				<div data-options="region:'center'" title="可添加的表">
					<table id="oc_schemamap_table_datagrid"  class="easyui-datagrid" 
							data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=tableService&method=queryForSchemaMap',pagination:false,striped:true,idField:'guid',
									fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar:'#oc_schemamap_schema_toolbar',
									frozenColumns:[[
								        {field:'guid',width:80,checkbox:true,title:'guid'},
								    ]]">
						<thead>
								<tr>
								<th data-options="field:'name',width:100,align:'left',sortable:true">表名称</th>
								<th data-options="field:'type',width:100,align:'left',sortable:true">表类型</th>
								<th data-options="field:'datanode',width:100,align:'left',sortable:true">分片节点</th>
								<th data-options="field:'rule',width:100,align:'left',sortable:true">分片规则</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
<div id="oc_schemamap_schema_toolbar" style="display: none;">
	<div id="schemapmm" style="width: 90px;">
		<div data-options="name:'name'">表名</div>
	</div>
	<input class="easyui-searchbox"
		data-options="searcher:oc_schemamap_table_search,prompt:'请输入值，输完，回车!',menu:'#schemapmm'"
		style="width: 300px;" />
</div>

<input type="hidden" id="sys_authuser_checkType" value="0">
<script type="text/javascript">	
	var oc_schemamap_table_search = function(value, name) {
		if (value != null && value != '') {
			var o = new Object();
			o[name] = value;
			$('#oc_schemamap_table_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
			o = null;
		} else {
			$('#oc_schemamap_table_datagrid').datagrid('load', {});
		}
	};
	var oc_schemamap_schema_onClick = function(rowIndex, rowData) {
		$('#oc_schemamap_schema_name').val(rowData.name);
		$('#oc_schemamap_table_datagrid').datagrid('load', {
			"schemaName" : rowData.name
		});
	};
	
</script>