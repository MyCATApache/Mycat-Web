<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_service_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width: 20%">服务名</th>
				<td><input name="serviceCode" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">服务描述</th>
				<td><input name="serviceName" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">方法名</th>
				<td><input name="methodCode" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">方法描述</th>
				<td><input name="methodName" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">所属包</th>
				<td><input name="package" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">备注</th>
				<td><textarea name="remark" style="height:60px;width:90%;" /></td>
			</tr>
		</table>
	</form>
</div>