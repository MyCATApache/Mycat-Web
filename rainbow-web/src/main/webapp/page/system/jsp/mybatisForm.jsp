<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_mybatis_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th style="width: 30%;">NAMESPACE代码</th>
				<td><input name="nsCode" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 30%;">NAMESPACE描述</th>
				<td><input name="nsName" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 30%;">STATEMENT代码</th>
				<td><input name="stCode" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 30%;">STATEMENT描述</th>
				<td><input name="stName" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 30%;">备注</th>
				<td><input name="remark" style="width:90%;" /></td>
			</tr>
		</table>
	</form>
</div>