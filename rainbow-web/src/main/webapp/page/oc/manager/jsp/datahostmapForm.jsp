<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'west',split:true,title:'物理节点列表'"
			style="width: 250px;">
			<table id="oc_datahostmap_datahost_datagrid"  class="easyui-datagrid" 
					data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=datahostService&method=query',pagination:false,striped:true,idField:'guid',
							onClickRow:oc_datahostmap_host_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
				<thead>
						<tr>
							<th data-options="field:'name',width:100,align:'left',sortable:true">物理节点</th>
						</tr>
				</thead>
			</table>
		</div>
		<div data-options="region:'center',title:'物理机绑定'">
			<div class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'north'" style="height: 40px;">
					<table class="queryForm" style="width: 100%">
						<tr>
							<th><font color="red">当前物理节点名称</font></th>
							<td><input id="oc_datahostmap_datahostname" name="roleName"
								readonly="readonly" type="text" title="请在选择右边机构树"
								style="width: 200px;" /></td>
						</tr>
					</table>
				</div>
				<div data-options="region:'center'" title="可绑定的物理机列表">
					<table id="oc_datahostmap_host_datagrid"  class="easyui-datagrid" 
							data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=hostService&method=queryLink',pagination:true,striped:true,idField:'guid',
									fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar:'#oc_datahostmap_datahost_toolbar',
									frozenColumns:[[
								        {field:'guid',width:80,checkbox:true,title:'guid'},
								    ]]">
						<thead>
								<tr>
									<th data-options="field:'host',width:100,align:'left',sortable:true">物理节点名称</th>
									<th data-options="field:'url',width:200,align:'left',sortable:true">链接地址</th>
									<th data-options="field:'dUser',width:100,align:'left',sortable:true">用户</th>
									<th data-options="field:'password',width:100,align:'left',sortable:true">密码</th>
								</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
<div id="oc_datahostmap_datahost_toolbar" style="display: none;">
	<div id="authUsermm" style="width: 90px;">
		<div data-options="name:'host'">物理节点名称</div>
	</div>
	<input class="easyui-searchbox"
		data-options="searcher:oc_datahostmap_host_search,prompt:'请输入值，输完，回车!',menu:'#authUsermm'"
		style="width: 300px;" />
</div>

<input type="hidden" id="sys_authuser_checkType" value="0">
<script type="text/javascript">	
	var oc_datahostmap_host_search = function(value, name) {
		if (value != null && value != '') {
			var o = new Object();
			o[name] = value;
			$('#oc_datahostmap_host_datagrid').datagrid('load',
					$.parseJSON(JSON.stringify(o)));
			o = null;
		} else {
			$('#oc_datahostmap_host_datagrid').datagrid('load', {});
		}
	};
	var oc_datahostmap_host_onClick = function(rowIndex, rowData) {
		$('#oc_datahostmap_datahostname').val(rowData.name);
		$('#oc_datahostmap_host_datagrid').datagrid('load', {
			"datahost" : rowData.name
		});
	};
	
</script>

