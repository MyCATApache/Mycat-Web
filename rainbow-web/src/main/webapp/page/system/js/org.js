//操作
var sys_org_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="sys_org_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="sys_org_delete(\'{2}\');" src="{3}"/>', row.orgCode, './ui/style/images/extjs_icons/pencil.png', row.orgCode, './ui/style/images/extjs_icons/delete.png');
};

//展开
var sys_org_undo = function(){
	var node = $('#sys_org_treegrid').treegrid('getSelected');
	if (node) {
		$('#sys_org_treegrid').treegrid('collapseAll', node.cid);
	} else {
		$('#sys_org_treegrid').treegrid('collapseAll');
	}
};

//折叠
var sys_org_redo = function(){
	var node = $('#sys_org_treegrid').treegrid('getSelected');
	if (node) {
		$('#sys_org_treegrid').treegrid('expandAll', node.cid);
	} else {
		$('#sys_org_treegrid').treegrid('expandAll');
	}
};

//刷新
var sys_org_reload = function(){
	$('#sys_org_treegrid').treegrid('reload');
};

//查询
var sys_org_query = function(value,name){
	var rainbow = new Rainbow();
	if(value != null && value != ''){
		rainbow.setParam(name, value);
	}else{
		sys_org_reload();
		return;
	}
	rainbow.setService("orgService");
	rainbow.setMethod("query");
	var callback = {
		onSuccess:function(data){
			try {
				if (data.success) {
					$('#sys_org_treegrid').treegrid('loadData',data.rows);
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



var orgAlive = [{"key":0,"value":"有效"},{"key":1,"value":"无效"}];
var orgAliveFormatter = function(value){
	for(var i=0; i<orgAlive.length; i++){
		if (orgAlive[i].key == value) return orgAlive[i].value;
	}
	return value;
};


//修改操作
function sys_org_editNode(code){
	if (code != undefined) {
		$('#sys_org_treegrid').treegrid('select', code);
	}
	var node = $('#sys_org_treegrid').treegrid('getSelected');
	
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_org_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_org_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("orgService");
					rainbow.setMethod("update");
					rainbowAjax.excute(rainbow,new sys_org_callback(d,sys_org_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/system/jsp/orgForm.jsp',buttons,600,300,true,'编辑信息','sys_org_addForm',node);
}

//新增操作
function sys_org_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
				if(isValid('sys_org_addForm') ==  true){
					var d = $(this).closest('.window-body');
					var data =serializeObject($('#sys_org_addForm'),true);
					var rainbow = new Rainbow();
					rainbow.setAttr(data);
					rainbow.setService("orgService");
					rainbow.setMethod("insert");
					rainbowAjax.excute(rainbow,new sys_org_callback(d,sys_org_reload));
				}
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/system/jsp/orgForm.jsp',buttons,600,300,true,'新增信息','sys_org_addForm');
}

//复制新增操作
var copy_add_org = function(){
	var node = $('#sys_org_datagrid').datagrid('getSelected');
	if(node){
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				var d = $(this).closest('.window-body');
				var data =serializeObject($('#sys_org_addForm'),true);
				var rainbow = new Rainbow();
				rainbow.setAttr(data);
				rainbow.setService("orgService");
				rainbow.setMethod("insert");
				rainbowAjax.excute(rainbow,new sys_org_callback(d,sys_org_reload));
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/sa/jsp/orgForm.jsp',buttons,600,350,true,'编辑信息','sys_org_addForm',node);
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};

//删除操作
var sys_org_delete = function(code){
	if (code != undefined) {
		$('#sys_org_treegrid').treegrid('select', code);
	}
	var node = $('#sys_org_treegrid').treegrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.orgName+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows(node);
			rainbow.setService("orgService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new sys_org_callback(d,sys_org_reload));
		}
	});
};

//ajax回调处理
var sys_org_callback = function(dialog,relod){
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