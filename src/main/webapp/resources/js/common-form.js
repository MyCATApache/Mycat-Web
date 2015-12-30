/**
 * 表单校验
 * @param $obj 表单对象
 * @param errormsg 出错信息
 * @returns {Boolean} 校验结果
 */
function validate($obj,errormsg){
	var errorLabel = "<label name=\"errorLabel\" class=\"control-label\" for=\"inputError\"><i class=\"fa fa-times-circle-o\"></i>"+errormsg+"</label>";
	var value = $obj.val();
	if($.trim(value) == ''){
		$obj.parent().append(errorLabel);
		$obj.parent().addClass("has-error");
		return false;
	}else{
		if($obj.parent().find("[name='errorLabel']").length > 0){
			$obj.parent().find("[name='errorLabel']").remove();
		}
		return true;
	}
}

var getFunc = function(methodName) {
	if (typeof (ValidateRegulation[methodName]) == "function") {
		return ValidateRegulation[methodName];
	} else if (typeof (window[methodName]) == "function") {
		return window[methodName];
	} else {
		return;
	}
}

/**
 * 显示弹出框
 * @param $parent 父容器
 * @param title 弹出框标题
 * @param context 弹出框内容
 * @param css primary,info,warning,success,danger
 */
function showDialog($parent,title,context,level){
	title = "执行结果";
  var dialogHTML = "<div class=\"modal "+(level != undefined?"modal-"+level:"") +"\" name=\"errorInfo\">"+
			    "<div class=\"modal-dialog\" >"+
			      "<div class=\"modal-content\">"+
			        "<div class=\"modal-header\">"+
			          "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">×</span></button>"+
			          "<h4 class=\"modal-title\">"+title+"</h4>"+
			        "</div>"+
			        "<div class=\"modal-body\" style=\"height:100%;width:100%;\">"+
			          "<p>"+context+"</p>"+
			        "</div>"+
			        "<div class=\"modal-footer\">"+
			          "<button type=\"button\" class=\"btn btn-outline pull-left\" data-dismiss=\"modal\">关闭</button>"+
			        "</div>"+
			      "</div>"+
			    "</div>"+
			 "</div>";
  if($parent.find("[name='errorInfo']").size() != 0){
	   $parent.find("[name='errorInfo']").eq(0).remove();
  }
  $parent.append($(dialogHTML));
  $("[name='errorInfo']").modal({backdrop: 'static', keyboard: false});
  $("[name='errorInfo']").modal("show");
  
  //2秒钟渐渐淡出
  setTimeout('modalFadeout("errorInfo")', 2000);
}

function modalFadeout(name){
	 $("[name='errorInfo']").modal("hide");
}

var serializeObject = function(form,isNull) {
	var o = {};
	$.each(form.serializeArray(), function(index) {
		if(this['value'] != null){
			if(this['value'] != '' || isNull){
				if (o[this['name']]) {
					o[this['name']] = o[this['name']] + "," + this['value'];
				} else {
					o[this['name']] = this['value'];
				}
			}
		}
	});
	return o;
};

function resetBtn(form){
	$("#"+form)[0].reset();
}

/**
 * 
 * @param j_guid
 * @param pannel_id
 */
function ready4Update(j_guid,pannel_id,extra_item_beforeCall,extra_item_afterCall, extra_call_beforeShow){
	 if(mmgrid){
		 	//reset button and error info
		 	$("button").button('reset');
		 	var modifyForm_obj = $("#modify_form");
			var rows = mmgrid.rows();
			var length = rows.length;
			for(var i=0;i<length ;i++){
				var row = rows[i];
				if(row.guid == j_guid){
					  for(var attr_ in row){
						  if(extra_item_beforeCall){
							  var extra_func = getFunc(extra_item_beforeCall);
							  if(extra_func)
								  extra_func(modifyForm_obj,row,attr_);
						  }
						  //val
						  modifyForm_obj.find("[name='"+attr_+"']").val([''+row[attr_]]);
						  
						  if(extra_item_afterCall){
							  var extra_func = getFunc(extra_item_afterCall);
							  if(extra_func)
								  extra_func(modifyForm_obj,row,attr_);
						  }
					  }
					  if(extra_call_beforeShow){
						  var extra_func = getFunc(extra_call_beforeShow);
						  if(extra_func)
							  extra_func(modifyForm_obj,row,attr_);
					  }
					 $("#"+pannel_id).modal('show');
					 return ;
				}
			}
		}
}

function updateForm(serviceName,method,form_id,extra_call){
	var v = new Validator(form_id);
	var result = v.traversalCheck();
	if(!result) return false;

	   var event = window.event || arguments[0];
	   var evt = event.srcElement ? event.srcElement : event.target; 
	   $(evt).button('loading');
	   
	var data =serializeObject($("#"+form_id),true);
	if(extra_call){
		  var extra_func = getFunc(extra_call);
		  if(extra_func)
			  extra_func(form_id,data);
	}
	var rainbow = new Rainbow();
	rainbow.setAttr(data);
	rainbow.addRows(data);
	rainbow.setService(serviceName);
	rainbow.setMethod(method);
	rainbowAjax.excute(rainbow,new Callback());
}

