//列操作
var oc_table_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="oc_table_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="oc_table_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

var oc_table_type = [{"key":"default","value":"普通表"},{"key":"global","value":"全局表"}];

function oc_table_typeFormatter(value){
	for(var i=0; i<oc_table_type.length; i++){
		if (oc_table_type[i].key == value) return oc_table_type[i].value;
	}
	return value;
}

//展开
var oc_table_undo = function(){
	var node = $('#oc_table_datagrid').treegrid('getSelected');
	if (node) {
		$('#oc_table_datagrid').treegrid('collapseAll', node.cid);
	} else {
		$('#oc_table_datagrid').treegrid('collapseAll');
	}
};

//折叠
var oc_table_redo = function(){
	var node = $('#oc_table_datagrid').treegrid('getSelected');
	if (node) {
		$('#oc_table_datagrid').treegrid('expandAll', node.cid);
	} else {
		$('#oc_table_datagrid').treegrid('expandAll');
	}
};

//刷新
var oc_table_reload = function(){
	$('#oc_table_datagrid').treegrid('reload');
};

//查询
var oc_table_query = function(value,name){
	var rainbow = new Rainbow();
	if(value != null && value != ''){
		rainbow.setParam(name, value);
	}
	rainbow.setService("tableService");
	rainbow.setMethod("query");
	var callback = {
		onSuccess:function(data){
			try {
				if (data.success) {
					$('#oc_table_datagrid').treegrid('loadData',data.rows);
				}
				$.messager.show({
					title : '提示',
					msg : data.msg
				});
			} catch (e) {
				$.messager.alert('提示', "系统异常!");
			}
		},
		onFail:function(jqXHR, textStatus, errorThrown){
			$.messager.alert('提示', "系统异常!");
		}
	};
	rainbowAjax.query(rainbow,callback);
};

//菜单编辑事件
function oc_table_editNode(code){
	if (code != undefined) {
		$('#oc_table_datagrid').treegrid('select', code);
	}
	var node = $('#oc_table_datagrid').treegrid('getSelected');
	var form = null;
	if(node.type == 'global')form = 'oc_table_globalForm';
	else form = 'oc_table_defaultForm';
	buttons = [ {
		text : '编辑',
		iconCls : 'icon-edit',
		handler : function() {
				if(isValid('oc_table_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var form = $('#oc_table_formFlag').val();
					var data =serializeObject($('#' + form),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("tableService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new oc_table_callback(d,oc_table_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/oc/manager/jsp/tableForm.jsp',buttons,600,400,true,'编辑信息',form,node);
}

//增加菜单
function oc_table_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('oc_table_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var form = $('#oc_table_formFlag').val();
					var data =serializeObject($('#' + form),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("tableService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new oc_table_callback(d,oc_table_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/oc/manager/jsp/tableForm.jsp',buttons,600,400,true,'新增信息',null);
}


//删除菜单
var oc_table_delete = function(code){
	if (code != undefined) {
		$('#oc_table_datagrid').treegrid('select', code);
	}
	var node = $('#oc_table_datagrid').treegrid('getSelected');
	$.messager.confirm('询问', '您确定要删除【'+node.name+'】以及所有子节点？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows(node);
			rainbow.setService("tableService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_table_callback(d,oc_table_reload));
		}
	});
};

//ajax回调处理
var oc_table_callback = function(dialog,relod){
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