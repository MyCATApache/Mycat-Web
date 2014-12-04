//列操作
var caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="rsa_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="rsa_delete(\'{2}\')" src="{3}"/>&nbsp;<img title="查看" onclick="rsa_check(\'{4}\');" src="{5}"/>', 
	row.guid,'./ui/style/images/extjs_icons/pencil.png', 
	row.guid,'./ui/style/images/extjs_icons/delete.png',
	row.guid,'./ui/style/images/extjs_icons/zoom/zoom.png');
};

//刷新
var rsa_reload = function(){
	$('#rsa_datagrid').datagrid('load',{});
};

//快速查找
var rsa_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#rsa_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		rsa_reload();
	}
};


//修改操作
function rsa_editNode(code){
	if (code != undefined) {
		$('#rsa_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#rsa_datagrid').datagrid('getSelected');
	
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#rsa_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#rsa_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("rsaService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new Callback(d,rsa_reload));
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.editDialog('./page/sa/jsp/rsaForm.jsp',buttons,500,350,true,'编辑信息','rsa_addForm',node);
}

//新增操作
function add_rsa(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('rsa_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#rsa_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("rsaService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new Callback(d,rsa_reload));
				}
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.addDialog('./page/sa/jsp/rsaForm.jsp',buttons,500,350,true,'新增信息','rsa_addForm');
}
//复制新增操作
var copy_add_rsa = function(){
	var node = $('#rsa_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('rsa_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#rsa_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("rsaService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new Callback(d,rsa_reload));
					}
				}
			},{
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					$(this).closest('.window-body').dialog('destroy');
				}
			}];
		rainbowDialog.editDialog('./page/sa/jsp/rsaForm.jsp',buttons,500,350,true,'编辑信息','rsa_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var rsa_delete = function(code){
	if (code != undefined) {
		$('#rsa_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#rsa_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("rsaService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,rsa_reload));
		}
	});
};

//批量删除操作
var batch_delete_rsa = function(){
var nodes = $('#rsa_datagrid').datagrid('getChecked');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
				$.messager.progress();
				rainbow.setService("rsaService");
				rainbow.setMethod("delete");
				rainbowAjax.excute(rainbow,new Callback(null,rsa_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//删除操作
var rsa_delete = function(code){
	if (code != undefined) {
		$('#rsa_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#rsa_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("rsaService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,rsa_reload));
		}
	});
};

//查看
function rsa_check(code){
	if (code != undefined) {
		$('#rsa_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#rsa_datagrid').datagrid('getSelected');
	
	rainbowDialog.showDialog('./page/sa/jsp/rsaForm.jsp',null,500,350,true,'查看信息','rsa_addForm',node);
}

//查询过滤
var rsa_query = function(){
	var datas =serializeObject($('#rsa_queryForm'));
	$('#rsa_datagrid').datagrid('load',datas);
};

//清空查询条件
var rsa_query_clear = function(){
	$('#rsa_queryForm input').val('');
	rsa_reload();
};