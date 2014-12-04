<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_code_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width: 20%">值</th>
				<td><input name="value" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">名称</th>
				<td><input name="text" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">代码</th>
				<td><input name="code" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">上级代码</th>
				<td><input name="parent" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">备注</th>
				<td><textarea name="remark" style="height:50px;width:90%;"/></td>
			</tr>
		</table>
	</form>
</div>