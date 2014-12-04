<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<div id="sys_authuser_tabs"  class="easyui-tabs" data-options="fit:true,onSelect:sys_authuser_selecttab">
	<div title="用户授角色">
		<div class="easyui-layout" data-options="fit:true,border:false">
			<div data-options="region:'west',split:true,title:'人员列表'"
				style="width: 310px;">
				<table id="authUser_user_datagrid" class="easyui-datagrid"
					data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=userService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					onClickRow:authUser_from_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,autoRowHeight:false,fitColumns:true,toolbar : '#sys_authuser_form_toolbar'">
					<thead>
						<tr>
							<th
								data-options="field:'guid',width:100,align:'left',sortable:true,hidden:true">guid</th>
							<th
								data-options="field:'loginId',width:100,align:'left',sortable:true">工号</th>
							<th
								data-options="field:'name',width:200,align:'left',sortable:true">姓名</th>
						</tr>
					</thead>
				</table>
			</div>
			<div data-options="region:'center',title:'人员授权管理'">
				<div class="easyui-layout" data-options="fit:true,border:false">
					<div data-options="region:'north'" style="height: 50px;">
						<table class="tableForm" style="width: 100%">
							<tr>
								<th><font color="red">当前选择人员</font></th>
								<td><input id="userName" name="userName"
									readonly="readonly" type="text" title="请在选择右边机构树"
									style="width: 200px;" /></td>
							</tr>
						</table>
					</div>
					<div data-options="region:'center'" title="角色别表">
						<table id="authUser_role_datagrid" class="easyui-datagrid" style="height: 340px"
							data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=authuserService&method=queryNotInRole',pagination:true,striped:true,idField:'guid',
						fit:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
							<thead>
								<tr>
									<th
										data-options="field:'guid',width:80,checkbox:true,title:'guid'">guid</th>
									<th
										data-options="field:'roleCode',width:100,align:'left',sortable:true">角色名称</th>
									<th
										data-options="field:'roleName',width:100,align:'left',sortable:true">角色名称</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div title="角色授用户">
	<div class="easyui-layout" data-options="fit:true,border:false">
			<div data-options="region:'west',split:true,title:'角色列表'"
				style="width: 450px;">
				<table id="authUser_role_datagrid1" class="easyui-datagrid"
					data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roleService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					onClickRow:authUser_role_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,autoRowHeight:false,fitColumns:true,toolbar : '#sys_authuser_role_toolbar'">
					<thead>
						<tr>
							<th
								data-options="field:'guid',width:80,title:'guid',hidden:true">guid</th>
							<th
								data-options="field:'roleCode',width:100,align:'left',sortable:true">角色名称</th>
							<th
								data-options="field:'roleName',width:100,align:'left',sortable:true">角色名称</th>
						</tr>
					</thead>
				</table>
			</div>
			<div data-options="region:'center',title:'人员授权管理'">
				<div class="easyui-layout" data-options="fit:true,border:false">
					<div data-options="region:'north'" style="height: 50px;">
						<table class="tableForm" style="width: 100%">
							<tr>
								<th><font color="red">当前角色名称</font></th>
								<td><input id="roleName" name="roleName"
									readonly="readonly" type="text" title="请在选择右边机构树"
									style="width: 200px;" /></td>
							</tr>
						</table>
					</div>
					<div data-options="region:'center'" title="用户列表">
						<table id="authUser_user_datagrid1" class="easyui-datagrid" style="height: 340px"
							data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=authuserService&method=queryNotInUser',pagination:true,striped:true,idField:'guid',
						fit:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
							<thead>
								<tr>
									<th
										data-options="field:'guid',width:80,checkbox:true,title:'guid'">guid</th>
									<th
										data-options="field:'loginId',width:100,align:'left',sortable:true">角色名称</th>
									<th
										data-options="field:'name',width:100,align:'left',sortable:true">角色名称</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="sys_authuser_form_toolbar" style="display: none;">
	<div id="authUsermm" style="width: 90px;">
		<div data-options="name:'loginId'">工号</div>
		<div data-options="name:'name'">姓名</div>
	</div>
	<input class="easyui-searchbox"
		data-options="searcher:sys_authuser_from_search,prompt:'请输入值，输完，回车!',menu:'#authUsermm'"
		style="width: 300px;" />
</div>
<div id="sys_authuser_role_toolbar" style="display: none;">
	<div id="sys_role_mm" style="width: 90px;">
		<div data-options="name:'roleName'">角色名称</div>
		<div data-options="name:'roleCode'">角色代码</div>
	</div>
	<input class="easyui-searchbox"
		data-options="searcher:sys_authuser_role_search,prompt:'请输入值，输完，回车!',menu:'#sys_role_mm'"
		style="width: 300px;" />
</div>

<input type="hidden" id="sys_authuser_checkType" value="0">
<script type="text/javascript">
	var sys_authuser_selecttab = function(title,index){
		$('#sys_authuser_checkType').val(index);
	};
	
	var sys_authuser_from_search = function(value, name) {
		if (value != null && value != '') {
			var o = new Object();
			o[name] = value;
			$('#authUser_user_datagrid').datagrid('load',
					$.parseJSON(JSON.stringify(o)));
			o = null;
		} else {
			$('#authUser_user_datagrid').datagrid('load', {});
		}
	};
	var sys_authuser_role_search = function(value, name) {
		if (value != null && value != '') {
			var o = new Object();
			o[name] = value;
			$('#authUser_role_datagrid1').datagrid('load',
					$.parseJSON(JSON.stringify(o)));
			o = null;
		} else {
			$('#authUser_role_datagrid1').datagrid('load', {});
		}
	};
	var authUser_from_onClick = function(rowIndex, rowData) {
		$('#userName').val(rowData.name);
		$('#authUser_role_datagrid').datagrid('load', {
			"loginId" : rowData.loginId
		});
	};
	var authUser_role_onClick = function(rowIndex, rowData) {
		$('#roleName').val(rowData.roleName);
		$('#authUser_user_datagrid1').datagrid('load', {
			"roleCode" : rowData.roleCode
		});
	};
</script>