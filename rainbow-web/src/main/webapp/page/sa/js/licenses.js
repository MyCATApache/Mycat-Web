//列操作
var caozuo = function(value, row, index) {
	return  formatString('<img title="查看" onclick="licenses_lookNode(\'{0}\');" src="{1}"/>&nbsp;<img  title="删除" onclick="licenses_delete(\'{2}\');" src="{3}"/>&nbsp;<a href="./licensesAction/download.do?guid={4}"  class="easyui-linkbutton" data-options="plain:true" style="float: center;" ><img border="0" title="证书下载"  src="{5}"/></a>', row.guid, './ui/style/images/extjs_icons/zoom/zoom.png', row.guid, './ui/style/images/extjs_icons/delete.png',row.guid, './ui/style/images/extjs_icons/disk_download.png');
};

var authorModels;
var encryptModels; 
var products;
var customers;
$.getJSON('./dispatcherAction/comboxCode.do?code=AUTHOR_MODEL',function(result){
	authorModels = result;
});
$.getJSON('./dispatcherAction/comboxCode.do?service=rsaService&method=queryCombox',function(result){
	encryptModels = result;
});
$.getJSON('./dispatcherAction/comboxCode.do?service=productService&method=queryCombox',function(result){
	products = result;
});
$.getJSON('./dispatcherAction/comboxCode.do?service=customerService&method=queryCombox',function(result){
	customers = result;
});

var authorModelsFormatter= function(value){
	for(var i=0; i<authorModels.length; i++){
		if (authorModels[i].value == value) return authorModels[i].text;
	}
	return value;
};

var encryptModelsFormatter= function(value){
	for(var i=0; i<encryptModels.length; i++){
		if (encryptModels[i].code == value) return encryptModels[i].name;
	}
	return value;
};
//刷新
var licenses_reload = function(){
	$('#licenses_datagrid').datagrid('load',{});
};

//快速查找
var licenses_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#licenses_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		licenses_reload();
	}
};

//查看操作
function licenses_lookNode(code){
	if (code != undefined) {
		$('#licenses_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#licenses_datagrid').datagrid('getSelected');
	$('#licenses_show').form('load',node);  
	formDisable("licenses_show");
	$('#licenses_dlg').dialog('open').dialog('setTitle','查看信息');  
}

//新增操作
function add_licenses(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('licenses_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#licenses_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("licensesService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new Callback(d,licenses_reload));
				}
			}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
	rainbowDialog.addDialog('./page/sa/jsp/licensesForm.jsp',buttons,400,520,true,'新增信息','licenses_addForm');	
}
//复制新增操作
var copy_add_licenses = function(){
	
	var node = $('#licenses_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('licenses_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#licenses_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("licensesService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new Callback(d,licenses_reload));
					}
				}
			},{
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					$(this).closest('.window-body').dialog('destroy');
				}
			}];
		rainbowDialog.editDialog('./page/sa/jsp/licensesForm.jsp',buttons,400,520,true,'编辑信息','licenses_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var licenses_delete = function(code){
	if (code != undefined) {
		$('#licenses_datagrid').datagrid('selectRecord', code);
	}
	var node = $('#licenses_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.licenseCode+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("licensesService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,licenses_reload));
		}
	});
};

//批量删除操作
var batch_delete_licenses = function(){
var nodes = $('#licenses_datagrid').datagrid('getChecked');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			rainbow.setService("licensesService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(null,licenses_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//查询过滤
var licenses_query = function(){
	var datas =serializeObject($('#licenses_queryForm'));
	$('#licenses_datagrid').datagrid('load',datas);
};

//清空查询条件
var licenses_query_clear = function(){
	$('#licenses_queryForm input').val('');
	licenses_reload();
};
