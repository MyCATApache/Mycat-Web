//列操作
var sys_setup_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_setup_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_setup_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_setup_reload = function(){
	$('#sys_setup_datagrid').datagrid('clearSelections');
	$('#sys_setup_datagrid').datagrid('reload',{});
};

//快速查找
var sys_setup_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_setup_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_setup_reload();
	}
};



//修改操作
function sys_setup_editNode(setup){
	$('#sys_setup_datagrid').datagrid('clearSelections');
	$('#sys_setup_datagrid').datagrid('selectRecord', setup);
	var node = $('#sys_setup_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_setup_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_setup_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("setupService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_setup_callback(d,sys_setup_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/setupForm.jsp',buttons,600,420,true,'编辑信息','sys_setup_addForm',node);
}

//新增操作
function sys_setup_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_setup_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_setup_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("setupService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_setup_callback(d,sys_setup_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/setupForm.jsp',buttons,600,420,true,'新增信息','sys_setup_addForm');
}
//复制新增操作
var sys_setup_copyAdd = function(){
	var nodes = $('#sys_setup_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_setup_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_setup_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("setupService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_setup_callback(d,sys_setup_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/setupForm.jsp',buttons,600,420,true,'编辑信息','sys_setup_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_setup_delete = function(guid){
	$.messager.confirm('询问', '您确定要删除当前记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("setupService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_setup_callback(d,sys_setup_reload));
		}
	});
};

//批量删除操作
var sys_setup_batchDelete = function(){
	var nodes = $('#sys_setup_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("setupService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_setup_callback(null,sys_setup_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var sys_setup_callback = function(dialog,relod){
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
var sys_setup_query = function(){
	var datas =serializeObject($('#sys_setup_queryForm'));
	$('#sys_setup_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_setup_query_clear = function(){
	$('#sys_setup_queryForm input').val('');
	sys_setup_reload();
};
var sys_setup_isDisplay = [{'text':'是','value':'1'},{'text':'否','value':'0'}];
var sys_setup_isDisplayFormatter = function(value, row, index){
	for ( var i = 0; i < sys_setup_isDisplay.length; i++) {
		if(sys_setup_isDisplay[i].value==value){
			return sys_setup_isDisplay[i].text;
		}
	}
	return value;
};