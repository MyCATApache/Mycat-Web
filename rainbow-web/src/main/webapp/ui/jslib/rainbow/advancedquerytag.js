var symbol_data=[{text:"等于",value:"="},
                 {text:"不等于",value:"<>"},
                 {text:"小于",value:"<"},
                 {text:"小于等于",value:"<="},
                 {text:"大于",value:">"},
                 {text:"大于等于",value:">="},
                 {text:"为空",value:"is null"},
                 {text:"全模糊",value:"like"},{text:"左模糊",value:"left_like"},{text:"右模糊",value:"right_like"},{text:"之间",value:"bt"}],combobox_symbol_data=[{text:"等于",value:"="}],dateBox_symbol_data=[{text:"等于",value:"="},{text:"小于",value:"<"},{text:"小于等于",value:"<="},{text:"大于",value:">"},{text:"之间",value:"bt"}];

var tag_aq_remove = function(object) {
	var tr = $(object).parent().parent();
	if(tr.parent().children().length >1){
		tr.remove();
	}
};

var tag_aq_fieldName_value = function(record, queryName, number) {
    var type = record.valueType;
    if(type == null){
    	type = "text";
    }
    $("#" + queryName + "_field_valueType" + number).val(type);
    tag_aq_initValue(queryName, number,type,null);
    switch (type) {
      case "baseCombobox":
        var code = record.valueCode;
        if (code) {
            $("#" + queryName + "_fieldValue" + number).combobox({
                url:"./dispatcherAction/comboxCode.do?code=" + code,
                id:number,
                panelWidth:294,
                panelHeight:"auto",
                valueField:"value",
                textField:"text"
            });
        };
        break;
      case "bizCombobox":
        $("#" + queryName + "_fieldValue" + number).combogrid({
            id:number,
            panelWidth:350,
            panelHeight:340,
            idField:record.bizComboxId,
            fitColumns:true,
            textField:record.bizComboxText,
            pagination:true,
            mode:"remote",
            url:"./dispatcherAction/query.do?service=" + record.valueService + "&method=" + record.valueMethod,
            columns:$.parseJSON(record.bizColumns)
        });
        break;
      case "dateBox":
    	   $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_btValue" + number + "' name='value'  type=\"hidden\"/> <input id='" + queryName + "_fieldValue" + number + "' style='width: 139px;' /><font style=\"font-weight:900;\">至</font><input id='" + queryName + "_endValue" + number + "' style='width: 139px;' /></span>");
  		  $("#" + queryName + "_fieldValue" + number).datetimebox();
          $("#" + queryName + "_endValue" + number).datetimebox();
          $("#" + queryName + "_endValue" + number).datetimebox({onChange: function(newValue, oldValue){
          		$("#" + queryName + "_btValue" + number).val($("#" + queryName + "_fieldValue" + number).datetimebox('getValue')+";"+newValue);
          	}
          });
          $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", dateBox_symbol_data);
          $("#" + queryName + "_symbol_combobox" + number).combobox("setValue", "bt");
          var now = getNow();
          var beginDate = now + " 00:00:00";
          $("#" + queryName + "_fieldValue" + number).datetimebox('setValue',beginDate);
          var endDate = now + " 23:59:59";
          $("#" + queryName + "_endValue" + number).datetimebox('setValue',endDate);
        break;

      default:
        $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", symbol_data);   
	  	break;
    }
};

var tag_aq_add = function(tableName, queryName, number, queryFildData) {
    $("#" + tableName + " tbody>tr:last").after('<tr><td align="center" width="25%"><input id="' + queryName + "_field_valueType" + number + '" name="valueType" type="hidden"/><input name="field" id="' + queryName + "_field_combobox" + number + '" style="width: 150px;"/></td><td align="center" width="17%"><input name="symbol" id="' + queryName + "_symbol_combobox" + number + '" style="width: 80px;" /></td><td align="center" width="50%" id="' + queryName + "valueData_"+number+'"><span id="' + queryName + "_span" + number +'"><input name="value" id="' + queryName + "_fieldValue" + number + '" style="width: 290px;" /></span></td><td align="center" width="8%"><a href="javascript:void(0);"  id="' + queryName + "_removeButton" + number + '" onclick="tag_aq_remove(this)\"></a></td></tr>');
    tag_aq_createCombobox(queryName + "_field_combobox", queryName, number, 140, "code", "describe", null, queryFildData, tag_aq_fieldName_value);
    tag_aq_createCombobox(queryName + "_symbol_combobox", queryName, number, 80, "value", "text", "=", symbol_data, tag_aq_symbol_value);
    $("#" + queryName + "_removeButton" + number).linkbutton({
        iconCls:"icon-remove",
        plain:true
    });
};

var tag_aq_symbol_value = function(record, queryName, number) {
	var symbolValue = record.value;
	var valueType = $("#" + queryName + "_field_valueType" + number).val();
	if (valueType == "baseCombobox" || valueType == "bizCombobox"){
		return;
	}
	tag_aq_initValue(queryName, number,null,symbolValue);
    if (symbolValue == "is null") {
        $("#" + queryName + "_fieldValue" + number).attr("readOnly", "readOnly");
    }else{
    	 $("#" + queryName + "_fieldValue" + number).removeAttr("readOnly");
    }
    
};

var tag_aq_createCombobox = function(part, queryName, number, width, valueCode, textCode, value, datas, fn) {
    $("#" + part + number).combobox({
        id:number,
        name:queryName,
        panelWidth:width,
        panelHeight:"auto",
        valueField:valueCode,
        textField:textCode,
        value:value,
        data:datas,
        onSelect:fn
    });
};

