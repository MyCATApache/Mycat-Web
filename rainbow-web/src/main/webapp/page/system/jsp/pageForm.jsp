<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_page_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%;" >
			<tr>
				<th style="width: 20%;">页面代码</th>
				<td><input name="code" style="width: 90%;" class="easyui-validatebox" data-options="required:true" />
				</td>
			</tr>
			<tr>
				<th style="width: 20%;">页面名称</th>
				<td><input name="name" style="width: 90%;" class="easyui-validatebox" data-options="required:true"/>
				</td>
			</tr>
			<tr>
				<th style="width: 20%;">页面路径</th>
				<td colspan="3"><input name="url" class="easyui-validatebox" style="width:90%;" data-options="required:true"/>
			</tr>
			<tr>
				<th style="width: 20%;">是否缓存</th>
				<td colspan="3"><input name="isCache" class="easyui-combobox" style="width:90%;" data-options="panelHeight:'auto',textField:'text',valueField:'value',data:[{'text':'是','value':1},{'text':'否','value':0}],value:'1'"/>
			</tr>
		</table>
	</form>
</div>