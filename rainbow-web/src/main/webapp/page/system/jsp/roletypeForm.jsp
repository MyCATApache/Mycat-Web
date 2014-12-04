<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_roletype_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width: 20%">角色类型代码</th>
				<td><input name="roleTypeCode" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">角色类型名称</th>
				<td><input name="roleTypeName" class="easyui-validatebox" data-options="required:true" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width: 20%">角色类型组</th>
				<td><input class="easyui-combobox" style="width:380px;"
					name="roleTypeGroup"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=ROLEYTYPEGROUP',
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							required:true
					"/></td>
			</tr>
			<tr>
				<th style="width: 20%">状态</th>
				<td>
				<select  class="easyui-combobox" name="status" data-options="panelHeight:'auto',required:true" style="width:380px;">  
					    <option value="0">有效</option>  
					    <option value="1">无效</option>
				</select> 
				</td> 
			</tr>
			<tr>
				<th style="width: 20%">排序索引</th>
				<td><input name="sortIndex" style="width:380px;" class="easyui-numberspinner" data-options="min:0,max:999,editable:false,required:true" value="0" /></td>
			</tr>
			<tr>
				<th style="width: 20%">备注</th>
				<td><textarea name="remark" style="height:50px;width:90%;" /></td>
			</tr>
		</table>
	</form>
</div>