var Add_Callback = function(){
	this.onSuccess=function(data){
		try {
			var j_obj = data;
			if (j_obj.success) {
				showDialog($("#container"),"新增","操作成功!","success");
				$(':input','#add_form').not(':button, :submit, :reset, :hidden,select').val('');
			}else{
				var failmsg="操作失败!";
				if(j_obj.msg!=null)
					failmsg=j_obj.msg;
				showDialog($("#container"),"",failmsg,"danger");
			}
		} catch (e) {
			alert("异常!")
		}finally{
			if($("button[data-loading-text]"))
				$("button[data-loading-text]").button('reset');

			$("button").button('reset');
		}
	};
	this.onFail = function(jqXHR, textStatus, errorThrown){
		showDialog($("#container"),"","操作失败!","danger");
	};
};

var Callback = function(){
		this.onSuccess=function(data){
			try {
				var j_obj = data;
				if (j_obj.success) {
					showDialog($("#container"),"","操作成功!","success");
					$("#showInfo").modal("hide"); 
					if(mmgrid){
						mmgrid.load();
					}
				}else{
					var failmsg="操作失败!";
					if(j_obj.msg!=null)
						failmsg=j_obj.msg;
					showDialog($("#container"),"",failmsg,"danger");
				}
			} catch (e) {
				alert("异常!")
			}finally{
				if($("button[data-loading-text]"))
					$("button[data-loading-text]").button('reset');

				$("button").button("reset");
			}
		};
		this.onFail = function(jqXHR, textStatus, errorThrown){
			showDialog($("#container"),"","操作失败!","danger");
		};
};

function delObj(serviceName,method,_id,extra_call){
	if(confirm("你确定删除这条记录吗?")){
		var rainbow = new Rainbow();
		var row = {"guid":_id};
		/*var rows =  mmgrid.rows();
		for(var i=0;i<rows.length;i++){
			if(rows[i].guid == _id){
				row = rows[i];
				break;
			}
		}*/
		rainbow.addRows(row);
		rainbow.setAttr(row);
		if(extra_call){
			var extra_func = getFunc(extra_call);
			if(extra_func){
				var result = extra_func(rainbow);
				if(result=="false"){
					return ;
				}
			}
		}
		rainbow.setService(serviceName);
		rainbow.setMethod(method);
		rainbowAjax.excute(rainbow,new Callback());
	}
}

function delObjBatch(serviceName,method,extra_call){
	if(mmgrid){
		var rows =  mmgrid.selectedRows();
		var length = rows.length;
		if(length <= 0){
			alert('请先选择要操作的任务!');
			return ;
		}
		if(confirm("你确定删除这些记录吗?")){
			var rainbow = new Rainbow();
			for(var i=0;i<length ;i++){
				var row = rows[i];
				rainbow.addRows(row);
			}
			if(extra_call){
				  var extra_func = getFunc(extra_call);
				  if(extra_func){
					  var result = extra_func(rainbow);
					  if(!result){
						  return ;
					  }
				  }
			}
			rainbow.setService(serviceName);
			rainbow.setMethod(method);
			rainbowAjax.excute(rainbow,new Callback());
		}
	}
}

function queryForm(form_id,extra_call){
	var datas =serializeObject($('#'+form_id));
	if(extra_call){
		  var extra_func = getFunc(extra_call);
		  if(extra_func)
			  extra_func(datas);
	}
	if(mmgrid){
		mmgrid.load(datas);
	}else{
		$('#table').load(datas);
	}
}

function addForm(serviceName,method,_formid,extra_call){
	   var v = new Validator(_formid);
	   var result = v.traversalCheck();
	   if(!result) return false;
	   
	   var event = window.event || arguments[0];
	   var evt = event.srcElement ? event.srcElement : event.target; 
	   $(evt).button('loading');
	   
	   var data =serializeObject($("#"+_formid),true);
	   var rainbow = new Rainbow();
	   if(extra_call){
			  var extra_func = getFunc(extra_call);
			  if(extra_func)
				  extra_func(_formid,data);
	   }
	   rainbow.setAttr(data);
	   rainbow.addRows(data);
	   rainbow.setService(serviceName);
	   rainbow.setMethod(method);
	   rainbowAjax.excute(rainbow,new Add_Callback());
}

 function getParam(url,name){
        var search = url;
        var pattern = new RegExp("[?&]"+name+"\=([^&]+)", "g");
        var matcher = pattern.exec(search);
        var items = null;
        if(null != matcher){
                try{
                        items = decodeURIComponent(decodeURIComponent(matcher[1]));
                }catch(e){
                        try{
                                items = decodeURIComponent(matcher[1]);
                        }catch(e){
                                items = matcher[1];
                        }
                }
        }
        return items;
};
var zkPath = "";
var zkId = "";
function loadContext(url,copy){
	
	if(intervalId)
		clearInterval(intervalId); 
	if(copy && mmgrid){
		var rows =  mmgrid.selectedRows();
		var length = rows.length;
		if(length > 0){
			data = rows[0];
		}else{
			alert("请选择复制的条目!");
			return;
		}
	}

	zkPath=getParam(url,"zkpath");
	zkId=getParam(url,"zkid");
   	$(".content-wrapper").load(url,function(response,status,xhr){
   		$(".content").resize(function(){});
   		
   	});
}

