//列操作
var sys_authuser_caozuo = function(value, row, index) {
	return  formatString('<img title="删除" onclick="sys_authuser_delete(\'{0}\');" src="{1}"/>', row.guid,'./ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_authuser_reload = function(){
	$('#sys_authuser_datagrid').datagrid('clearSelections');
	$('#sys_authuser_datagrid').datagrid('reload',{});
};

//快速查找
var sys_authuser_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_authuser_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_authuser_reload();
	}
};



//修改操作
function sys_authuser_editNode(code){
	$('#sys_authuser_datagrid').datagrid('clearSelections');
	$('#sys_authuser_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_authuser_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_authuser_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_authuser_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("authuserService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_authuser_callback(d,sys_authuser_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/authuserForm.jsp',buttons,850,500,true,'编辑信息','sys_authuser_addForm',node);
}

//新增操作
function sys_authuser_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			var roletype = $('#sys_authuser_checkType').val();
			if(roletype == 0){
				var userNode = $('#authUser_user_datagrid').datagrid('getSelected');
				if(userNode){
					var nodes = $('#authUser_role_datagrid').datagrid('getSelections');
					if(nodes.length > 0){
						var d = $(this).closest('.window-body');
						var rainbow = new Rainbow();
						rainbow.set("userGuid",userNode.guid);
						rainbow.set("userCode",userNode.loginId);
						rainbow.setRows(nodes);
						rainbow.setService("authuserService");
						rainbow.setMethod("insertByUser");
						rainbowAjax.excute(rainbow,new sys_authuser_callback(d,sys_authuser_reload));
					}else{
						$.messager.show({
							title : '提示',
							msg : '请至少选择一个角色!'
						});
					}
					
				}else{
					$.messager.show({
						title : '提示',
						msg : '请选择用户!'
					});
				}
			}else{
				var userNode = $('#authUser_role_datagrid1').datagrid('getSelected');
				if(userNode){
					var nodes = $('#authUser_user_datagrid1').datagrid('getSelections');
					if(nodes.length > 0){
						var d = $(this).closest('.window-body');
						var rainbow = new Rainbow();
						rainbow.set("roleGuid",userNode.guid);
						rainbow.set("roleCode",userNode.roleCode);
						rainbow.setRows(nodes);
						rainbow.setService("authuserService");
						rainbow.setMethod("insertByRole");
						rainbowAjax.excute(rainbow,new sys_authuser_callback(d,sys_authuser_reload));
					}else{
						$.messager.show({
							title : '提示',
							msg : '请至少选择一个用户!'
						});
					}
					
				}else{
					$.messager.show({
						title : '提示',
						msg : '请选择角色!'
					});
				}
			}
		}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/authuserForm.jsp',buttons,850,550,true,'新增信息','sys_authuser_addForm');
}
//复制新增操作
var sys_authuser_copyAdd = function(){
	var nodes = $('#sys_authuser_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_authuser_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_authuser_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("authuserService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_authuser_callback(d,sys_authuser_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/authuserForm.jsp',buttons,850,500,true,'编辑信息','sys_authuser_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_authuser_delete = function(code){
	$.messager.confirm('询问', '您确定要删除当前选择的记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("authuserService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_authuser_callback(d,sys_authuser_reload));
		}
	});
};

//批量删除操作
var sys_authuser_batchDelete = function(){
	var nodes = $('#sys_authuser_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
				$.messager.progress();
				var rainbow = new Rainbow();
				for(var i = 0 ; i < nodes.length ; i++){
					rainbow.addRows({"guid":nodes[i].guid});
				}
				rainbow.setService("authuserService");
				rainbow.setMethod("delete");
				rainbowAjax.excute(rainbow,new sys_authuser_callback(null,sys_authuser_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//ajax回调处理
var sys_authuser_callback = function(dialog,relod){
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
var sys_authuser_query = function(){
	var datas =serializeObject($('#sys_authuser_queryForm'));
	$('#sys_authuser_datagrid').datagrid('load',datas);
};



//清空查询条件
var sys_authuser_queryClear = function(){
	$('#sys_authuser_queryForm input').val('');
	sys_authuser_reload();
};