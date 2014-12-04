
//列操作
var caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="customer_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="customer_delete(\'{2}\')" src="{3}"/>&nbsp;<img title="查看" onclick="customer_check(\'{4}\');" src="{5}"/>', 
	row.guid,'./ui/style/images/extjs_icons/pencil.png', 
	row.guid,'./ui/style/images/extjs_icons/delete.png',
	row.guid,'./ui/style/images/extjs_icons/zoom/zoom.png');
};

//刷新
var customer_reload = function(){
	$('#customer_datagrid').datagrid('load',{});
};

//快速查找
var customer_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#customer_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		customer_reload();
	}
};

//修改操作
function customer_editNode(code){
	if (code != undefined) {
		$('#customer_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#customer_datagrid').datagrid('getSelected');

	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#customer_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#customer_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("customerService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new Callback(d,customer_reload));
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.editDialog('./page/sa/jsp/customerForm.jsp',buttons,400,350,true,'编辑信息','customer_addForm',node);
}

//新增操作
function add_customer(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('customer_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#customer_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("customerService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new Callback(d,customer_reload));
				}
			}
			},{
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					$(this).closest('.window-body').dialog('destroy');
				}
			}];
	rainbowDialog.addDialog('./page/sa/jsp/customerForm.jsp',buttons,400,350,true,'新增信息','customer_addForm');
}
//复制新增操作
var copy_add_customer = function(){
	var node = $('#customer_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('customer_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#customer_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("customerService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new Callback(d,customer_reload));
					}
				}
			},{
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					$(this).closest('.window-body').dialog('destroy');
				}
			}];
		rainbowDialog.editDialog('./page/sa/jsp/customerForm.jsp',buttons,400,350,true,'编辑信息','customer_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var customer_delete = function(code){
	if (code != undefined) {
		$('#customer_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#customer_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("customerService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,customer_reload));
		}
	});
};

//批量删除操作
var batch_delete_customer = function(){
var nodes = $('#customer_datagrid').datagrid('getChecked');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
				$.messager.progress();
				rainbow.setService("customerService");
				rainbow.setMethod("delete");
				rainbowAjax.excute(rainbow,new Callback(null,customer_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//查看
function customer_check(code){
	if (code != undefined) {
		$('#customer_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#customer_datagrid').datagrid('getSelected');
	
	rainbowDialog.showDialog('./page/sa/jsp/customerForm.jsp',null,400,350,true,'查看信息','customer_addForm',node);
}


//查询过滤
var customer_query = function(){
	var datas =serializeObject($('#customer_queryForm'));
	$('#customer_datagrid').datagrid('load',datas);
};

//清空查询条件
var customer_query_clear = function(){
	$('#customer_queryForm input').val('');
	customer_reload();
};

