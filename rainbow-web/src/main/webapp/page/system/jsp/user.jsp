<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="sys_user_datagrid" title="用户信息维护" class="easyui-datagrid" 
		data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=userService&method=queryByPage',pagination:true,striped:true,idField:'guid',
				pageSize:20,pageList:[20,50,100,150,200],fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,autoRowHeight:false,toolbar : '#sys_user_toolbar',
				frozenColumns:[[
			        {field:'guid',width:80,checkbox:true,title:'guid'},
			        {field:'caozuo',width:50,align:'center',sortable:true,formatter:sys_user_caozuo,frozen:true,title:'操作'}
			    ]]">
	<thead>
			<tr>
				<th data-options="field:'loginId',width:100,align:'left',sortable:true">工号</th>
				<th data-options="field:'name',width:100,align:'left',sortable:true">姓名</th>
				<th data-options="field:'age',width:100,align:'left',sortable:true">年龄</th>
				<th data-options="field:'aliveFlag',width:100,align:'left',sortable:true,formatter:sys_user_aliveFlagFormatter">是否有效</th>
				<th data-options="field:'isOnline',width:100,align:'left',sortable:true,formatter:sys_user_isOnlineFormatter">是否在线</th>
				<th data-options="field:'isVerify',width:100,align:'left',sortable:true,formatter:sys_user_isVerifyFormatter">是否需要验证码</th>
				<th data-options="field:'sex',width:100,align:'left',sortable:true,formatter:sys_user_sexFormatter">性别</th>
				<th data-options="field:'mobilePhone',width:100,align:'left',sortable:true">移动电话</th>
				<th data-options="field:'workStatus',width:100,align:'left',sortable:true,formatter:sys_user_workStatusFormatter">工作状态</th>
				<th data-options="field:'inJoinTime',width:100,align:'left',sortable:true">入职时间</th>
				<th data-options="field:'createTime',width:100,align:'left',sortable:true">创建时间</th>
				<th data-options="field:'createUser',width:100,align:'left',sortable:true">创建人</th>
			</tr>
	</thead>
</table>


<div id="sys_user_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="sys_user_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_user_copyAdd();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_user_batchDelete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="sys_user_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="sys_user_mm" style="width:90px" >
		<div data-options="name:'loginId'">工号</div>
		<div data-options="name:'name'">姓名</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:sys_user_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#sys_user_mm'" style="width:300px;" />
</div>
	
<script type="text/javascript" src="./page/system/js/user.js"></script>