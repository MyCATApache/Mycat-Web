var symbol_data = [ {
    text:"等于",
    value:"="
}, {
    text:"不等于",
    value:"<>"
}, {
    text:"小于",
    value:"<"
}, {
    text:"小于等于",
    value:"<="
}, {
    text:"大于",
    value:">"
}, {
    text:"大于等于",
    value:">="
}, {
    text:"为空",
    value:"is null"
}, {
    text:"模糊",
    value:"like"
}, {
    text:"之间",
    value:"bt"
} ];

var combobox_symbol_data = [ {
    text:"等于",
    value:"="
} ];

var dateBox_symbol_data = [ {
    text:"等于",
    value:"="
}, {
    text:"小于",
    value:"<"
}, {
    text:"小于等于",
    value:"<="
}, {
    text:"大于",
    value:">"
}, {
    text:"之间",
    value:"bt"
} ];

var tag_query_remove = function(tableName, object) {
    var table = $("#" + tableName);
    var current_td = $(object).parent();
    var current_tr = current_td.parent();
    var trs = table.find("tr");
    var trSeq = trs.index(current_tr[0]);
    current_td.remove();
    for (var i = 0; i < trs.length; i++) {
        if (i < trSeq) {
            continue;
        }
        var tr = $(trs[i]);
        var tr_next = $(trs[i + 1]);
        if (tr_next) {
            var td = tr_next.find("td")[0];
            if (td) {
                tr.append($(td));
            }
            if (tr_next.children().length == 0) {
                tr_next.remove();
            }
        }
    }
};

var tag_query_fieldName_value = function(record, queryName, number) {
    var symbolValue = tag_query_initValue(queryName, number);
    var type = record.valueType;
    if (type == "baseCombobox" || type == "bizCombobox") {
        $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", combobox_symbol_data);
        if (symbolValue == "bt") {
            $("#" + queryName + "_tagNode" + number).remove();
            $("#" + queryName + "_fieldValue" + number).css("width", "230px");
        }
        $("#" + queryName + "_symbol_combobox" + number).combobox("setValue", "=");
    }
    switch (type) {
      case "baseCombobox":
        var code = record.valueCode;
        if (code) {
            $("#" + queryName + "_fieldValue" + number).combobox({
                url:"./dispatcherAction/comboxCode.do?code=" + code,
                id:number,
                panelWidth:230,
                panelHeight:"auto",
                valueField:"value",
                textField:"text"
            });
        }
        ;
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
        $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", dateBox_symbol_data);
        var symbolValue = $("#" + queryName + "_symbol_combobox" + number).combobox("getValue");
        if (symbolValue == "bt") {
            $("#" + queryName + "_fieldValue" + number).datetimebox({
                panelWidth:230
            });
            $("#" + queryName + "_endValue" + number).datetimebox({
                panelWidth:230
            });
        } else {
            $("#" + queryName + "_fieldValue" + number).datetimebox({
                panelWidth:230
            });
        }
        break;

      default:
        $("#" + queryName + "_symbol_combobox" + number).combobox("loadData", symbol_data);
        break;
    }
};

var tag_query_add = function(tableName, queryName, number, queryFildData) {
    var table = $("#" + tableName);
    var td_length = table.find("td").length;
    if (td_length % 2 == 0) {
        table.find("tr:last").after('<tr><td><font style="font-weight:900;">属性：</font><input name="field" id="' + queryName + "_field_combobox" + number + '" style="width: 100px;"/>&nbsp;&nbsp;<font style="font-weight:900;">条件：</font><input name="symbol" id="' + queryName + "_symbol_combobox" + number + '" style="width: 65px;" />&nbsp;&nbsp;<font style="font-weight:900;">值：</font><input name="value" id="' + queryName + "_fieldValue" + number + '" style="width: 230px;" /><a href="javascript:void(0);"  id="' + queryName + "_removeButton" + number + '" onclick="tag_query_remove(\'' + tableName + "',this)\"></a></td></tr>");
    } else {
        table.find("tr:last").append('<td><font style="font-weight:900;">属性：</font><input id="' + queryName + "_field_combobox" + number + '" name="field" style="width: 100px;"/>&nbsp;&nbsp;<font style="font-weight:900;">条件：</font><input name="symbol" id="' + queryName + "_symbol_combobox" + number + '" style="width: 65px;" />&nbsp;&nbsp;<font style="font-weight:900;">值：</font><input name="value" id="' + queryName + "_fieldValue" + number + '" style="width: 230px;" /><a href="javascript:void(0);"  id="' + queryName + "_removeButton" + number + '" onclick="tag_query_remove(\'' + tableName + "',this)\"></a></td>");
    }
    tag_query_createCombobox(queryName + "_field_combobox", queryName, number, 140, "code", "describe", null, queryFildData, tag_query_fieldName_value);
    tag_query_createCombobox(queryName + "_symbol_combobox", queryName, number, 65, "value", "text", "=", symbol_data, tag_query_symbol_value);
    $("#" + queryName + "_removeButton" + number).linkbutton({
        iconCls:"icon-remove",
        plain:true
    });
};

