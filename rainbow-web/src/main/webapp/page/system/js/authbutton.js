//列操作
var sys_authbutton_caozuo = function(value, row, index) {
	return  formatString('<img title="删除" onclick="sys_authbutton_delete(\'{0}\');" src="{1}"/>', row.guid, './ui/style/images/extjs_icons/delete.png');
};


//刷新
var sys_authbutton_reload = function(){
	$('#sys_authbutton_datagrid').datagrid('clearSelections');
	$('#sys_authbutton_datagrid').datagrid('reload',{});
};

//快速查找
var sys_authbutton_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_authbutton_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_authbutton_reload();
	}
};



//修改操作
function sys_authbutton_editNode(authbutton){
	if (authbutton != undefined) {
		$('#sys_authbutton_datagrid').datagrid('selectRecord', authbutton);
	}
	var node = $('#sys_authbutton_datagrid').datagrid('getSelections');
	
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_authbutton_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_authbutton_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("authbuttonService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_authbutton_callback(d,sys_authbutton_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/authbuttonForm.jsp',buttons,800,550,true,'编辑信息','sys_authbutton_addForm',node);
}

//新增操作
function sys_authbutton_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			var roleNode = $('#authbutton_role_datagrid').datagrid('getSelected');
			if(roleNode){
				var nodes = $('#authbutton_button_datagrid').datagrid('getChecked');
				if(nodes.length > 0){
					var d = $(this).closest('.window-body');
					var rainbow = new Rainbow();
					rainbow.set("roleGuid",roleNode.guid);
					rainbow.set("roleCode",roleNode.roleCode);
					rainbow.setRows(nodes);
					rainbow.setService("authbuttonService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_authbutton_callback(d,sys_authbutton_reload));
				}else{
					$.messager.show({
						title : '提示',
						msg : '请至少选择一个按钮!'
					});
				}
				
			}else{
				$.messager.show({
					title : '提示',
					msg : '请选择角色!'
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
	rainbowDialog.addDialog('./page/system/jsp/authbuttonForm.jsp',buttons,800,550,true,'授权管理<font color="red">(角色与按钮建立关联后，所属页面的该按钮不可见！)</font>','sys_authbutton_addForm');
}
//复制新增操作
var sys_authbutton_copyAdd = function(){
	var nodes = $('#sys_authbutton_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_authbutton_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_authbutton_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("authbuttonService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_authbutton_callback(d,sys_authbutton_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/authbuttonForm.jsp',buttons,800,350,true,'编辑信息','sys_authbutton_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_authbutton_delete = function(guid){
	$.messager.confirm('询问', '您确定要删除当前选择的记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("authbuttonService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_authbutton_callback(d,sys_authbutton_reload));
		}
	});
};

//批量删除操作
var sys_authbutton_batchDelete = function(){
	var nodes = $('#sys_authbutton_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("authbuttonService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_authbutton_callback(null,sys_authbutton_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var sys_authbutton_callback = function(dialog,relod){
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
var sys_authbutton_query = function(){
	var datas =serializeObject($('#sys_authbutton_queryForm'));
	$('#sys_authbutton_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_authbutton_queryClear = function(){
	$('#sys_authbutton_queryForm input').val('');
	sys_authbutton_reload();
};