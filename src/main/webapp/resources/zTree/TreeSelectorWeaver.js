function TreeSelectorWeaver(parentContainer, modalDiv, treeDIV, target_input, multipleCheck){
		this.parentContainer = parentContainer;
		this.modalDiv = modalDiv;//弹出层
		this.treeDIV = treeDIV;
		this.target_input = target_input;
		
		var hiddenInput_obj = $(" <input id=\""+target_input+"_hidden\" type=\"hidden\" name=\""+target_input+"\" >");
		$("#"+this.target_input).after(hiddenInput_obj);
		$("#"+this.target_input).removeAttr("name");
		this.multipleCheck = multipleCheck;
		//TODO 必须是配合update设置realVal
		var realVal = $("#"+this.target_input).attr("realVal");
		hiddenInput_obj.val(realVal);
		this.default_val =realVal;
		
		var $this = this;
		this.createModal($("#"+this.parentContainer),"可选择节点","<ul id='"+this.treeDIV+"' class='ztree'></ul>","primary");
		
		this.setting = {
   				check: {
   					enable: true,
   					chkStyle: "radio",
   					radioType: "all",
   					nocheck : false
   				},
   				data: {
   					simpleData: {
   						enable: true
   					}
   				},callback: {
   					onClick: function(e, treeId, treeNode) {
   						var zTree = $.fn.zTree.getZTreeObj(treeId);
   						zTree.checkNode(treeNode, !treeNode.checked, null, true);
   						return false;
   					},
   					onCheck: function(e, treeId, treeNode) {
   						
   						var zTree = $.fn.zTree.getZTreeObj(treeId),
   						nodes = zTree.getCheckedNodes(true),
   						v = "";
   						rv = "";
   						for (var i=0, l=nodes.length; i<l; i++) {
   							v += nodes[i].name + ",";
   							rv += nodes[i].id + ",";
   						}
   						if (rv.length > 0 ) rv = rv.substring(0, rv.length-1);
   						if (v.length > 0 ) v = v.substring(0, v.length-1);
   						var target_input_val_Obj = $("#"+$this.target_input+"_hidden");
   						target_input_val_Obj.val(rv);
   						var target_input_Obj = $("#"+$this.target_input);
   						target_input_Obj.val(v);
   						
   						if(!$this.multipleCheck){
   							$("#"+$this.modalDiv).modal('hide');
   						}
   					}
   				}
   			};
}

/**
 * url
 * 对单个元素进行验证,显示验证错误
 * invoke 
 */
TreeSelectorWeaver.prototype.weave = function(url,param,adapter) {
	var $this = this;
	$.ajax({
        url : url,
        cache : false, 
        data: param,
        async : false,
        type : "POST",
        dataType : 'json',
        success : function (result){
                var zNodes = [];
                for(var i=0;i< result.rows.length;i++){
                	if(adapter){
                		var item = adapter(result.rows[i]);
                		zNodes.push(item);
                	}else{
                		var item = result.rows[i];
                		item.pId = result.rows[i].parentCode;
                		item.name = result.rows[i].text;
                		item.url ="";
                		/*if($this.default_val && $this.default_val.indexOf(item.id) != -1){//TODO需要改进
                		item.checked = true;
                	}*/
                		zNodes.push(item);
                	}
                }
       	  		$.fn.zTree.init($("#"+$this.treeDIV),  $this.setting, zNodes);
        },
        fail : function(){
        	alert("获取菜单数据时候异常!");
        }
});
	
	$("#"+$this.target_input).on("focus",function(){
		var realVal = $("#"+$this.target_input).attr("realVal");
		if(realVal){
			var zTree = $.fn.zTree.getZTreeObj($this.treeDIV);
			//设置节点选中
			var nodes = zTree.getNodesByParam("id", realVal, null)
			for(var i=0;i<nodes.length;i++)
				zTree.checkNode(nodes[i], true, true);
		}
		
		$("#"+$this.modalDiv).modal('show');
	});
}

/**
 * 显示弹出框
 * @param $parent 父容器
 * @param title 弹出框标题
 * @param context 弹出框内容
 * @param css primary,info,warning,success,danger
 * 
 * 
 */
TreeSelectorWeaver.prototype.createModal = function($parent,title,context,level){
	  var dialogHTML = "<div id='"+this.modalDiv+"'  class=\"modal fade\" name=\"treeInfo\">"+
	    "<div class=\"modal-dialog\">"+
	      "<div class=\"modal-content\">"+
	        "<div class=\"modal-header\">"+
	          "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">×</span></button>"+
	          "<h4 class=\"modal-title\">"+title+"</h4>"+
	        "</div>"+
	        "<div class=\"modal-body\">"+
	           context+
	        "</div>"+
	        "<div class=\"modal-footer\">"+
	          "<button type=\"button\" class=\"btn btn-outline pull-left\" data-dismiss=\"modal\">关闭</button>"+
	        "</div>"+
	      "</div>"+
	    "</div>"+
	 "</div>";
	if($parent.find("#"+this.modalDiv).size() != 0){
	$parent.find("#"+this.modalDiv).eq(0).remove();
	}
	$parent.append($(dialogHTML));
}


