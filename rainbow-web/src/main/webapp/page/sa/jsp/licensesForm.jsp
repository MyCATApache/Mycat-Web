<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="licenses_addForm" method="post">
	<input name="guid"  type="hidden" />
	<input id="customerName" name="customerName" type="hidden"/>
	<input id= "productName" name="productName" type="hidden"/>
		<table class="tableForm" style="width: 80%">
			<tr>
				<th style="width: 120px">客户名称</th>
				<td><input class="easyui-combobox" style="width:205px;" 
					name="customerCode"
					data-options="
							data:customers,
							valueField:'code',
							textField:'name',
							panelHeight:'auto',
							editable:false,
							required:true,
							onSelect: function(rec){   
						   		$('#customerName').val(rec.name);
						    }
					"/></td>
			</tr>
			<tr>
				<th style="width: 120px">产品名称</th>
				<td><input class="easyui-combobox" style="width:205px;" 
					name="productCode"
					data-options="
							data:products,
							valueField:'code',
							textField:'name',
							panelHeight:'auto',
							editable:false,
							required:true,
							onSelect: function(rec){   
						   			$('#productName').val(rec.name);
						    }
					"/></td>
			</tr>
			<tr>
				<th style="width: 120px">许可证代码</th>
				<td><input name="licenseCode" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>	
				<th style="width: 120px">许可证版本</th>
				<td><input name="licVersion" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">许可证模式</th>
				<td><input class="easyui-combobox" style="width:205px;" value="01"
					name="licenseMode"
					data-options="
							data:authorModels,
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							editable:false,
							required:true
					"/></td>
				</tr>
			<tr>
				<th style="width: 120px">加密模式</th>
				<td><input class="easyui-combobox" style="width:205px;" value="SYSTEM"
					name="encryptModel"
					data-options="
							data:encryptModels,
							valueField:'code',
							textField:'name',
							panelHeight:'auto',
							editable:false,
							required:true
					"/>
				</td>
			</tr>
			<tr>
				<th style="width: 120px">起始时间</th>
				<td><input name="startDate" class="easyui-datebox" required="required" style="width:205px;" /></td>
			</tr>
			<tr>	
				<th style="width: 120px">过期时间</th>
				<td><input name="expiringDate" class="easyui-datebox" required="required" style="width:205px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">签发时间</th>
				<td><input name="signingDate" class="easyui-datebox" required="required" style="width:205px;" /></td>
			</tr> 
			<tr>	
				<th style="width: 120px">产品版本</th>
				<td><input name="versionNumber" class="easyui-validatebox" required="required" style="width:200px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">签发单位</th>
				<td colspan="3"><input name="productCompany" class="easyui-validatebox" required="required" style="width:200px;" /></td>
			</tr>
		</table>
	</form>
</div>