//列操作
var sys_roletype_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_roletype_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_roletype_delete(\'{2}\',\'{3}\');" src="{4}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, row.roleTypeName,'./ui/style/images/extjs_icons/delete.png');
};

var sys_roletype_roletypegroups =[];
var sys_roletype_statusArray =[{"key":0,"value":"有效"},{"key":1,"value":"无效"}];

var sys_roletype_groupFormatter = function(value){
	for(var i=0; i<sys_roletype_roletypegroups.length; i++){
		if (sys_roletype_roletypegroups[i].value == value) return sys_roletype_roletypegroups[i].text;
	}
	return value;
};

var sys_roletype_status = function(value){
	for(var i=0; i<sys_roletype_statusArray.length; i++){
		if (sys_roletype_statusArray[i].key == value) return sys_roletype_statusArray[i].value;
	}
	return value;
};

//刷新
var sys_roletype_reload = function(){
	$('#sys_roletype_datagrid').datagrid('clearSelections');
	$('#sys_roletype_datagrid').datagrid('reload',{});
};

//查找
var sys_roletype_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_roletype_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_roletype_reload();
	}
};
//修改操作
function sys_roletype_editNode(code){
	$('#sys_roletype_datagrid').datagrid('clearSelections');
	$('#sys_roletype_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_roletype_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_roletype_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_roletype_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("roletypeService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_roletype_callback(d,sys_roletype_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/roletypeForm.jsp',buttons,600,350,true,'编辑信息','sys_roletype_addForm',node);
}

//新增操作
function sys_roletype_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_roletype_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_roletype_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("roletypeService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_roletype_callback(d,sys_roletype_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/roletypeForm.jsp',buttons,600,350,true,'新增信息','sys_roletype_addForm');
}
//复制新增操作
var sys_roletype_copyAdd = function(){
	var nodes = $('#sys_roletype_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_roletype_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_roletype_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("roletypeService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_roletype_callback(d,sys_roletype_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/roletypeForm.jsp',buttons,600,350,true,'编辑信息','sys_roletype_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_roletype_delete = function(code,roleTypeName){
	$.messager.confirm('询问', '您确定要删除当前【'+roleTypeName+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("roletypeService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_roletype_callback(d,sys_roletype_reload));
		}
	});
};

//批量删除操作
var sys_roletype_batchDelete = function(){
var nodes = $('#sys_roletype_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("roletypeService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_roletype_callback(null,sys_roletype_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//ajax回调处理
var sys_roletype_callback = function(dialog,relod){
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
var sys_roletype_query = function(){
	var datas =serializeObject($('#sys_roletype_queryForm'));
	$('#sys_roletype_datagrid').datagrid('load',datas);
};


//清空查询条件
var sys_roletype_queryClear = function(){
	$('#sys_roletype_queryForm input').val('');
	sys_roletype_reload();
};

$(function(){
	rainbowAjax.comboxCode('ROLEYTYPEGROUP',function(datas){
		sys_roletype_roletypegroups = datas.rows;
	});
});