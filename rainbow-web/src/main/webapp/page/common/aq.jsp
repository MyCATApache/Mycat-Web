<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<style>
<!--
.aqFormheard {
	border-collapse: collapse;
}

.aqFormbody {
	border-collapse: collapse;
}

.aqFormheard  td {
	border-left: 1px solid #ccc;
	border-right: 1px solid #ccc;
	border-bottom: 0px solid #ccc;
	border-top: 1px solid #ccc;
	background: #F4F4F4;
	font-size: 12px;
	text-align: center;
	font-family: arial;
	font-weight: bold;
	padding: 6px 6px 6px 6px;
	color: #281e78;
}

.aqFormbody  td {
	text-align: left;
	border: 1px solid #ccc;
	font-size: 12px;
	font-family: arial;
	font-weight: bold;
	padding: 6px 6px 6px 6px;
	color: #281e78;
}
-->
</style>
<form id="${param.whereName}_queryForm" method="post">
	<div align="center" style="height: 190px; overflow-x: hidden;">

		<table id="tag_aq_table_${param.whereName}" style="width: 100%;" class="aqFormbody">
			<thead class="aqFormheard">
				<tr align="center">
					<td width="25%">属性</td>
					<td width="17%">条件</td>
					<td width="50%">值</td>
					<td width="8%"><a href="javascript:void(0);"
						id="${param.whereName}_removeButton0" class="easyui-linkbutton"
						data-options="iconCls:'icon-add',plain:true"
						onclick="tag_aq_add('tag_aq_table_${param.whereName}','${param.whereName}',${param.whereName}_count++,${param.whereName}_fieldData)"></a></td>
				</tr>
			</thead>
			<tbody class="aqFormbody">
				<tr align="left">
					<td align="center" width="25%"><input
						id="${param.whereName}_field_valueType0" type="hidden"
						name="valueType" />
						<input id="${param.whereName}_field_combobox0"
						class="easyui-combobox"
						data-options="id:'0',name:'${param.whereName}',data: ${param.whereName}_fieldData,panelWidth:140,panelHeight:'auto',valueField:'code',textField:'describe',onSelect:tag_aq_fieldName_value"
						style="width: 150px" name="field" /></td>
					<td align="center" width="17%"><input
						id="${param.whereName}_symbol_combobox0" class="easyui-combobox"
						style="width: 80px;" name="symbol"
						data-options="id:'0',name:'${param.whereName}',panelWidth:80,
									panelHeight:'auto',
									valueField:'value',
									textField:'text',
									value:'=',
									onSelect:tag_aq_symbol_value,
									data:symbol_data" /></td>
					<td align="center" width="50%" id="${param.whereName}valueData_0"><span
						id="${param.whereName}_span0""><input
							id="${param.whereName}_fieldValue0" name="value"
							style="width: 290px;" /></span>
					</td>
					<td align="center" width="8%"><a href="javascript:void(0);"
						id="${param.whereName}_removeButton0" class="easyui-linkbutton"
						data-options="iconCls:'icon-remove',plain:true"
						onclick="tag_aq_remove(this)"></a>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</form>