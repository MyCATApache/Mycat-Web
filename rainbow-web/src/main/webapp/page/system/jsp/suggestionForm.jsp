<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" align="center">
	<form id="sys_suggestion_addForm" method="post">
			<input name="guid" type="hidden" />
			<table class="tableForm" style="width:95%">
				<tr>
					<th>意见类型</th>
					<td><input class="easyui-combobox" data-options="panelHeight : 'auto',textField : 'text',valueField : 'value',value : 0,data : sys_suggestion_type" disabled="disabled" name="type" /></td>
					<th >级别</th>
					<td><input class="easyui-combobox" data-options="panelHeight : 'auto',textField : 'text',valueField : 'value',value : 0,data : sys_suggestion_sugLevel" disabled="disabled" name="sugLevel" /></td>
					<th>状态</th>
					<td><input id="sys_suggestion_sugStatusForm" class="easyui-combobox" data-options="panelHeight : 'auto',textField : 'text',valueField : 'value',value : 0,data : sys_suggestion_sugStatus" name="sugStatus" /></td>
				</tr>
				<tr>
					<th >意见标题</th>
					<td colspan="5"><input name="title"  style="width:100%;" disabled="disabled"/></td>
				</tr>
				<tr>
					<th >意见涉及模块</th>
					<td colspan="5"><input name="funModules"  style="width:100%;" disabled="disabled"/></td>
				</tr>
				<tr>
					<th >意见内容</th>
					<td colspan="5"><textarea id="north_sug_suggestion" name="suggestion" style="width:100%;height:140px;" disabled="disabled"></textarea></td>
				</tr>
				<tr>
					<th >系统回复</th>
					<td colspan="5"><textarea name="reply"  style="width:100%;height:140px;"></textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<script>
var sys_suggestion_isReply = function(textarea){
	var status = $('#sys_suggestion_sugStatusForm');
	if($(textarea).html() != ''){
		if(status.combobox('getValue') != 3){
			status.combobox('setValue','3');
		}
	}else{
		status.combobox('setValue','2');
	}
};
</script>