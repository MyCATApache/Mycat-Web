var rainbowAjax = {
	/**
	 * 发送get请求
	 * @param url:发送请求地址。
	 * @param data:待发送 Key/value 参数。
	 * @param callback:发送成功时回调函数。
	 */
	get:function(url,data,callback){
		$.get(url,data,callback);
	},
	/**
	 * 获取json对象
	 * @param url:发送请求地址。
	 * @param data:待发送 Key/value 参数。
	 * @param callback:发送成功时回调函数。
	 */
	getJSON:function(url,data,callback){
		$.getJSON(url,data,callback);
	},
	/**
	 * 发送post请求
	 * @param url:发送请求地址。
	 * @param data:待发送 Key/value 参数。
	 * @param callback:发送成功时回调函数。
	 * @param type:返回内容格式，xml, html, script, json, text, _default
	 */
	post:function(url,data,callback,type){
		$.post(url,data,callback,type);
	},
	/**
	 * 向平台发送query请求
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	query:function(rainbow, callback) {
		 sendCommand("./dispatcherAction/query.do",rainbow,"post",callback);
	},
	/**
	 * 向平台发送queryTree请求,一般用于treegrid数据查询
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	queryTree:function(rainbow, callback) {
		sendCommand("./dispatcherAction/queryTree.do",rainbow,"post",callback);
	},
	/**
	 * 向平台发送excute请求,一般用于新增、修改、删除操作
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	excute:function(rainbow, callback) {
		sendCommand("./dispatcherAction/execute.do",rainbow,"post",callback);
	},
	/**
	 * 向平台发送queryTree请求,一般用于treegrid数据查询
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	queryComboxTree:function(rainbow, callback) {
		sendCommand("./dispatcherAction/queryComboxTree.do",rainbow,"post",callback);
	},
	/**
	 * 向平台发送comboxCode请求,一般用于下拉框动态取数据
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	comboxCode:function(codeValue, callback) {
		$.get('./dispatcherAction/query.do?service=codeService&method=query&code='+codeValue,callback);
	},
	/**
	 * 向平台发送url请求,一般用于下拉框动态取数据
	 * @param url 请求地址
	 * @param rainbow 数据对象
	 * @param callback 发送成功时回调函数。
	 */
	url:function(url,rainbow, callback) {
		sendCommand(url,rainbow,"post",callback);
	}
};

//发送ajax请求,参数:请求地址,rainbow对象结构,请求模式(post,get),回调函数
var sendCommand = function(url, rainbow, mothod, callback) {
	if (mothod == null || mothod == undefined) {
		mothod = "post";
	}
	if (rainbow == null) {
		rainbow = new Rainbow();
	}
	if (!rainbow instanceof Rainbow) {
		return;
	}
	$.ajax({
		url : url,
		data : $.parseJSON(JSON.stringify(rainbow)),
		type : mothod,
		datatype : 'json',
		success : function(data) {
			if ((callback != null) && typeof (callback.onSuccess) == "function") {
				if (typeof (callback.onSuccess) == "function") {
					callback.onSuccess(data);
				}
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (typeof (callback) == "object") {
				if ((callback != null)
						&& typeof (callback.onFail) == "function")
					callback.onFail(jqXHR, textStatus, errorThrown);
			}
		}
	});
};

//ajax回调处理
var Callback = function(dialog,relod){
		this.onSuccess=function(data){
			alert(data.success);
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


(function ($) {
    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }
})(jQuery);

$.ajaxSetup({
	cache : false,
	contentType : "application/x-www-form-urlencoded;charset=utf-8",
	complete : function(XMLHttpRequest, textStatus) {
		 // 通过XMLHttpRequest取得响应头，sessionstatus
		var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
		if (sessionstatus == "timeout") {
		   	$(".content-wrapper").load("./login.html",function(response,status,xhr){
		   		$(".content").resize(function(){});
		   	});
		}
	}
});
