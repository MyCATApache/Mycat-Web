<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_changepwd_addForm" method="post">
	<input name="guid"  type="hidden"  />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width: 20%">原始密码:</th>
				<td><input name="password" type="password" class="easyui-validatebox" data-options="required:true"  style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width: 20%">新密码:</th>
				<td><input name="newPassword" type="password" data-options="required:true"  class="easyui-validatebox" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width: 20%">确认密码:</th>
				<td><input name="rePassword" type="password"  data-options="required:true" style="width:90%;"/></td>
			</tr>
		</table>
	</form>
</div>
