<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_button_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width: 20%">按钮代码</th>
				<td><input name="buttonCode" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">按钮名称</th>
				<td><input name="buttonName" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">页面代码</th>
				<td><input name="pageCode" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
		</table>
	</form>
</div>