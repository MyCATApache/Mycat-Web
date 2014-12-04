<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',split:true,title:'角色列表'" style="width:420px;">
		<div id="sys_authbutton_form_toolbar" style="display: none;">
			<div id="sys_authbutton_form_mm" style="width:90px;" >
				<div data-options="name:'roleCode'">角色代码</div>
				<div data-options="name:'roleName'">角色名称</div>
			</div>
			<input class="easyui-searchbox" data-options="searcher:sys_authbutton_from_search,prompt:'请输入值，输完，回车!',menu:'#sys_authbutton_form_mm'" style="width:410px;" />
		</div>
		<table id="authbutton_role_datagrid" class="easyui-datagrid" style="height:360px"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roleService&method=queryByPage',pagination:true,striped:true,idField:'guid',
						onClickRow:authbutton_from_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_authbutton_form_toolbar'">
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
					<td><input id="sys_button_roleName" name="roleName" readonly="readonly" type="text" title="请在选择右边角色" style="width:200px;" /></td>
				</tr>
			</table>
		<div class="easyui-panel" title="按钮信息"    
		        style="width:362px;height:412px;">  
		      <table id="authbutton_button_datagrid" class="easyui-datagrid"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=authbuttonService&method=queryAuthButtonByPage',pagination:true,striped:true,idField:'guid',
				fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,
				frozenColumns:[[
				        {field:'guid',width:80,checkbox:true,title:'guid'}
				]]">
				<thead>
					<tr>
						<th data-options="field:'pageCode',width:100,align:'left',sortable:true">所属页面代码</th>
						<th data-options="field:'buttonCode',width:100,align:'left',sortable:true">按钮代码</th>
						<th data-options="field:'buttonName',width:100,align:'left',sortable:true">按钮名称</th>
					</tr>
				</thead>
			</table>
		</div> 
	</div>
</div>
<script type="text/javascript">
var sys_authbutton_from_search = function(value,name){
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#authbutton_role_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		$('#authbutton_role_datagrid').datagrid('load',{});
	}
};

var authbutton_from_onClick = function(rowIndex, rowData){
	$('#sys_button_roleName').val(rowData.roleName);
	var o = new Object();
	o["roleGuid"] = rowData.guid;
	$("#authbutton_button_datagrid").datagrid('clearSelections');
	$("#authbutton_button_datagrid").datagrid('load',$.parseJSON(JSON.stringify(o)));
};

//折叠
var sys_authresouce_undo = function(){
	$('#sys_authbutton_form_meunTree').tree('collapseAll');
};

//展开
var sys_authresouce_redo = function(){
	$('#sys_authbutton_form_meunTree').tree('expandAll');
};
</script>