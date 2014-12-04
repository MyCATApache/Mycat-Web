//列操作
var sys_whereex_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_whereex_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_whereex_delete(\'{2}\');" src="{3}"/>',row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_whereex_reload = function(){
	$('#sys_whereex_datagrid').datagrid('clearSelections');
	$('#sys_whereex_datagrid').datagrid('reload',{});
};
var sys_whereex_commitRow = function(){
	$('#sys_whereex_datagrid').datagrid('acceptChanges');
	var nodes = $('#sys_whereex_datagrid').datagrid('getChecked');
	if(!nodes||nodes.length==0){
		return false;
	}
	var d = $(this).closest('.window-body');
	var rainbow = new Rainbow();
	rainbow.setRows(nodes);
	rainbow.setService("whereexService");
	rainbow.setMethod("update");
	rainbowAjax.excute(rainbow,new sys_whereex_callback(d,sys_whereex_reload));
	$('#sys_whereex_datagrid').datagrid('endEdit');
};

var sys_whereex_endEdite = function(rowIndex, rowData){
	$('#sys_whereex_datagrid').datagrid('cancelEdit',rowIndex);
};
//快速查找
var sys_whereex_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_whereex_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_whereex_reload();
	}
};



//修改操作
function sys_whereex_editNode(whereex){
	$('#sys_whereex_datagrid').datagrid('clearSelections');
	$('#sys_whereex_datagrid').datagrid('selectRecord', whereex);
	var node = $('#sys_whereex_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_whereex_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_whereex_addForm'),true);
			var rows = [];
			rows[0] = data;
			var rainbow = new Rainbow();
			rainbow.setRows(rows);
			rainbow.setService("whereexService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_whereex_callback(d,sys_whereex_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/whereexForm.jsp',buttons,650,550,true,'编辑信息','sys_whereex_addForm',node);
}


function sys_whereex_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_whereex_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_whereex_addForm'),true);
					var rows = [];
					rows[0] = data;
					var rainbow = new Rainbow();
					rainbow.setRows(rows);
					rainbow.setService("whereexService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_whereex_callback(d,sys_whereex_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/whereexForm.jsp',buttons,650,550,true,'新增信息','sys_whereex_addForm');
}


//复制新增操作
var sys_whereex_copyAdd = function(){
	var nodes = $('#sys_whereex_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_whereex_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_whereex_addForm'),true);
						var rows = [];
						rows[0] = data;
						var rainbow = new Rainbow();
						rainbow.setRows(rows);
						rainbow.setService("whereexService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_whereex_callback(d,sys_whereex_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/whereexForm.jsp',buttons,650,550,true,'编辑信息','sys_whereex_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作


var sys_whereex_delete = function(code){
	$.messager.confirm('询问', '您确定要删除当前选择的记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("whereexService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_whereex_callback(d,sys_whereex_reload));
		}
	});
};	


//批量删除操作
var sys_whereex_batchDelete = function(){
	var nodes = $('#sys_whereex_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("whereexService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_whereex_callback(null,sys_whereex_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//批量修改
var sys_whereex_update = function(){
	var rows = $('#sys_whereex_datagrid').datagrid('getRows');
	 for ( var i = 0; i < rows.length; i++) {
		 $('#sys_whereex_datagrid').datagrid('beginEdit', i);
	 }
};

//保存
var sys_whereex_save = function(){
	var rows = $('#sys_whereex_datagrid').datagrid('getRows');
	for ( var i = 0; i < rows.length; i++) {
		 $('#sys_whereex_datagrid').datagrid('endEdit', i);
	 }
	var changeRows = $('#sys_whereex_datagrid').datagrid('getChanges','updated');
	var rainbow = new Rainbow();
	rainbow.setRows(changeRows);
	rainbow.setService("whereexService");
	rainbow.setMethod("update");
	rainbowAjax.excute(rainbow,new sys_whereex_callback(null,sys_whereex_reload));
};

//ajax回调处理
var sys_whereex_callback = function(dialog,relod){
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
var sys_whereex_query = function(){
	var datas =serializeObject($('#sys_whereex_queryForm'));
	$('#sys_whereex_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_whereex_queryClear = function(){
	$('#sys_whereex_queryForm input').val('');
	sys_whereex_reload();
};