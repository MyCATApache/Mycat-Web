<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="customer_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th >客户编号</th>
				<td><input name="code" class="easyui-validatebox" data-options="required:true"  style="width:200px;" /></td>
			</tr>
			<tr>
				<th >客户名称</th>
				<td><input name="name" class="easyui-validatebox" data-options="required:true"  style="width:200px;" /></td>
			</tr>
			<tr>
				<th >联系人</th>
				<td><input name="linkman" class="easyui-validatebox" data-options="required:true"  style="width:200px;" /></td>
			</tr>
			<tr>
				<th >联系电话</th>
				<td><input name="telephony" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >邮箱地址</th>
				<td><input name="eMail" class="easyui-validatebox" data-options="validType:'email'" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >备注</th>
				<td><input name="remark" style="width:200px;" /></td>
			</tr>
		</table>
	</form>
</div>