//列操作
var sys_suggestion_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_suggestion_editNode(\'{0}\',{1});" src="{2}"/>&nbsp;<img title="删除" onclick="sys_suggestion_delete(\'{3}\');" src="{4}"/>', row.guid,row.sugStatus, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};


//刷新
var sys_suggestion_reload = function(){
	$('#sys_suggestion_datagrid').datagrid('clearSelections');
	$('#sys_suggestion_datagrid').datagrid('reload',{});
};

//快速查找
var sys_suggestion_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_suggestion_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_suggestion_reload();
	}
};



//修改操作
function sys_suggestion_editNode(suggestion,status){
	
	$('#sys_suggestion_datagrid').datagrid('clearSelections');
	$('#sys_suggestion_datagrid').datagrid('selectRecord', suggestion);
	var node = $('#sys_suggestion_datagrid').datagrid('getSelected');
	if(status < 2){
		node['sugStatus'] = '2';
	}
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_suggestion_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_suggestion_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("suggestionService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_suggestion_callback(d,sys_suggestion_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/suggestionForm.jsp',buttons,800,500,true,'编辑信息','sys_suggestion_addForm',node);
}

//删除操作
var sys_suggestion_delete = function(guid){
	$.messager.confirm('询问', '您确定要删除当前记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("suggestionService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_suggestion_callback(d,sys_suggestion_reload));
		}
	});
};

//批量删除操作
var sys_suggestion_batchDelete = function(){
	var nodes = $('#sys_suggestion_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("suggestionService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_suggestion_callback(null,sys_suggestion_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var sys_suggestion_callback = function(dialog,relod){
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
var sys_suggestion_query = function(){
	var datas =serializeObject($('#sys_suggestion_queryForm'));
	$('#sys_suggestion_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_suggestion_query_clear = function(){
	$('#sys_suggestion_queryForm input').val('');
	sys_suggestion_reload();
};

var sys_suggestion_type = null;
var sys_suggestion_sugLevel = null;
var sys_suggestion_sugStatus = null;
var sys_suggestion_sugLevelFormatter = function(value){
	for(var i=0; i<sys_suggestion_sugLevel.length; i++){
		if (sys_suggestion_sugLevel[i].value == value) 
			return sys_suggestion_sugLevel[i].text;
	}
	return value;
};
var sys_suggestion_typeFormatter = function(value){
	for(var i=0; i<sys_suggestion_type.length; i++){
		if (sys_suggestion_type[i].value == value) 
			return sys_suggestion_type[i].text;
	}
	return value;
};

var sys_suggestion_sugStatusFormatter = function(value){
	for(var i=0; i<sys_suggestion_sugStatus.length; i++){
		if (sys_suggestion_sugStatus[i].value == value) 
			return sys_suggestion_sugStatus[i].text;
	}
	return value;
};

$(function($){
	$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUGGESTION',null,function(data){
		sys_suggestion_type = data.rows;
	});
	$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUG_LEVEL',null,function(data){
		sys_suggestion_sugLevel = data.rows;
	});
	$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUG_STATUS',null,function(data){
		sys_suggestion_sugStatus = data.rows;
	});
});