<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="rsa_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th >密钥代码</th>
				<td><input name="code" class="easyui-validatebox" data-options="required:true" style="width:280px;" /></td>
			</tr>
			<tr>
				<th >密钥名称</th>
				<td><input name="name" class="easyui-validatebox" data-options="required:true" style="width:280px;" /></td>
			</tr>
			<tr>
				<th >公钥</th>
				<td><input name="publicKey" readonly="readonly" style="width:280px;background-color:#E6E4DA;" /></td>
			</tr>
			<tr>
				<th >私钥</th>
				<td><textarea name="privateKey"  readonly="readonly" style="width:280px;height:50px;background-color:#E6E4DA;" /></td>
			</tr>
			<tr>
				<th >模数</th>
				<td><textarea name="modulus" wrap="virtual" readonly="readonly" style="width:280px;height:50px;background-color:#E6E4DA;"/></td>
			</tr>
		</table>
	</form>
</div>