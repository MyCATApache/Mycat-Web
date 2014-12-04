<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_setup_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 85%">
			<tr>
				<th style="width:15%;">功能名称</th>
				<td><input name="funName" style="width:85%;" /></td>
			</tr>
			<tr>
				<th style="width:15%;">功能代码</th>
				<td><input name="funCode" style="width:85%;" /></td>
			</tr>
			<tr>
				<th style="width:15%;">是否可见</th>
				<td><input name="isDisplay" class="easyui-combobox" data-options="panelHeight:'auto',textField:'text',valueField:'value',data:[{'text':'是','value':'1'},{'text':'否','value':'0'}]" style="width:85%;" /></td>
			</tr>
			<tr>
				<th style="width:15%;">功能描述</th>
				<td><textarea name="funDesc" style="width:85%;" rows="5"></textarea></td>
			</tr>
			<tr>
				<th style="width:15%;">备注</th>
				<td><textarea name="remark" style="width:85%;" rows="5"></textarea></td>
			</tr>
		</table>
	</form>
</div>