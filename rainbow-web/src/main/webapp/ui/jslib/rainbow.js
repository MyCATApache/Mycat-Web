var Rainbow = function() {
	this.service = null;
	this.method = null;
	this.rows = [];
	this.attr = Object();

	Rainbow.prototype.setService = function(name) {
		this.service = name;
	};
	Rainbow.prototype.getService = function() {
		return this.service;
	};

	Rainbow.prototype.setMethod = function(name) {
		this.method = name;
	};
	Rainbow.prototype.getMethod = function() {
		return this.method;
	};

	Rainbow.prototype.setRows = function(rows) {
		if(rows instanceof Array){
			this.rows = rows;
		}
	};
	Rainbow.prototype.getRows = function() {
		return this.data;
	};
	Rainbow.prototype.addRows = function(row) {
		if(row != null){
			this.rows.push(row);
		}
	};
	Rainbow.prototype.set = function(key, value) {
		this.attr[key] = value;
	};
	Rainbow.prototype.remove = function(key) {
		delete this.attr[key];
	};
	
	Rainbow.prototype.setAttr = function(data){
		if(data != null && typeof(data) == "object"){
			for(var key in data){
				this.attr[key] = data[key];
			}
		}
		
	};
	

};

var sendCommand = function(rainbow, mothod, callback) {
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
		url : './dispatcherAction/execute.do',
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

var sendCommand2 = function(url, rainbow, mothod, callback) {
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

var editDialog = function(pageUrl,buttons, width, height, isModal, title, formName,
		node) {
	if(buttons == null || typeof(buttons) != "Array"){
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
	$('<div/>').dialog({
		href : pageUrl,
		width : width,
		height : height,
		modal : modal,
		title : title,
		buttons :buttons,
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function() {
			$('#' + formName).form('load', node);
		}
	});
};

var addDialog = function(pageUrl,buttons, width, height, isModal,title, formName) {
	if(buttons == null || typeof(buttons) != "Array"){
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
	$('<div />').dialog({
		href : pageUrl,
		width : width,
		height : height,
		modal : modal,
		title : title,
		buttons :buttons,
		onClose : function() {
			$(this).dialog('destroy');
		}
	});
};



function getTreeValues(obj,data,id){
	if (obj != null && obj != null && typeof (obj) == "object") {
		var value = new Object();
		value[id] = obj[id];
		var childrens =obj["children"];
		data.push(value);
		if (childrens != null && typeof (childrens) == "object") {
			
			var length = childrens.length;
			for ( var i = 0; i < length; i++) {
				getTreeValues(childrens[i],data,id);
			}
		} 
	}
	return data;
}
