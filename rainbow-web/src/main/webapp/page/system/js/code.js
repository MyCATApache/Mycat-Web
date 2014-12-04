//列操作
var sys_code_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_code_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_code_delete(\'{2}\',\'{3}\');" src="{4}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid,row.text, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_code_reload = function(){
	$('#sys_code_datagrid').datagrid('clearSelections');
	$('#sys_code_datagrid').datagrid('reload',{});
};

//快速查找
var sys_code_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_code_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_code_reload();
	}
};


//ajax回调处理
var Callback = function(dialog){
		this.onSuccess=function(data){
			try {
				if (data.success) {
					sys_code_reload();
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

//修改操作
function sys_code_editNode(code){
	$('#sys_code_datagrid').datagrid('clearSelections');
	$('#sys_code_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_code_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_code_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_code_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("codeService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_code_callback(d,sys_code_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/codeForm.jsp',buttons,600,350,true,'编辑信息','sys_code_addForm',node);
}

//新增操作
function sys_code_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_code_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_code_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("codeService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_code_callback(d,sys_code_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/codeForm.jsp',buttons,600,350,true,'新增信息','sys_code_addForm');
}
//复制新增操作
var sys_code_copyAdd = function(){
	var nodes = $('#sys_code_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_code_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_code_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("codeService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_code_callback(d,sys_code_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/codeForm.jsp',buttons,600,350,true,'编辑信息','sys_code_addForm',nodes[length -1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_code_delete = function(code,text){
	$.messager.confirm('询问', '您确定要删除当前【' + text + '】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("codeService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_code_callback(d,sys_code_reload));
		}
	});
};

//批量删除操作
var sys_code_batchDelete = function(){
	var nodes = $('#sys_code_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("codeService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_code_callback(null,sys_code_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//ajax回调处理
var sys_code_callback = function(dialog,relod){
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