var mmgrid;
var data ={};

function ready4AddForm(extra_call){
	   if(window.data){
		   var obj = window.data;
		   for(var attr_ in obj){
			   if( $("#add_form").find("[name='"+attr_+"']")){
				   $("#add_form").find("[name='"+attr_+"']").val([''+obj[attr_]]);
			   }
			   if(extra_call){
					  var extra_func = getFunc(extra_call);
					  if(extra_func)
						  extra_func($("#add_form"), obj, attr_);
			   }
		   }
		   data = {};
	   }
}




function showModal(myModal){
	
	$("#"+myModal).modal('show');
}

function loadFormData(formId,jsonStr){
    var obj = eval("("+jsonStr+")");
    var key,value,tagName,type,arr;
    for(x in obj){
        key = x;
        value = obj[x];
        $("[name='"+key+"']").each(function(){
            tagName = $(this)[0].tagName;
            type = $(this).attr('type');
            if(tagName=='INPUT'){
                if(type=='radio'){
                    $(this).prop('checked',$(this).val()==value);
                }else if(type=='checkbox'){
                    arr = String(value).split(',');
                    for(var i =0;i<arr.length;i++){
                    	$(this).prop('checked',false);
                        if($(this).val()== arr[i]){
                            $(this).prop('checked',true);
                            break;
                        }
                    }
                }else{
                    $(this).val(value);
                }
            }else if(tagName=='SELECT'){
            	var dataArray = String(value).split(',');
            	if(typeof $(tagName).attr("multiple")=="undefined"){
            		  $(this).multiselect('select', dataArray[0]);
            		  $(this).multiselect("refresh");
            	}else{
            		  $(this).multiselect('deselectAll',false);
            		  $(this).multiselect('select', dataArray);
            		  $(this).multiselect("refresh");
            	}
            }else if(tagName=='TEXTAREA'){
            	$(this).val(value);
            }
        });
    }
}

function datagrid(tableId,url,columns){
	$("#"+tableId).bootstrapTable({
		url:url,
		columns : columns,
		dataType : "json",
		locale: 'zh-CN',
		pageList : [10,20,30,50],
		sidePagination : "server",
		showPaginationSwitch : true,
		pagination:true,
		paginationHAlign : "right",
		showColumns : true,
		showRefresh : true,
		showToggle : true,
		search : true,
		showPaginationSwitch : true,
		minimumCountColumns : 2,
		//height : 500,
		pageList : [10,20,30,50],
		queryParamsType :"limit",
		pageSize:10,
		pageNumber:1,
		queryParams : function(params){
			return {
				offset : params.offset,
				limit : params.limit,
				search : params.search
			}
		},
		cache :false   
	
	});
}

function deleteRow(serviceName,method,data,callback){
	var rainbow = new Rainbow();
	rainbow.setAttr(data);
	rainbow.addRows(data);
	rainbow.setService(serviceName);
	rainbow.setMethod(method);
	handler(rainbow,callback);
}

function handler(rainbow, callback) {
	try {
		jQuery.ajax({
			cache : false,
			type : 'POST',
			dataType : "json",
			url : './dispatcherAction/execute.do',
			data : $.parseJSON(JSON.stringify(rainbow)),
			error : function() {
				showDialog($("#container"), "", "请求失败!", "danger");
			},
			success : function(response) {
				if (response.success == true) {
					showDialog($("#container"), "", "操作成功!", "success");
					if (callback instanceof Function) {
						callback();
					}
				} else {
					showDialog($("#container"), "", response.msg, "danger");
				}
			}
		});
	} catch (e) {
		alert("异常!");
	} finally {
		if ($("button[data-loading-text]"))
			$("button[data-loading-text]").button('reset');
			$("button").button('reset');
	}
	return false;
}

function saveForm (serviceName,method,form,data,callback){
	   var event = window.event || arguments[0];
	   var evt = event.srcElement ? event.srcElement : event.target; 
	   $(evt).button('loading');
	   var formData =serializeObject($("#"+form),true);
	   $.extend(formData,data);
	   var rainbow = new Rainbow();
	   rainbow.setAttr(formData);
	   rainbow.addRows(formData);
	   rainbow.setService(serviceName);
	   rainbow.setMethod(method);
	   handler(rainbow,callback);
}

function destroyValidator(form){
	 $("#"+form).bootstrapValidator("destroy");
	 
}
