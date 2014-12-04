function sys_page_caozuo(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_page_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_page_delete(\'{2}\',\'{3}\');" src="{4}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, row.name,'./ui/style/images/extjs_icons/delete.png');
}
//刷新
var sys_page_reload = function(){
	$('#sys_page_datagrid').datagrid('clearSelections');
	$('#sys_page_datagrid').datagrid('reload',{});
};

//快速查找
var sys_page_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_page_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_page_reload();
	}
};

//页面编辑事件
function sys_page_editNode(code){
	$('#sys_page_datagrid').datagrid('clearSelections');
	$('#sys_page_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_page_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '编辑',
		iconCls : 'icon-edit',
		handler : function() {
				if(isValid('sys_page_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_page_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("pageService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new sys_page_callback(d,sys_page_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/pageForm.jsp',buttons,600,238,true,'编辑页面','sys_page_addForm',node);
}

//增加页面
function sys_page_add(){
	var buttons = [ {
		text : '新增',
		iconCls : 'icon-add',
		handler : function() {
				if(isValid('sys_page_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_page_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("pageService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_page_callback(d,sys_page_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	
	rainbowDialog.addDialog('./page/system/jsp/pageForm.jsp',buttons,600,238,true,'新增页面','sys_page_addForm');
}
//复制新增
var sys_page_copyAdd = function(){
	var nodes = $('#sys_page_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons =  [ {
			text : '新增',
			iconCls : 'icon-edit',
			handler : function() {
					if(isValid('sys_page_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_page_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("pageService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_page_callback(d,sys_page_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/pageForm.jsp',buttons,600,238,true,'新增页面','sys_page_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除页面
var sys_page_delete = function(code,name){
	$.messager.confirm('询问', '您确定要删除当前【'+name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("pageService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_page_callback(d,sys_page_reload));
		}
	});
};

//批量删除
var sys_page_datchDelete = function(){
	var nodes = $('#sys_page_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("pageService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_page_callback(null,sys_page_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

var sys_page_isCache = [{"key":0,"value":"否"},{"key":1,"value":"是"}];
var sys_pager_isCacheFormatter = function(value, row, index){
	for(var i=0; i<sys_page_isCache.length; i++){
		if (sys_page_isCache[i].key == value) 
			return sys_page_isCache[i].value;
	}
	return value;
};

//ajax回调处理
var sys_page_callback = function(dialog,relod){
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