var tag_aq_initValue = function(queryName, number,valueType,symbolValue) {
	$("#" + queryName + "_span" + number).remove();
	if(!valueType){
		valueType = $("#" + queryName + "_field_valueType" + number).val();
	}
	if(!symbolValue){
		symbolValue = $("#" + queryName + "_symbol_combobox" + number).combobox("getValue");
	}
    if (valueType == "baseCombobox" || valueType == "bizCombobox") {
    	 $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_fieldValue" + number + "' name='value' style='width: 290px;' /></span>");
    	$("#" + queryName + "_symbol_combobox" + number).combobox("loadData", combobox_symbol_data);
        $("#" + queryName + "_symbol_combobox" + number).combobox("setValue", "=");
    }else{
	  	if(symbolValue == "bt"){
	  		if(valueType == "dateBox"){
	  			  $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_btValue" + number + "' name='value'  type=\"hidden\"/> <input id='" + queryName + "_fieldValue" + number + "' style='width: 139px;' /><font style=\"font-weight:900;\">至</font><input id='" + queryName + "_endValue" + number + "' style='width: 139px;' /></span>");
		  		  $("#" + queryName + "_fieldValue" + number).datetimebox();
		          $("#" + queryName + "_endValue" + number).datetimebox();
		          $("#" + queryName + "_endValue" + number).datetimebox({onChange: function(newValue, oldValue){
		          		$("#" + queryName + "_btValue" + number).val($("#" + queryName + "_fieldValue" + number).datetimebox('getValue')+";"+newValue);
		          	}
		          });
		          $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", dateBox_symbol_data);
		          $("#" + queryName + "_symbol_combobox" + number).combobox("setValue", "bt");
		          var now = getNow();
		          var beginDate = now + " 00:00:00";
		          $("#" + queryName + "_fieldValue" + number).datetimebox('setValue',beginDate);
		          var endDate = now + " 23:59:59";
		          $("#" + queryName + "_endValue" + number).datetimebox('setValue',endDate);
			}else{
		  		 $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_btValue" + number + "' name='value'  type=\"hidden\"/> <input id='" + queryName + "_fieldValue" + number + "' style='width: 139px;' /><font style=\"font-weight:900;\">至</font><input id='" + queryName + "_endValue" + number + "' style='width: 139px;' /></span>");
		  		$("#" + queryName + "_endValue" + number).blur(function(){
					$("#" + queryName + "_btValue" + number).val($("#" + queryName + "_fieldValue" + number).val() + ";" + $("#" + queryName + "_endValue" + number).val());
				});
			}
	  		
		}else{
			if(valueType == "dateBox"){
				 $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_fieldValue" + number + "' name='value' style='width: 290px;' /></span>");	
				 $("#" + queryName + "_fieldValue" + number).datetimebox();
			}else{
				 $("#" + queryName + "valueData_" + number).html("<span id='" + queryName + "_span" + number +"'><input id='" + queryName + "_fieldValue" + number + "' name='value' style='width: 290px;' /></span>");
			}
		}
	  	
    }
    return symbolValue;
};

var tag_aq_ddlSreach = function(queryName,gridId,title,filedData,whereName,dbId){

		if(title){
			whereName = title;
		}
		if($('#'+gridId).length == 0){
			return;
		}
		if(filedData){
			if(filedData.length == 0){
				$.messager.show({
					title : '温馨提示',
					msg : '该页面没有配置高级查询!'
				});
			}else{
				var pageUrl = './page/common/aq.jsp?whereName='+queryName; 
				var buttons = [{
					text : '查询',
					iconCls : 'icon-search',
					handler :function(){
						var datas =serializeObject($('#'+queryName+'_queryForm'));
						if(!datas) datas={};
						datas['dbname'] = $('#'+dbId).val();
						$('#'+ gridId).datagrid('load',datas);
					}
				},{
					text : '清除',
					iconCls : 'icon-clear',
					handler :function(){
						$('#'+queryName+'_dialog').dialog('refresh',pageUrl);
						tag_aq_reload('#'+ gridId,dbname);
					}
				},{
					text : '查询&关闭',
					iconCls : 'icon-search',
					handler : function(){
						var datas =serializeObject($('#'+queryName+'_queryForm'));
						if(!datas) datas={};
						datas['dbname'] = $('#'+dbId).val();
						$('#'+ gridId).datagrid('load',datas);
						$('#'+queryName+'_dialog').dialog({onClose:function(){
							
						}});
						$('#'+queryName+'_dialog').dialog('close');
					}
				},{
					text : '关闭',
					iconCls : 'icon-cancel',
					handler :function(){
						$('#'+queryName+'_dialog').dialog({onClose:function(){
							
						}});
						$('#'+queryName+'_dialog').dialog('close');
					}
				}];
				if($('#'+queryName+'_dialog').length > 0){
					$('#'+queryName+'_dialog').dialog('open');
				}else{
					rainbowDialog.openDialog(queryName+'_dialog',pageUrl,buttons,650,280,false,'<font color="red">'+whereName+'</font>-高级查询       按住当前位置可拖动',queryName+'_queryForm',null);
					$('#'+queryName+'_dialog').dialog('move',{   
						  top:220   
						});
				}
			}
		}
};
var tag_aq_reload = function(grid,dbname){
	$(grid).datagrid('clearSelections');
	$(grid).datagrid('reload',{'dbname':dbname});
};

var getNow = function(){
	var userAgent = navigator.userAgent.toLowerCase();
    var chrome = /chrome/.test(userAgent);
    var now = new Date();
    var year = now.getYear();
    if ($.browser.mozilla || chrome) {
        year += 1900;
    }
	return year + "-" + (now.getMonth() + 1) + "-" + now.getDate(); 
};