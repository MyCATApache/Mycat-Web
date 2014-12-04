//列操作
var sys_user_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_user_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_user_delete(\'{2}\',\'{3}\');" src="{4}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, row.name, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var sys_user_reload = function(){
	$('#sys_user_datagrid').datagrid('clearSelections');
	$('#sys_user_datagrid').datagrid('reload',{});
};

//快速查找
var sys_user_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#sys_user_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		sys_user_reload();
	}
};

var sys_user_sex = [{"key":0,"value":"男"},{"key":1,"value":"女"}];
var sys_user_aliveFlag = [{"key":0,"value":"有效"},{"key":1,"value":"无效"}];
var sys_user_workStatus = [{"key":0,"value":"在职"},{"key":1,"value":"离职"}];
var sys_user_isOnline = [{"key":0,"value":"不在线"},{"key":1,"value":"在线"}];
var sys_user_isVerify = [{"key":0,"value":"否"},{"key":1,"value":"是"}];

function sys_user_sexFormatter(value){
	for(var i=0; i<sys_user_sex.length; i++){
		if (sys_user_sex[i].key == value) return sys_user_sex[i].value;
	}
	return value;
}

function sys_user_aliveFlagFormatter(value){
	for(var i=0; i<sys_user_aliveFlag.length; i++){
		if (sys_user_aliveFlag[i].key == value) return sys_user_aliveFlag[i].value;
	}
	return value;
}

function sys_user_workStatusFormatter(value){
	for(var i=0; i<sys_user_workStatus.length; i++){
		if (sys_user_workStatus[i].key == value) return sys_user_workStatus[i].value;
	}
	return value;
}

function sys_user_isOnlineFormatter(value){
	for(var i=0; i<sys_user_isOnline.length; i++){
		if (sys_user_isOnline[i].key == value) return sys_user_isOnline[i].value;
	}
	return value;
}
function sys_user_isVerifyFormatter(value){
	for(var i=0; i<sys_user_isVerify.length; i++){
		if (sys_user_isVerify[i].key == value) return sys_user_isVerify[i].value;
	}
	return value;
}



//修改操作
function sys_user_editNode(code){
	$('#sys_user_datagrid').datagrid('clearSelections');
	$('#sys_user_datagrid').datagrid('selectRecord', code);
	var node = $('#sys_user_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_user_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_user_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("userService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new sys_user_callback(d,sys_user_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/userForm.jsp',buttons,600,490,true,'编辑信息','sys_user_addForm',node);
}

//新增操作
function sys_user_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {	
				if(isValid('sys_user_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_user_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("userService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_user_callback(d,sys_user_reload));
				}			
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/userForm.jsp',buttons,600,490,true,'新增信息','sys_user_addForm');
}
//复制新增操作
var sys_user_copyAdd = function(){
	var nodes = $('#sys_user_datagrid').treegrid('getSelections');
	var length = nodes.length;
	if(length > 0){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-edit',
			handler : function() {
					if(isValid('sys_user_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_user_addForm'),true);
						var rainbow = new Rainbow();
						rainbow.setAttr(data);
						rainbow.setService("userService");
						rainbow.setMethod("insert");
						rainbowAjax.excute(rainbow,new sys_user_callback(d,sys_user_reload));
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/userForm.jsp',buttons,600,490,true,'新增信息','sys_user_addForm',nodes[length-1]);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_user_delete = function(code,name){
	$.messager.confirm('询问', '您确定要删除当前【'+name+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":code});
			rainbow.setService("userService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_user_callback(d,sys_user_reload));
		}
	});
};

//批量删除操作
var sys_user_batchDelete = function(){
	var nodes = $('#sys_user_datagrid').treegrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("userService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_user_callback(null,sys_user_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//ajax回调处理
var sys_user_callback = function(dialog,relod){
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
var sys_user_query = function(){
	var datas =serializeObject($('#sys_user_queryForm'));
	$('#sys_user_datagrid').datagrid('load',datas);
};

//清空查询条件
var sys_user_query_clear = function(){
	$('#sys_user_queryForm input').val('');
	sys_user_reload();
};