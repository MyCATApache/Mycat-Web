<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="oc_tablerule_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th style="width:20%">分片规则名称</th>
				<td><input name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">分片字段集合</th>
				<td><input name="columns" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">分片函数名</th>
				<td><input id="oc_host_parentHost" name="algorithm" class="easyui-combobox"
							   data-options="
								url:'./dispatcherAction/comboxCode.do?service=functionService&method=queryCombox',
								valueField:'id',
								textField:'text',
								panelHeight:'auto'"/></td>
			</tr>
		</table>
	</form>
</div>