//列操作
var oc_connection_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="oc_connection_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="oc_connection_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var oc_connection_reload = function(){
	$('#oc_connection_datagrid').datagrid('clearSelections');
	$('#oc_connection_datagrid').datagrid('reload',{});
};

//快速查找
var oc_connection_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#oc_connection_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		oc_connection_reload();
	}
};



//修改操作
function oc_connection_editNode(datahost){
	$('#oc_connection_datagrid').datagrid('clearSelections');
	$('#oc_connection_datagrid').datagrid('selectRecord', datahost);
	var node = $('#oc_connection_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#oc_connection_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#oc_connection_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("datahostService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new oc_connection_callback(d,oc_connection_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/oc/manager/jsp/datahostForm.jsp',buttons,600,320,true,'选择物理机','oc_connection_addForm',node);
}

//新增操作
function oc_connection_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('oc_connection_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#oc_connection_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("datahostService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new oc_connection_callback(d,oc_connection_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/oc/manager/jsp/datahostForm.jsp',buttons,600,320,true,'新增信息','oc_connection_addForm');
}
//复制新增操作
var oc_connection_copyAdd = function(){
	var nodes = $('#oc_connection_datagrid').datagrid('getChecked');
	if(nodes && nodes.length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('oc_connection_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#oc_connection_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("datahostService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new oc_connection_callback(d,oc_connection_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/oc/manager/jsp/datahostForm.jsp',buttons,600,320,true,'编辑信息','oc_connection_addForm',nodes[0]);
		
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var oc_connection_delete = function(guid){
	$('#oc_connection_datagrid').datagrid('clearSelections');
	$('#oc_connection_datagrid').datagrid('selectRecord',guid);
	var node = $('#oc_connection_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("datahostService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_connection_callback(d,oc_connection_reload));
		}
	});
};

//批量删除操作
var oc_connection_batchDelete = function(){
	var nodes = $('#oc_connection_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("datahostService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_connection_callback(null,oc_connection_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var oc_connection_callback = function(dialog,relod){
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
var oc_connection_query = function(){
	var datas =serializeObject($('#oc_connection_queryForm'));
	$('#oc_connection_datagrid').datagrid('load',datas);
};

//清空查询条件
var oc_connection_query_clear = function(){
	$('#oc_connection_queryForm input').val('');
	oc_connection_reload();
};