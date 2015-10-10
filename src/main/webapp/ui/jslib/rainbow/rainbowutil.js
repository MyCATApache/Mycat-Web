/**
 * 验证form
 * @param form 表单名称
 */
var isValid = function(form){
	var isValid = $('#'+form).form('validate');
	if (!isValid){
		return false;
	}else{
		return true;
	}
};

//这是Form表达为只读
var formDisable = function(formName){
	$('#' + formName + ' *').attr("disabled", "disabled");
    $("#" + formName + " .easyui-numberbox").numberbox("disable");
    $("#" + formName + " .easyui-datebox").datebox("disable");
    $("#" + formName + " .easyui-combobox").combobox("disable");
    $("#" + formName + " .easyui-combotree").combotree("disable");
};