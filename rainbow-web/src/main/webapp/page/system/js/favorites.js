//列操作
var system_favorites_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="system_favorites_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="system_favorites_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var system_favorites_reload = function(){
	$('#system_favorites_datagrid').datagrid('clearSelections');
	$('#system_favorites_datagrid').datagrid('reload',{});
};

//快速查找
var system_favorites_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#system_favorites_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		system_favorites_reload();
	}
};



//修改操作
function system_favorites_editNode(favorites){
	$('#system_favorites_datagrid').datagrid('clearSelections');
	$('#system_favorites_datagrid').datagrid('selectRecord', favorites);
	var node = $('#system_favorites_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#system_favorites_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#system_favorites_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("favoritesService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new system_favorites_callback(d,system_favorites_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/favoritesForm.jsp',buttons,600,350,true,'编辑信息','system_favorites_addForm',node);
}

//新增操作
function system_favorites_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('system_favorites_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#system_favorites_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("favoritesService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new system_favorites_callback(d,system_favorites_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/favoritesForm.jsp',buttons,600,350,true,'新增信息','system_favorites_addForm');
}
//复制新增操作
var system_favorites_copyAdd = function(){
	var nodes = $('#system_favorites_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('system_favorites_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#system_favorites_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("favoritesService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new system_favorites_callback(d,system_favorites_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/favoritesForm.jsp',buttons,600,350,true,'编辑信息','system_favorites_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var system_favorites_delete = function(guid){
	$.messager.confirm('询问', '您确定要删除当前记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("favoritesService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new system_favorites_callback(d,system_favorites_reload));
		}
	});
};

//批量删除操作
var system_favorites_batchDelete = function(){
	var nodes = $('#system_favorites_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("favoritesService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new system_favorites_callback(null,system_favorites_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var system_favorites_callback = function(dialog,relod){
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
var system_favorites_query = function(){
	var datas =serializeObject($('#system_favorites_queryForm'));
	$('#system_favorites_datagrid').datagrid('load',datas);
};

//清空查询条件
var system_favorites_query_clear = function(){
	$('#system_favorites_queryForm input').val('');
	system_favorites_reload();
};