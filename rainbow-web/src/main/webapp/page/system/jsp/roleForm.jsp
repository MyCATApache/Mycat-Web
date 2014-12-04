<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',split:true,title:'组织机构树'" style="width:210px;">
	<div id="sys_role_orgmm" style="width:90px;" >
		<div data-options="name:'orgCode'">机构代码</div>
		<div data-options="name:'orgName'">机构名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:filter_org,prompt:'筛选机构',menu:'#sys_role_orgmm'" style="width:200px;" />
	 <ul id="sys_role_orgTree" class="easyui-tree" data-options="url:'./dispatcherAction/queryComboxTree.do?service=orgService&method=queryComboxTree',lines:true"></ul>
	</div>
	<div data-options="region:'center',title:'新增角色'">
		<table class="tableForm" style="width: 100%">
			<tr>
				<th ><font color="red">已选组织机构名称</font></th>
				<td><input id="orgName" name="orgName"  style="width:200px;" /></td>
			</tr>
		</table>
		<table id="role_roletype_datagrid" title="角色类型别表" class="easyui-datagrid" style="height:360px"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roletypeService&method=queryNotInOrg',pagination:true,striped:true,idField:'guid',
						fit:false,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false">
			<thead>
					<tr>
						<th data-options="field:'guid',width:80,checkbox:true,title:'guid'">guid</th>
						<th data-options="field:'roleTypeCode',width:100,align:'left',sortable:true">角色类型代码</th>
						<th data-options="field:'roleTypeName',width:100,align:'left',sortable:true">角色类型名称</th>
					</tr>
			</thead>
		</table>
</div>
	<script type="text/javascript">
	$('#sys_role_orgTree').tree({
		onClick: function(node){
			if(node.text != $('#orgName').val()){
				$('#orgName').val(node.text);
				$('#role_roletype_datagrid').datagrid('load',{orgCode:node.id});
			}
		}
	});


var filter_org= function(value,name){
	var rainbow = new Rainbow();
	if(value != null && value != ''){
		rainbow.setParam(name, value);
	}else{
		$('#sys_role_orgTree').tree('reload');
		return;
	}
	rainbow.setService("orgService");
	rainbow.setMethod("queryTree");
	var callback = {
		onSuccess:function(data){
			try {
				if (data.success) {
					$('#sys_role_orgTree').tree('loadData',data.rows);
				}
			} catch (e) {
				$.messager.alert('提示', "系统异常!");
			}
		},
		onFail:function(jqXHR, textStatus, errorThrown){
			$.messager.alert('提示', "系统异常!");
		}
	};
	rainbowAjax.query(rainbow,callback);
};
</script>
