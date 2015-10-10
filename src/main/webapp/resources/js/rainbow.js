var Rainbow = function() {
	this.service = null; //服务名
	this.method = null; //方法名
	this.dbname = null; 
	this.rows = []; //多条记录容器,相当于java中List<Map>
	this.attr = Object();//单条记录属性相当java中,Map

	Rainbow.prototype.setService = function(name) {
		this.service = name;
	};
	Rainbow.prototype.getService = function() {
		return this.service;
	};

	Rainbow.prototype.setMethod = function(name) {
		this.method = name;
	};
	
	Rainbow.prototype.getDbname = function() {
		return this.dbname;
	};

	Rainbow.prototype.setDbname = function(name) {
		this.dbname = name;
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
		return this.rows;
	};
	Rainbow.prototype.addRows = function(row) {
		if(row != null){
			this.rows.push(row);
		}
	};
	Rainbow.prototype.set = function(key, value) {
		this.attr[key] = value;
	};
	Rainbow.prototype.setParam = function(key, value) {
		this[key] = value;
	};
	Rainbow.prototype.removeParam = function(key) {
		delete this.attr[key];
	};
	
	Rainbow.prototype.setAttr = function(data){
		if(data != null && typeof(data) == "object"){
			for(var key in data){
				this.attr[key] = data[key];
			}
		}
		
	};
	
	Rainbow.prototype.getAttr = function(){
		return this.attr;
		
	};
	

};