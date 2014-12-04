<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',split:true,title:'角色列表'" style="width:420px;">
		<div id="sys_authservice_form_toolbar" style="display: none;">
			<div id="sys_authservice_form_mm" style="width:90px;" >
				<div data-options="name:'roleCode'">角色代码</div>
				<div data-options="name:'roleName'">角色名称</div>
			</div>
			<input class="easyui-searchbox" data-options="searcher:sys_authservice_from_search,prompt:'请输入值，输完，回车!',menu:'#sys_authservice_form_mm'" style="width:410px;" />
		</div>
		<table id="authservice_role_datagrid" class="easyui-datagrid" style="height:360px"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roleService&method=queryByPage',pagination:true,striped:true,idField:'guid',
						onClickRow:authservice_from_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_authservice_form_toolbar'">
			<thead>
					<tr>
						<th data-options="field:'guid',width:80,title:'guid',hidden:true">guid</th>
						<th data-options="field:'roleCode',width:100,align:'left',sortable:true">角色代码</th>
						<th data-options="field:'roleName',width:100,align:'left',sortable:true">角色名称</th>
					</tr>
			</thead>
		</table>
	

	</div>
	<div data-options="region:'center',title:'按钮权限管理'" >
			<table class="tableForm" style="width: 100%">
				<tr>
					<th ><font color="red">当前角色</font></th>
					<td><input id="sys_service_roleName" name="roleName" readonly="readonly" type="text" title="请在选择右边角色" style="width:200px;" /></td>
				</tr>
			</table>
		<div class="easyui-panel" title="按钮信息"    
		        style="width:362px;height:412px;">  
		      <table id="authservice_service_datagrid" class="easyui-datagrid"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=authserviceService&method=queryAuthServiceByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:false,autoRowHeight:false,
				frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'}
				]]">
				<thead>
					<tr>
						<th data-options="field:'serviceCode',width:100,align:'left',sortable:true">服务代码</th>
						<th data-options="field:'methodCode',width:100,align:'left',sortable:true">方法代码</th>
						<th data-options="field:'package',width:200,align:'left',sortable:true">所属包</th>
					</tr>
				</thead>
			</table>
		</div> 
	</div>
</div>
<script type="text/javascript">
var sys_authservice_from_search = function(value,name){
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#authservice_role_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		$('#authservice_role_datagrid').datagrid('load',{});
	}
};

var authservice_from_onClick = function(rowIndex, rowData){
	$('#sys_service_roleName').val(rowData.roleName);
	var o = new Object();
	o["roleGuid"] = rowData.guid;
	$("#authservice_service_datagrid").datagrid('clearSelections');
	$("#authservice_service_datagrid").datagrid('load',$.parseJSON(JSON.stringify(o)));
};

//折叠
var sys_authresouce_undo = function(){
	$('#sys_authservice_form_meunTree').tree('collapseAll');
};

//展开
var sys_authresouce_redo = function(){
	$('#sys_authservice_form_meunTree').tree('expandAll');
};
</script>