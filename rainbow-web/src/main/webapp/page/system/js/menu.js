//操作
var sys_menu_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_menu_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_menu_delete(\'{2}\');" src="{3}"/>', row.code, './ui/style/images/extjs_icons/pencil.png', row.code, './ui/style/images/extjs_icons/delete.png');
};

//展开
var sys_menu_undo = function(){
	var node = $('#sys_menu_datagrid').treegrid('getSelected');
	if (node) {
		$('#sys_menu_datagrid').treegrid('collapseAll', node.cid);
	} else {
		$('#sys_menu_datagrid').treegrid('collapseAll');
	}
};

//折叠
var sys_menu_redo = function(){
	var node = $('#sys_menu_datagrid').treegrid('getSelected');
	if (node) {
		$('#sys_menu_datagrid').treegrid('expandAll', node.cid);
	} else {
		$('#sys_menu_datagrid').treegrid('expandAll');
	}
};

//刷新
var sys_menu_reload = function(){
	$('#sys_menu_datagrid').treegrid('reload');
};

//查询
var sys_menu_query = function(value,name){
	var rainbow = new Rainbow();
	if(value != null && value != ''){
		rainbow.setParam(name, value);
	}
	rainbow.setService("menuService");
	rainbow.setMethod("query");
	var callback = {
		onSuccess:function(data){
			try {
				if (data.success) {
					$('#sys_menu_datagrid').treegrid('loadData',data.rows);
				}
				$.messager.show({
					title : '提示',
					msg : data.msg
				});
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

//菜单编辑事件
function sys_menu_editNode(code){
	if (code != undefined) {
		$('#sys_menu_datagrid').treegrid('select', code);
	}
	var node = $('#sys_menu_datagrid').treegrid('getSelected');
	buttons = [ {
		text : '编辑',
		iconCls : 'icon-edit',
		handler : function() {
				if(isValid('sys_menu_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_menu_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("menuService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new sys_menu_callback(d,sys_menu_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/menuForm.jsp',buttons,600,320,true,'编辑信息','sys_menu_addForm',node);
}

//增加菜单
function sys_menu_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_menu_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_menu_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("menuService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_menu_callback(d,sys_menu_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/menuForm.jsp',buttons,600,320,true,'新增信息','sys_menu_addForm');
}


//删除菜单
var sys_menu_delete = function(code){
	if (code != undefined) {
		$('#sys_menu_datagrid').treegrid('select', code);
	}
	var node = $('#sys_menu_datagrid').treegrid('getSelected');
	$.messager.confirm('询问', '您确定要删除【'+node.name+'】以及所有子节点？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows(node);
			rainbow.setService("menuService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_menu_callback(d,sys_menu_reload));
		}
	});
};

//ajax回调处理
var sys_menu_callback = function(dialog,relod){
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


