/**
 * @author LeoChan
 * @date 2015/09/02
 * 
 */
/**
 * 验证的结果
 * 
 * @return
 */
function ValidateResult() {
	this.isPass = true;
	this.message = "";
}

/**
 * 验证规则对象，有一系列验证方法
 * 
 * 约定：所有的参数之间必须以'-'相连，不同的验证类型必须用'|'相隔
 * 
 * @return
 */
function ValidateRegulation() {}

/**
 * 把属于某种验证方式的验证参数从字符串转换为数组,包含的验证类型会被移除，仅仅剩下参数数组
 */
ValidateRegulation.convertArgsToArray = function(validateTypeStr) {
	var validateArgsArray=validateTypeStr.split("-");
	return validateTypeStr.split("-").slice(1);
}

/**
 * 必须性验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.required = function(value) {
	if(Object.prototype.toString.apply(value) === '[object Array]'){
		var validateResult = new ValidateResult();
		validateResult.isPass = !((value == null) || (value.length == 0));
		validateResult.message = "您必须选择一个值.";
		return validateResult;
	}else{
		value = $.trim(value);
		var validateResult = new ValidateResult();
		validateResult.isPass = !((value == null) || (jQuery.trim(value).length == 0));
		validateResult.message = "您还未输入值呢.";
		return validateResult;
	}
}

/**
 * 最小长度验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.minLength = function(value, validateTypeStr) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	var args = ValidateRegulation.convertArgsToArray(validateTypeStr);
	validateResult.isPass = !(value.length < parseInt(args[0]));
	validateResult.message = "输入长度必须大于" + args[0] + "当前长度为" + value.length;
	return validateResult;
}

/**
 * 最大长度验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.maxLength = function(value, validateTypeStr) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	var args = ValidateRegulation.convertArgsToArray(validateTypeStr);
	validateResult.isPass = !(value.length > parseInt(args[0]));
	validateResult.message = "输入长度必须小于" + args[0] + "当前长度为" + value.length;
	return validateResult;
}

/**
 * 长度范围内验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.lengthRange = function(value, validateTypeStr) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	var args = ValidateRegulation.convertArgsToArray(validateTypeStr);
	validateResult.isPass = (value.length >= parseInt(args[0]) && value.length <= parseInt(args[1]));
	validateResult.message = "输入长度必须在" + args[0] + "与" + args[1] + "之间,当前长度为"
			+ value.length;
	return validateResult;
}

/**
 * 数字验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.beDigits = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (!isNaN(value) && !/^\s+$/.test(value));
	validateResult.message = "只接受数字哦";
	return validateResult;
}

/**
 * 排斥中文字符
 */
ValidateRegulation.excludeCN = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (escape(value).indexOf("%u")<0);
	validateResult.message = "不接受中文哦";
	return validateResult;
}

/**
 * 两输入控件值对比验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.equals = function(value, validateTypeStr) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	var args = ValidateRegulation.convertArgsToArray(validateTypeStr);
	validateResult.isPass = (value == jQuery("#" + args[0]).val());
	validateResult.message = "两次输入不一致，请检查";
	return validateResult;
}

/**
 * 电话号码验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.phone = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (value == "" || /^((0[1-9]{3,4})?(0[12][0-9])?[-])?\d{6,8}$/
			.test(value));
	validateResult.message = "电话号码格式必须正确,如‘010-29392929’";
	return validateResult;
}

/**
 * 手机号码验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.mobilePhone = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (value == "" || /(^0?[1][358][0-9]{9}$)/
			.test(value));
	validateResult.message = "手机号码格式必须正确";
	return validateResult;
}

/**
 * email验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.email = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (value == "" || /\w{1,}[@][\w\-]{1,}([.]([\w\-]{1,})){1,3}$/
			.test(value));
	validateResult.message = "email格式必须正确";
	return validateResult;
}

/**
 * ip验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.ip = function(value) {
	value = jQuery.trim(value);
	var validateResult = new ValidateResult();
	validateResult.isPass = (value == "" || /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
			.test(value));
	validateResult.message = "ip格式必须正确";
	return validateResult;
}

/**
 * 文件验证，返回验证结果，是ValidateRegulation类的静态方法
 */
ValidateRegulation.file = function(value, validateTypeStr) {
	var args = ValidateRegulation.convertArgsToArray(validateTypeStr);
	var pass = false;
	var allowedType = "";
	$(args).each( function(i, arg) {
		if (new RegExp('\\.' + arg + '$', 'i').test(value)) {
			pass = true;
			return false;
		}
		allowedType = allowedType + arg + ",";
	});
	
	if(value=="")
	pass=true;

	var validateResult = new ValidateResult();
	validateResult.isPass = pass;
	validateResult.message = "只接受以下文件类型：" + allowedType;
	return validateResult;
}

