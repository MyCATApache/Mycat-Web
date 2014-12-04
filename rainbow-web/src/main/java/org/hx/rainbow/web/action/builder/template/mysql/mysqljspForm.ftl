<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="${jspName}_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<#list columns as property>
			<#if property.propertyName != "createTime" && property.propertyName != "createUser">
			<tr>
				<th >${property.columnComment}</th>
			<#if property.dataType == "timestamp">
				<td><input name="${property.propertyName}" class="Wdate" style="width:200px;" onFocus="WdatePicker({readOnly:true})"/></td>
			<#else>
			<#if property.dataType == "int">
				<td><input name="${property.propertyName}" style="width:205px;" class="easyui-numberspinner" data-options="min:0,max:999,editable:false,required:true" value="0" /></td>
			<#else>
				<td><input name="${property.propertyName}" style="width:200px;" /></td>
			</#if>
			</#if>
			</tr>
			</#if>
			</#list>
		</table>
	</form>
</div>