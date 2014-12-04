<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="oc_user_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th style="width:20%">用户</th>
				<td><input name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">密码</th>
				<td><input name="password" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">逻辑库</th>
				<td><input id="oc_host_state" class="easyui-combobox"
						name="schemas"
						data-options="
								url:'./dispatcherAction/comboxCode.do?service=schemaService&method=query',
								valueField:'name',
								textField:'name',
								multiple:true,
								panelHeight:'auto'
						"/>
				</td>
			</tr>
			<tr>
				<th style="width:20%">应用名称</th>
				<td><input name="appName" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">应用代码</th>
				<td><input name="appCode" style="width:90%;"/></td>
			</tr>
		</table>
	</form>
</div>