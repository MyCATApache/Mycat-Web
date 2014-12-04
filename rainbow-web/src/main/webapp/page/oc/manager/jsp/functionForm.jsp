<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="oc_function_addForm" method="post" >
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 100%">
			<tr>
				<th style="width:20%">函数名</th>
				<td colspan="3"><input id="oc_functionName" name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">类名称</th>
				<td colspan="3"><input name="class" style="width:90%;"/></td>
			</tr>
		</table>
		<a href="javascript:void(0);" onclick="oc_function_param_add()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加属性</a>
		<a href="javascript:void(0);" onclick="oc_function_param_delete();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">删除属性</a>
		<table id="co_fnParam" class="tableForm" style="width: 100%">
			<tr>
				<th style="width:20%">参数key</th>
				<td><input id="paramKey" name="paramKey" style="width:90%;"/></td>
				<th style="width:20%">参数value</th>
				<td><input name="paramValue" style="width:90%;"/></td>
			</tr>
		</table>

	</form>
</div>
<script type="text/javascript">
var oc_function_param_add = function(key,value){
	
	if(key == null)key = "";
	if(value == null)value = "";
	var param = '<tr>'
				  +	'<th style="width:20%">参数key</th>'
				  +	'<td><input name="paramKey" style="width:90%;" value="' + key + '"/></td>'
				  +	'<th style="width:20%">参数value</th>'
				  +	'<td><input name="paramValue" style="width:90%;" value="' + value + '"/></td>'
			  +	'</tr>';
	$('#co_fnParam').append(param);
	
	
};
var oc_function_param_delete = function(){
	var tr = $('#co_fnParam tr');
	var length = tr.length;
	$(tr[length-1]).remove();
};

var oc_function_loadParam = function(data){
	
	if('${param.flag}' != '1'){
		
		$('#oc_functionName').attr('readonly','readonly');
	}
	$.post('./dispatcherAction/query.do?service=functionParamService&method=query',{'functionName':data.name},function(data){
		var rows = null;
		if(data != null) rows = data.rows;
		if(rows != null && rows.length > 0){
			 $('#co_fnParam tr').remove();
			 var length = rows.length;
			 for(var i = 0; i < length; i++){
				 oc_function_param_add(rows[i].paramKey,rows[i].paramValue); 
			 }
		 }
	});
};

$(function(){
	$('#oc_function_addForm').form({onLoadSuccess:oc_function_loadParam});
});
</script>