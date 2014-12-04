<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="product_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th >产品代码</th>
				<td><input name="productCode" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >产品名称</th>
				<td><input name="productName" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >产品摘要</th>
				<td><input name="productDigest" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >产品类型</th>
				<td><input id= "" class="easyui-combobox" style="width:205px;"
					name="productType"
					data-options="
							data:productTypes,
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							editable:false,
							required:true
					"/>
				</td>
			</tr>
			<tr>
				<th >业务板块</th>
				<td><input class="easyui-combobox" style="width:205px;" 
					name="bizField"
					data-options="
							data:bizFields,
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							editable:false,
							required:true
					"/>
				</td>
			</tr>
			<tr>
				<th >产品经理</th>
				<td><input name="productManager" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >产品经理电话</th>
				<td><input name="managerPhone" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >公司名称</th>
				<td><input name="company" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >公司地址</th>
				<td><input name="address"  style="width:200px;" /></td>
			</tr>
			<tr>
				<th >客服电话</th>
				<td><input name="customerService" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >运维电话</th>
				<td><input name="operationPhone" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >移动电话</th>
				<td><input name="mobilePhone"  style="width:200px;" /></td>
			</tr>
		</table>
	</form>
</div>