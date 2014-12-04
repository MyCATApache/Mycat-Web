//列操作
var caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="${jspName}_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="${jspName}_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var ${jspName}_reload = function(){
	$('#${jspName}_datagrid').datagrid('load',{});
};

//快速查找
var ${jspName}_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#${jspName}_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		${jspName}_reload();
	}
};


//ajax回调处理
var Callback = function(dialog){
		this.onSuccess=function(data){
			try {
				if (data.success) {
					${jspName}_reload();
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

//修改操作
function ${jspName}_editNode(${jspName}){
	if (${jspName} != undefined) {
		$('#${jspName}_datagrid').datagrid('selectRecord', ${jspName});
	}
	var node = $('#${jspName}_datagrid').datagrid('getSelected');
	
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#${jspName}_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#${jspName}_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new Callback(d,${jspName}_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,400,350,true,'编辑信息','${jspName}_addForm',node);
}

//新增操作
function add_${jspName}(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('${jspName}_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#${jspName}_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("${jspName}Service");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new Callback(d,${jspName}_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,400,350,true,'新增信息','${jspName}_addForm');
}
//复制新增操作
var copy_add_${jspName} = function(){
	var node = $('#${jspName}_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('${jspName}_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#${jspName}_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("${jspName}Service");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new Callback(d,${jspName}_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,400,350,true,'编辑信息','${jspName}_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var ${jspName}_delete = function(${jspName}){
	if (${jspName} != undefined) {
		$('#${jspName}_datagrid').datagrid('selectRecord', ${jspName});
	}
	var node = $('#${jspName}_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.text+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":${jspName}});
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(d,${jspName}_reload));
		}
	});
};

//批量删除操作
var batch_delete_${jspName} = function(){
var nodes = $('#${jspName}_datagrid').datagrid('getChecked');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new Callback(null,${jspName}_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//查询过滤
var ${jspName}_query = function(){
	var datas =serializeObject($('#${jspName}_queryForm'));
	$('#${jspName}_datagrid').datagrid('load',datas);
};

//清空查询条件
var ${jspName}_query_clear = function(){
	$('#${jspName}_queryForm input').val('');
	${jspName}_reload();
};