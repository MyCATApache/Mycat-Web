<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_org_addForm" method="post">
	<input name="guid"  type="hidden"  />
		<table class="tableForm" style="width: 90%;">
			<tr>
				<th style="width:20%;">机构代码</th>
				<td><input name="orgCode" class="easyui-validatebox" data-options="required:true" style="width:90%;"/></td>
				</tr>
			<tr>
				<th style="width:20%;">机构名称</th>
				<td><input name="orgName" class="easyui-validatebox" data-options="required:true" style="width:90%;"/></td>
				</tr>
			<tr>
				<th style="width:20%;">上级机构代码</th>
				<td><input id="parentCodes" name="parentCode" class="easyui-combotree" data-options="url:'./orgAction/queryComboxTree.do',parentField:'parentCode',lines:true,editable:true" style="width:380px;"><img src="./ui/style/images/extjs_icons/cut_red.png" onclick="$('#parentCodes').combotree('clear');" /> 
				</td>
			</tr>
			<tr>
				<th style="width:20%;">节点状态</th>
				<td><input class="easyui-combobox" style="width:380px;"
					name="state"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=IS_LEAF',
							valueField:'value',
							textField:'text',
							panelHeight:'auto'
					"/>
					</td></td>
				</tr>
			<tr>
				<th style="width:20%;">是否有效</th>
				<td><select  class="easyui-combobox" name="aliveFlag" data-options="panelHeight:'auto'" style="width:380px;">  
					    <option value="0">有效</option>  
					    <option value="1">无效</option>
					</select>  
				</td>
			</tr>
		</table>
	</form>
</div>