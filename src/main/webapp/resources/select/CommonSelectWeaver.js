function CommonSelectWeaver(idArea){
	this.idArea = idArea;
	this.optionItems = [];
	this.select_obj = $("#"+idArea);
	this.select_obj.empty();
};

/**
 * 根据选项数组生成下拉选项
 * 同时读取realValue进行设置
 * @return
 */
CommonSelectWeaver.prototype.weave = function(url,param,adapter){
	var $this = this;
///	$this.optionItems.empty();
	$.ajax({
        url : url,
        cache : false, 
        data: param,
        async : false,
        type : "GET",
        dataType : 'json',
        success : function (result){
                for(var i=0;i< result.rows.length;i++){
                	if(adapter){
                		var item = adapter(result.rows[i]);
                		var item_obj = $this.createOption(item);
                		//optionItems.push();
                		$this.select_obj.append(item_obj);
                	}else{
                		var item = result.rows[i];
                		var item_obj = $this.createOption(item);
                		//optionItems.push();
                		$this.select_obj.append(item_obj);
                	}
                }
        },
        fail : function(){
        	alert("获取数据时候异常!");
        }
	});
	
};



/**
 * item:{val:'',text:''}
 * @return
 */
CommonSelectWeaver.prototype.createOption= function(item){
	return $("<option value='"+item.val+"'>"+item.text+"</option>");
};
