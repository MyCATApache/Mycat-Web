//列操作
var oc_host_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="oc_host_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="oc_host_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

var oc_host_state = [{"key":"closed","value":"主服务"},{"key":"open","value":"从服务"}];

function oc_host_stateFormatter(value){
	for(var i=0; i<oc_host_state.length; i++){
		if (oc_host_state[i].key == value) return oc_host_state[i].value;
	}
	return value;
}

//展开
var oc_host_undo = function(){
	var node = $('#oc_host_datagrid').treegrid('getSelected');
	if (node) {
		$('#oc_host_datagrid').treegrid('collapseAll', node.cid);
	} else {
		$('#oc_host_datagrid').treegrid('collapseAll');
	}
};

//折叠
var oc_host_redo = function(){
	var node = $('#oc_host_datagrid').treegrid('getSelected');
	if (node) {
		$('#oc_host_datagrid').treegrid('expandAll', node.cid);
	} else {
		$('#oc_host_datagrid').treegrid('expandAll');
	}
};

//刷新
var oc_host_reload = function(){
	$('#oc_host_datagrid').treegrid('reload');
};

//查询
var oc_host_query = function(value,name){
	var rainbow = new Rainbow();
	if(value != null && value != ''){
		rainbow.setParam(name, value);
	}
	rainbow.setService("hostService");
	rainbow.setMethod("query");
	var callback = {
		onSuccess:function(data){
			try {
				if (data.success) {
					$('#oc_host_datagrid').treegrid('loadData',data.rows);
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
function oc_host_editNode(code){
	if (code != undefined) {
		$('#oc_host_datagrid').treegrid('select', code);
	}
	var node = $('#oc_host_datagrid').treegrid('getSelected');

	buttons = [ {
		text : '编辑',
		iconCls : 'icon-edit',
		handler : function() {
				if(isValid('oc_host_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#oc_host_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("hostService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new oc_host_callback(d,oc_host_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/oc/manager/jsp/hostForm.jsp',buttons,600,320,true,'编辑信息','oc_host_addForm',node);
}

//增加菜单
function oc_host_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('oc_host_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#oc_host_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("hostService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new oc_host_callback(d,oc_host_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/oc/manager/jsp/hostForm.jsp',buttons,600,320,true,'新增信息','oc_host_addForm');
}


//删除菜单
var oc_host_delete = function(code){
	if (code != undefined) {
		$('#oc_host_datagrid').treegrid('select', code);
	}
	var node = $('#oc_host_datagrid').treegrid('getSelected');
	$.messager.confirm('询问', '您确定要删除【'+node.host+'】以及所有子节点？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows(node);
			rainbow.setService("hostService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_host_callback(d,oc_host_reload));
		}
	});
};

//ajax回调处理
var oc_host_callback = function(dialog,relod){
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