//列操作
var oc_schema_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="oc_schema_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="oc_schema_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var oc_schema_reload = function(){
	$('#oc_schema_datagrid').datagrid('clearSelections');
	$('#oc_schema_datagrid').datagrid('reload',{});
};

//快速查找
var oc_schema_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#oc_schema_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		oc_schema_reload();
	}
};



//修改操作
function oc_schema_editNode(schema){
	$('#oc_schema_datagrid').datagrid('clearSelections');
	$('#oc_schema_datagrid').datagrid('selectRecord', schema);
	var node = $('#oc_schema_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#oc_schema_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#oc_schema_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("schemaService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new oc_schema_callback(d,oc_schema_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/oc/manager/jsp/schemaForm.jsp',buttons,600,350,true,'编辑信息','oc_schema_addForm',node);
}

//新增操作
function oc_schema_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('oc_schema_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#oc_schema_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("schemaService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new oc_schema_callback(d,oc_schema_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/oc/manager/jsp/schemaForm.jsp',buttons,600,350,true,'新增信息','oc_schema_addForm');
}
//复制新增操作
var oc_schema_copyAdd = function(){
	var nodes = $('#oc_schema_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('oc_schema_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#oc_schema_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("schemaService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new oc_schema_callback(d,oc_schema_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/oc/manager/jsp/schemaForm.jsp',buttons,600,350,true,'编辑信息','oc_schema_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var oc_schema_delete = function(guid){
	$('#oc_schema_datagrid').datagrid('clearSelections');
	$('#oc_schema_datagrid').datagrid('selectRecord',guid);
	var node = $('#oc_schema_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("schemaService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_schema_callback(d,oc_schema_reload));
		}
	});
};

//批量删除操作
var oc_schema_batchDelete = function(){
	var nodes = $('#oc_schema_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("schemaService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_schema_callback(null,oc_schema_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var oc_schema_callback = function(dialog,relod){
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
var oc_schema_query = function(){
	var datas =serializeObject($('#oc_schema_queryForm'));
	$('#oc_schema_datagrid').datagrid('load',datas);
};

//清空查询条件
var oc_schema_query_clear = function(){
	$('#oc_schema_queryForm input').val('');
	oc_schema_reload();
};