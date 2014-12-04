//列操作
var sys_ownermapdb_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_ownermapdb_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_ownermapdb_delete(\'{2}\');" src="{3}"/>', row.goodsownerid, './ui/style/images/extjs_icons/pencil.png', row.goodsownerid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_ownermapdb_reload = function(){
	$('#sys_ownermapdb_datagrid').datagrid('clearSelections');
	$('#sys_ownermapdb_datagrid').datagrid('reload',{});
};

//快速查找
var sys_ownermapdb_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_ownermapdb_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_ownermapdb_reload();
	}
};



//修改操作
function sys_ownermapdb_editNode(goodsownerid){
	if (goodsownerid != undefined) {
		$('#sys_ownermapdb_datagrid').datagrid('selectRecord', goodsownerid);
	}
	var node = $('#sys_ownermapdb_datagrid').datagrid('getSelections');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#sys_ownermapdb_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#sys_ownermapdb_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("ownermapdbService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new sys_ownermapdb_callback(d,sys_ownermapdb_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/ownermapdbForm.jsp?u=1',buttons,600,350,true,'编辑信息','sys_ownermapdb_addForm',node[0]);
}

//新增操作
function sys_ownermapdb_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_ownermapdb_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_ownermapdb_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("ownermapdbService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_ownermapdb_callback(d,sys_ownermapdb_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/ownermapdbForm.jsp',buttons,700,420,true,'新增信息','sys_ownermapdb_addForm');
}
//复制新增操作
var sys_ownermapdb_copyAdd = function(){
	var nodes = $('#sys_ownermapdb_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_ownermapdb_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_ownermapdb_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("ownermapdbService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_ownermapdb_callback(d,sys_ownermapdb_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/ownermapdbForm.jsp?u=1',buttons,600,350,true,'编辑信息','sys_ownermapdb_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_ownermapdb_delete = function(goodsownerid){
	$.messager.confirm('询问', '您确定要删除当前选择记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"goodsownerid":goodsownerid});
			rainbow.setService("ownermapdbService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_ownermapdb_callback(d,sys_ownermapdb_reload));
		}
	});
};

//批量删除操作
var sys_ownermapdb_batchDelete = function(){
	var nodes = $('#sys_ownermapdb_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("ownermapdbService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_ownermapdb_callback(null,sys_ownermapdb_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var sys_ownermapdb_callback = function(dialog,relod){
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
var sys_ownermapdb_query = function(){
	var datas =serializeObject($('#sys_ownermapdb_queryForm'));
	$('#sys_ownermapdb_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_ownermapdb_queryClear = function(){
	$('#sys_ownermapdb_queryForm input').val('');
	sys_ownermapdb_reload();
};