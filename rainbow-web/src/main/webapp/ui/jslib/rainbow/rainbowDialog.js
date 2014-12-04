var rainbowDialog = {		
	/**
	 * 编辑弹出层
	 * @param pageUrl:页面路径,
	 * @param buttons:弹出层按钮,
	 * @param width:宽度,
	 * @param height:高度,
	 * @param isModal:是否模态窗口,
	 * @param title弹出框标题,
	 * @param formName:弹出层中的表单名称,
	 * @param node:传给表单的值对象json对象
	 */
	editDialog:function(pageUrl,buttons, width, height, isModal, title, formName,node){
		dialog(null,pageUrl,buttons, width, height, isModal, title, formName,node,'edit');
	},
	/**
	 * 新增弹出层
	 * @param pageUrl:页面路径,
	 * @param buttons:弹出层按钮,
	 * @param width:宽度,
	 * @param height:高度,
	 * @param isModal:是否模态窗口,
	 * @param title弹出框标题,
	 * @param formName:弹出层中的表单名称
	 */
	addDialog:function(pageUrl,buttons, width, height, isModal,title,formName){
		dialog(null,pageUrl,buttons, width, height, isModal, title ,formName,null);
	},
	/**
	 * 查看表单弹出层
	 * @param pageUrl:页面路径,
	 * @param buttons:弹出层按钮,
	 * @param width:宽度,
	 * @param height:高度,
	 * @param isModal:是否模态窗口,
	 * @param title弹出框标题
	 */
	showDialog:function(pageUrl,buttons, width, height, isModal, title, formName,node){
		dialog(null,pageUrl,buttons, width, height, isModal, title, formName,node,'show');
	},
	/**
	 * 普通弹出层
	 * @param pageUrl:页面路径,
	 * @param buttons:弹出层按钮,
	 * @param width:宽度,
	 * @param height:高度,
	 * @param isModal:是否模态窗口,
	 * @param title弹出框标题
	 */
	openDialog:function(id,pageUrl,buttons, width, height, isModal, title, formName,node){
		dialog(id,pageUrl,buttons, width, height, isModal, title,formName,node);
	},
	
	/**
	 * 主从关系弹出层
	 * @param pageUrl:页面路径,
	 * @param buttons:弹出层按钮,
	 * @param width:宽度,
	 * @param height:高度,
	 * @param isModal:是否模态窗口,
	 * @param title弹出框标题
	 */
	openDtlDialog:function(id,pageUrl,buttons, width, height, isModal, title, formName,node){
		dialog(id,pageUrl,buttons, width, height, isModal, title,formName,node);
	}
};



var dialog = function(id,pageUrl,buttons, width, height, isModal, title, formName,
		node,typle) {
	if(buttons == null || !(buttons instanceof Array)){
		buttons = [];
	}
	var modal = true;
	if (typeof (isModal) == "Boolean") {
		modal = isModal;
	}
	if (typeof (width) == "String") {
		width = parseInt(width);
	}
	if (typeof (height) == "String") {
		height = parseInt(height);
	}
	var div = '<div/>';
	if(id){
		div = '<div id='+id+'/>';
	}
	$(div).dialog({
		href : pageUrl,
		width : width,
		height : height,
		modal : modal,
		title : title,
		buttons :buttons,
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function(){
			if(typle == 'edit'){
				$('#' + formName).form('load', node);
			}else if(typle == 'show'){
				$('#' + formName).form('load', node);
				formDisable(formName);
			}
			$('#' + formName + ' input').css('background-color',$('#' + formName).css('background-color'));
		}
	});
};