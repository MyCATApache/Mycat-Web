<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<form id="${param.whereName}_queryForm" method="post">
	<div align="center">
	<table id="tag_aq_table_${param.whereName}" style="width:100%;" class="tableForm">
		<tbody>
			<tr align="left">
				<td><font style="font-weight:900;">属性：</font><input id="${param.whereName}_field_valueType0" type="hidden" name="valueType"/><input
							id="${param.whereName}_field_combobox0" class="easyui-combobox"
							data-options="id:'0',name:'${param.whereName}',data: ${param.whereName}_fieldData,panelWidth:140,panelHeight:'auto',valueField:'code',textField:'describe',onSelect:tag_aq_fieldName_value"
							style="width: 140px;" name="field" />&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-weight:900;">条件：</font><input
							id="${param.whereName}_symbol_combobox0" class="easyui-combobox"
							style="width: 120px;" name="symbol"
							data-options="id:'0',name:'${param.whereName}',panelWidth:120,
									panelHeight:'auto',
									valueField:'value',
									textField:'text',
									value:'=',
									onSelect:tag_aq_symbol_value,
									data:symbol_data" />&nbsp;&nbsp;<br><font style="font-weight:900;" color="red">&nbsp;&nbsp;&nbsp;&nbsp;值：</font><span id="${param.whereName}_span0""><input
							id="${param.whereName}_fieldValue0" name="value" style="width: 314px;" /></span><a
							href="javascript:void(0);" id="${param.whereName}_removeButton0"
							class="easyui-linkbutton"
							data-options="iconCls:'icon-add',plain:true"
							onclick="tag_aq_add('tag_aq_table_${param.whereName}','${param.whereName}',${param.whereName}_count++,${param.whereName}_fieldData)"></a></td>
			</tr>
		</tbody>
	</table>
	</div>
</form>