//列操作
var ${modelName}_${jspName}_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="${modelName}_${jspName}_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="${modelName}_${jspName}_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var ${modelName}_${jspName}_reload = function(){
	$('#${modelName}_${jspName}_datagrid').datagrid('clearSelections');
	$('#${modelName}_${jspName}_datagrid').datagrid('reload',{});
};

//快速查找
var ${modelName}_${jspName}_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#${modelName}_${jspName}_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		${modelName}_${jspName}_reload();
	}
};



//修改操作
function ${modelName}_${jspName}_editNode(${jspName}){
	$('#${modelName}_${jspName}_datagrid').datagrid('clearSelections');
	$('#${modelName}_${jspName}_datagrid').datagrid('selectRecord', ${jspName});
	var node = $('#${modelName}_${jspName}_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#${modelName}_${jspName}_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#${modelName}_${jspName}_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new ${modelName}_${jspName}_callback(d,${modelName}_${jspName}_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,600,350,true,'编辑信息','${modelName}_${jspName}_addForm',node);
}

//新增操作
function ${modelName}_${jspName}_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('${modelName}_${jspName}_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#${modelName}_${jspName}_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("${jspName}Service");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new ${modelName}_${jspName}_callback(d,${modelName}_${jspName}_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,600,350,true,'新增信息','${modelName}_${jspName}_addForm');
}
//复制新增操作
var ${modelName}_${jspName}_copyAdd = function(){
	var nodes = $('#${modelName}_${jspName}_datagrid').datagrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('${modelName}_${jspName}_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#${modelName}_${jspName}_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("${jspName}Service");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new ${modelName}_${jspName}_callback(d,${modelName}_${jspName}_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/${modelName}/jsp/${jspName}Form.jsp',buttons,600,350,true,'编辑信息','${modelName}_${jspName}_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var ${modelName}_${jspName}_delete = function(guid){
	$.messager.confirm('询问', '您确定要删除当前记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new ${modelName}_${jspName}_callback(d,${modelName}_${jspName}_reload));
		}
	});
};

//批量删除操作
var ${modelName}_${jspName}_batchDelete = function(){
	var nodes = $('#${modelName}_${jspName}_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("${jspName}Service");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new ${modelName}_${jspName}_callback(null,${modelName}_${jspName}_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var ${modelName}_${jspName}_callback = function(dialog,relod){
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
var ${modelName}_${jspName}_query = function(){
	var datas =serializeObject($('#${modelName}_${jspName}_queryForm'));
	$('#${modelName}_${jspName}_datagrid').datagrid('load',datas);
};

//清空查询条件
var ${modelName}_${jspName}_queryClear = function(){
	$('#${modelName}_${jspName}_queryForm input').val('');
	${modelName}_${jspName}_reload();
};