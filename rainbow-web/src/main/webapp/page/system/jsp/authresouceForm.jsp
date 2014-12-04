<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',split:true,title:'角色列表'" style="width:420px;">
		<div id="sys_authresource_form_toolbar" style="display: none;">
			<div id="sys_authresource_form_mm" style="width:90px;" >
				<div data-options="name:'roleCode'">角色代码</div>
				<div data-options="name:'roleName'">角色名称</div>
			</div>
			<input class="easyui-searchbox" data-options="searcher:sys_authresource_from_search,prompt:'请输入值，输完，回车!',menu:'#sys_authresource_form_mm'" style="width:410px;" />
		</div>
		<table id="authresource_role_datagrid" class="easyui-datagrid" style="height:360px"
				data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=roleService&method=queryByPage',pagination:true,striped:true,idField:'guid',
						onClickRow:authresource_from_onClick,singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#sys_authresource_form_toolbar'">
			<thead>
					<tr>
						<th data-options="field:'guid',width:80,title:'guid',hidden:true">guid</th>
						<th data-options="field:'roleCode',width:100,align:'left',sortable:true">角色代码</th>
						<th data-options="field:'roleName',width:100,align:'left',sortable:true">角色名称</th>
					</tr>
			</thead>
		</table>
	

	</div>
	<div data-options="region:'center',title:'资源授权管理'" >
			<table class="tableForm" style="width: 100%">
				<tr>
					<th ><font color="red">当前角色</font></th>
					<td><input id="roleName" name="roleName" readonly="readonly" type="text" title="请在选择右边角色" style="width:200px;" /></td>
				</tr>
			</table>
		<div class="easyui-panel" title="菜单资源信息"    
		        style="width:362px;height:412px;"  
		        data-options="tools: [{   
				    iconCls:'icon-redo',   
				    handler:function(){sys_authresouce_redo();}   
				  },{   
				    iconCls:'icon-undo',   
				    handler:function(){sys_authresouce_undo();}   
				  }] ">  
		       <ul id="sys_authresource_form_meunTree" class="easyui-tree" data-options="url:'./dispatcherAction/queryTree.do?service=menuService&method=queryComboxTree',checkbox:true,lines:true,cascadeCheck:false"></ul>
		                
		</div>  	
	</div>
	
</div>
<script type="text/javascript">
var sys_authresource_from_search = function(value,name){
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#authresource_role_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		$('#authresource_role_datagrid').datagrid('load',{});
	}
};

var authresource_from_onClick = function(rowIndex, rowData){
	$('#roleName').val(rowData.roleName);
	var nodes = $('#sys_authresource_form_meunTree').tree('getChecked');
	for(var i = 0 ; i < nodes.length ; i++){
		var node = $('#sys_authresource_form_meunTree').tree('find',nodes[i].id);
		$('#sys_authresource_form_meunTree').tree('uncheck', node.target);
	}
	var data = {"roleGuid":rowData.guid};
	rainbowAjax.post("./dispatcherAction/query.do?service=authresouceService&method=query",data,function(data){
		for(var i =0;i<data.rows.length;i++){
			var resource = data.rows[i];
			var node = $('#sys_authresource_form_meunTree').tree('find',resource["resourceCode"]);
			if(node){
				$('#sys_authresource_form_meunTree').tree('check', node.target);
			}
		}
	},"json");

};

//折叠
var sys_authresouce_undo = function(){
	$('#sys_authresource_form_meunTree').tree('collapseAll');
};

//展开
var sys_authresouce_redo = function(){
	$('#sys_authresource_form_meunTree').tree('expandAll');
};
</script>