var tag_query_symbol_value = function(record, queryName, number) {
    $("#" + queryName + "_fieldValue" + number).val("");
    var symbolValue = record.value;
    if (symbolValue == "bt") {
        var fildValueCss = $("#" + queryName + "_fieldValue" + number).css("display");
        if (fildValueCss == "none") {
            $("#" + queryName + "_fieldValue" + number).datetimebox("destroy");
            $("#" + queryName + "_removeButton" + number).before("<input id='" + queryName + "_fieldValue" + number + "' name='value' style='width: 100px;' /><span id='" + queryName + "_tagNode" + number + "'>至：<input id='" + queryName + "_endValue" + number + "' name='value' style='width: 100px;' /></span>");
            $("#" + queryName + "_fieldValue" + number).datetimebox({
                panelWidth:230
            });
            $("#" + queryName + "_endValue" + number).datetimebox({
                panelWidth:230
            });
        } else {
            $("#" + queryName + "_fieldValue" + number).css("width", "100px");
            $("#" + queryName + "_fieldValue" + number).after("<span id='" + queryName + "_tagNode" + number + "'>至：<input id='" + queryName + "_endValue" + number + "' name='value' style='width: 100px;' /></span>");
        }
        $("#" + queryName + "_fieldValue" + number).removeAttr("readOnly");
    } else {
        if ($("#" + queryName + "_tagNode" + number).length > 0) {
            $("#" + queryName + "_tagNode" + number).remove();
            $("#" + queryName + "_fieldValue" + number).css("width", "230px");
            $("#" + queryName + "_fieldValue" + number).removeAttr("readOnly");
        }
        if (symbolValue == "is null") {
            $("#" + queryName + "_fieldValue" + number).attr("readOnly", "readOnly");
        }
    }
};

var tag_query_createCombobox = function(part, queryName, number, width, valueCode, textCode, value, datas, fn) {
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

var tag_query_initValue = function(queryName, number) {
    $("#" + queryName + "_fieldValue" + number).removeAttr("readOnly");
    $("#" + queryName + "_fieldValue" + number).combobox({});
    $("#" + queryName + "_fieldValue" + number).combobox("destroy");
    $("#" + queryName + "_removeButton" + number).before("<input id='" + queryName + "_fieldValue" + number + "' name='value' style='width: 230px;' />");
    return $("#" + queryName + "_symbol_combobox" + number).combobox("getValue");
};
var tag_query = function(queryName,grid){
	var datas =serializeObject($('#'+queryName+'_queryForm'));
	$(grid).datagrid('load',datas);
};
var tag_queryClear = function(queryName,grid,count){
	$('#'+queryName+'_queryForm input').val('');
	if($('#'+queryName+'_symbol_combobox'+i).length==0){
		return false;
	}
	for ( var i = 0; i < count; i++) {
		$('#'+queryName+'_symbol_combobox'+i).combobox('setValue','=');
	}
	tag_reload(grid);
};
var tag_ddlSreach = function(queryName,headName){
	$('#'+headName).css('height','140px');
	$('#tag_query_'+queryName+'_panel').panel('open');
};
var tag_reload = function(grid){
	$(grid).datagrid('clearSelections');
	$(grid).datagrid('reload',{});
};