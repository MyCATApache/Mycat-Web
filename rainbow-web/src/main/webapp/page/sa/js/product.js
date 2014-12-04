//列操作
var caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="product_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="product_delete(\'{2}\')" src="{3}"/>&nbsp;<img title="查看" onclick="product_check(\'{4}\');" src="{5}"/>', 
	row.guid,'./ui/style/images/extjs_icons/pencil.png', 
	row.guid,'./ui/style/images/extjs_icons/delete.png',
	row.guid,'./ui/style/images/extjs_icons/zoom/zoom.png');
};

var productTypes =[];
var bizFields =[];

$.getJSON("./dispatcherAction/comboxCode.do?code=PRODUCT_TYPE",function(data){
		productTypes = data;
	}
);

$.getJSON("./dispatcherAction/comboxCode.do?code=BIZ_FIELD",function(data){
		bizFields = data;
	}
);

var farmatterProductType = function(value){
	for(var i=0; i<productTypes.length; i++){
		if (productTypes[i].value == value) return productTypes[i].text;
	}
	return value;
};

var farmatterBizField = function(value){
	for(var i=0; i<bizFields.length; i++){
		if (bizFields[i].value == value) return bizFields[i].text;
	}
	return value;
};

//刷新
var product_reload = function(){
	$('#product_datagrid').datagrid('load',{});
};

//快速查找
var product_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#product_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		product_reload();
	}
};

//修改操作
function product_editNode(code){
	if (code != undefined) {
		$('#product_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#product_datagrid').datagrid('getSelected');

	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#product_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#product_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("productService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new Callback(d,product_reload));
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.editDialog('./page/sa/jsp/productForm.jsp',buttons,400,550,true,'编辑信息','product_addForm',node);
}

//新增操作
function add_product(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('product_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#product_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("productService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new Callback(d,product_reload));
				}
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.addDialog('./page/sa/jsp/productForm.jsp',buttons,400,550,true,'新增信息','product_addForm');
}
//复制新增操作
var copy_add_product = function(){
	var node = $('#product_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('product_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#product_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("productService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new Callback(d,product_reload));
					}
				}
			},{
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					$(this).closest('.window-body').dialog('destroy');
				}
			}];
		rainbowDialog.editDialog('./page/sa/jsp/productForm.jsp',buttons,400,550,true,'编辑信息','product_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var product_delete = function(code){
	if (code != undefined) {
		$('#product_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#product_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.productName+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("productService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,product_reload));
		}
	});
};

//批量删除操作
var batch_delete_product = function(){
var nodes = $('#product_datagrid').datagrid('getChecked');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			isValid();
			rainbow.setService("productService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(null,product_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//查看
function product_check(code){
	if (code != undefined) {
		$('#product_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#product_datagrid').datagrid('getSelected');
	
	rainbowDialog.showDialog('./page/sa/jsp/productForm.jsp',null,400,520,true,'查看信息','product_addForm',node);
}

//查询过滤
var product_query = function(){
	var datas =serializeObject($('#product_queryForm'));
	$('#product_datagrid').datagrid('load',datas);
};

//清空查询条件
var product_query_clear = function(){
	$('#product_queryForm').form('clear');  
	product_reload();
};

var shouquan = function(value, row, index){
	return formatString('<a href="javascript:void(0);" onclick="authorization(\'{0}\');" class="easyui-linkbutton" data-options="plain:true" style="float: center;" >授权</a>&nbsp;<a href="javascript:void(0);" onclick="app_editNode(\'{1}\');" class="easyui-linkbutton" data-options="plain:true" style="float: center;" >查看</a>', row.guid,row.guid);
	
};

var authorization = function(code){
	rainbowDialog.openDialog(null,'./page/sa/jsp/productAuthorization.jsp?guid='+code,null,800,400,true,'授权管理');
};