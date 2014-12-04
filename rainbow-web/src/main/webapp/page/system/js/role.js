//列操作
var sys_role_caozuo = function(value, row, index) {
	return  formatString('<img title="删除" onclick="sys_role_delete(\'{0}\',\'{1}\');" src="{2}"/>',row.guid,row.roleName, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_role_reload = function(){
	$('#sys_role_datagrid').datagrid('clearSelections');
	$('#sys_role_datagrid').datagrid('reload',{});
};

//快速查找
var sys_role_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_role_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_role_reload();
	}
};



//修改操作
function sys_role_editNode(code){
	$('#sys_role_datagrid').datagrid('clearSelections');
	$('#sys_role_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_role_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_role_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_role_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("roleService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_role_callback(d,sys_role_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/roleForm.jsp',buttons,600,350,true,'编辑信息','sys_role_addForm',node);
}

//新增操作
function sys_role_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			var orgNode = $('#sys_role_orgTree').tree('getSelected');
			if(orgNode){
				var nodes = $('#role_roletype_datagrid').datagrid('getSelections');
				if(nodes.length > 0){
					var d = $(this).closest('.window-body');
					var rainbow = new Rainbow();
					rainbow.set("orgId",orgNode.id);
					rainbow.set("orgName",orgNode.text);
					rainbow.setRows(nodes);
					rainbow.setService("roleService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_role_callback(d,sys_role_reload));
				}else{
					$.messager.show({
						title : '提示',
						msg : '请至少选择一个角色类型!'
					});
				}
				
			}else{
				$.messager.show({
					title : '提示',
					msg : '请选择组织机构!'
				});
			}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/roleForm.jsp',buttons,800,500,true,'新增信息','sys_role_addForm');
}
//批量修改
var sys_role_update = function(){
	var rows = $('#sys_role_datagrid').datagrid('getRows');
	 for ( var i = 0; i < rows.length; i++) {
		 $('#sys_role_datagrid').datagrid('beginEdit', i);
	 }
};

//保存
var sys_role_save = function(){
	var rows = $('#sys_role_datagrid').datagrid('getRows');
	for ( var i = 0; i < rows.length; i++) {
		 $('#sys_role_datagrid').datagrid('endEdit', i);
	 }
	var changeRows = $('#sys_role_datagrid').datagrid('getChanges');
	var rainbow = new Rainbow();
	rainbow.setRows(changeRows);
	rainbow.setService("roleService");
	rainbow.setMethod("update");
	rainbowAjax.excute(rainbow,new sys_role_callback(null,sys_role_reload));
};


var sys_role_cancel = function(){
	var rows = $('#sys_role_datagrid').datagrid('getRows');
	for ( var i = 0; i < rows.length; i++) {
		 $('#sys_role_datagrid').datagrid('cancelEdit', i);
	 }
};
//删除操作
var sys_role_delete = function(code,roleName){
	$.messager.confirm('询问', '您确定要删除当前【'+roleName+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("roleService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_role_callback(d,sys_role_reload));
		}
	});
};

//批量删除操作
var sys_role_batchDelete = function(){
	var nodes = $('#sys_role_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
				$.messager.progress();
				var rainbow = new Rainbow();
				for(var i = 0 ; i < nodes.length ; i++){
					rainbow.addRows({"guid":nodes[i].guid});
				}
				rainbow.setService("roleService");
				rainbow.setMethod("delete");
				rainbowAjax.excute(rainbow,new sys_role_callback(null,sys_role_reload));
			}
		});
	}else{
		$.messager.show({
			title : '提示',
			msg : '请选择一条记录!'
		});
	}
};

//ajax回调处理
var sys_role_callback = function(dialog,relod){
		this.onSuccess=function(data){
			try {
				if (data.success) {
					relod();
					if(dialog){
						dialog.dialog('destroy');
					}
				}
				$.messager.progress('close');
				$.messager.show({
					title : '提示',
					msg : data.msg
				});
			} catch (e) {
				$.messager.progress('close');
				$.messager.alert('提示', "系统异常!");
			}
		};
		this.onFail = function(jqXHR, textStatus, errorThrown){
			$.messager.progress('close');
			$.messager.alert('提示', "系统异常!");
		};
};

//查询过滤
var sys_role_query = function(){
	var datas =serializeObject($('#sys_role_queryForm'));
	$('#sys_role_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_role_queryClear = function(){
	$('#sys_role_queryForm input').val('');
	sys_role_reload();
};