/**
 * 根据方法名获得相应验证方法，自定义验证方法也支持
 */
ValidateRegulation.getFunc = function(methodName) {
	if (typeof (ValidateRegulation[methodName]) == "function") {
		return ValidateRegulation[methodName];
	} else if (typeof (window[methodName]) == "function") {
		return window[methodName];
	} else {
		alert("暂不支持此种验证！");
		return;
	}
}

/**
 * 将某元素上的验证方式字符串转化为数组
 */
Validator.getValidateTypeArray = function(validateTypeStr) {
	return validateTypeStr.split("|");
}


/**
 * 验证执行者
 * 
 * 约定：按钮中如果有‘submit=true’的属性，则该按钮为某区域的提交按钮
 * 约定：当该元素所属区域的‘update’属性为‘true’时，不忽略默认值，否则把默认值看做是空字符串
 * 约定：每进行一次验证动作，都需要验证全局表单，如果不希望某个控件的某个函数参与全局验证，可以在该控件上添加 noRepeatCheck="不希望参与全局验证的函数名"
 * 约定：默认对输入控件的blur和keyup事件进行绑定，如果希望自定义默认的触发验证的动作，请给指定控件添加 method="触发验证的动作"
 * 
 * @return
 */
function Validator(areaId,needDisable){
	this.area=jQuery("#"+areaId);
	this.needDisable = (needDisable==undefined?true:needDisable);
	this.j_elements=this.area.find("[validateType]");
	this.init();
}

/**
 * 对单个元素进行验证,显示验证错误
 */
Validator.prototype.validateField = function(element) {
	var j_element = jQuery(element);
	var result = this.validate(j_element);
}

/**
 * 对单个元素进行验证,仅仅获得验证结果，并不显示错误
 */
Validator.prototype.validateField4Pass = function(element) {
	var result;
	var j_element = jQuery(element);
	//某个验证方法不需要参与重复验证
	if(j_element.attr("noRepeatCheck")==undefined){
		var result = this.validate(j_element);
	}else{
		result = this.validate(j_element,j_element.attr("noRepeatCheck"));
	}
	this.removeInfo(j_element);
	if(!result.isPass){
		this.showInfo(j_element,result.message)
	}
	return result.isPass;
}

/**
 * 验证全局 确定是否开启提交按钮
 * @return
 */
Validator.prototype.traversalCheck = function(){
	    var isPass=true;
	    var validator = this;
		this.j_elements.each(function(i,element){
			//如果有一个元素未通过，则不再验证余下元素
			if(!isPass){
				return false;
			}
			isPass=validator.validateField4Pass(element);
		});
		
		return isPass;
}


/**
 * 初始化验证器
 * @return
 */
Validator.prototype.init = function(){
	var validator = this;
	//执行一次全局验证
}

/**
 * 处理元素默认值（如更新，或者是提醒用户该如何填写，都会给元素默认值）
 */
Validator.prototype.dealWithValue = function(j_element) {
	if (j_element.val() == j_element.attr("defaultValue")
			&& this.area.attr("update") != "true" && j_element.attr("update")!="true") {
		return "";
	} else {
		return j_element.val();
	}
}

/**
 * 验证一个元素是否能通过所有验证规则
 * @param j_element
 * @return
 */
Validator.prototype.validate = function(j_element,skipFuncName){
	var result;
	
	//处理元素值
	var elementValue=this.dealWithValue(j_element);
	var validateTypeArray = Validator.getValidateTypeArray(j_element.attr("validateType"));
	jQuery(validateTypeArray).each(function(i,validateTypeStr){
		//获得除去参数的验证类型
		var validateType=validateTypeStr.split("-")[0];
		//拿到代表该验证类型的验证函数对象
		var validateFunc=ValidateRegulation.getFunc(validateType);
		//需要跳过的函数
		if(skipFuncName==validateType){
			result = new ValidateResult();
			result.isPass = true;
		}else{
			//获得验证结果
			result=validateFunc(elementValue,validateTypeStr);
			if(!result.isPass){
				//跳出循环，不继续验证余下规则
				return false;
			}
		}
	});
	
	return result;
}


Validator.prototype.showInfo = function($obj,errormsg){
	var errorLabel = "<label name=\"errorLabel\" class=\"control-label\" for=\"inputError\"><i class=\"fa fa-times-circle-o\"></i>"+errormsg+"</label>";
		$obj.parent().append(errorLabel);
		$obj.parent().addClass("has-error");
}
Validator.prototype.removeInfo = function($obj,errormsg){
	if($obj.parent().find("[name='errorLabel']").length > 0){
		$obj.parent().find("[name='errorLabel']").remove();
		$obj.parent().removeClass("has-error");
	}
}


