<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag body-content="empty"%>
<%@ tag dynamic-attributes="tagAttrs"%>
<%@ attribute name="headName" required="true"%>
<%@ attribute name="whereName" required="true"%>
<%@ attribute name="gridQuery" required="true"%>
<%@ attribute name="gridId" required="true"%>
<div id="tag_query_${whereName}_panel" class="easyui-panel" data-options="closable:true,onClose:tag_query_colse" title="查询条件" style="height:140px;">
<form id="${whereName}_queryForm" method="post">
<div style="height:75px;overflow-x:hidden;" >
	<table id="tag_query_table_${whereName}" style="width: 100%;" class="tableForm">
		<tbody>
			<tr align="left">
				<td><font style="font-weight:900;">属性：</font>
						    <input id="${whereName}_field_valueType0"  type="hidden"/>
							<input id="${whereName}_field_combobox0" class="easyui-combobox"
							data-options="id:'0',name:'${whereName}',panelWidth:140,panelHeight:'auto',valueField:'code',textField:'describe',onSelect:tag_query_fieldName_value"
							style="width: 100px;" name="field" />&nbsp;&nbsp;<font style="font-weight:900;">条件：</font><input
							id="${whereName}_symbol_combobox0" class="easyui-combobox"
							style="width: 65px;" name="symbol"
							data-options="id:'0',name:'${whereName}',panelWidth:65,
									panelHeight:'auto',
									valueField:'value',
									textField:'text',
									value:'=',
									onSelect:tag_query_symbol_value,
									data:symbol_data" />&nbsp;&nbsp;<font style="font-weight:900;">值：</font><input
							id="${whereName}_fieldValue0" name="value" style="width: 230px;" /><a
							href="javascript:void(0);" id="${whereName}_removeButton0"
							class="easyui-linkbutton"
							data-options="iconCls:'icon-add',plain:true"
							onclick="tag_query_add('tag_query_table_${whereName}','${whereName}',${whereName}_count++,${whereName}_fieldData)"></a></td>
			</tr>
		</tbody>
	</table>
</div>
<table style="width: 100%;" >
	<tr>
		<th style="width: 50%"></th>
		<td style="width: 50%;" align="right"><a
			href="javascript:void(0);" class="easyui-linkbutton"
			data-options="iconCls:'icon-search',plain:true"
			onclick="tag_query('${whereName}','#${gridId}');">条件过滤</a> <a href="javascript:void(0);"
			class="easyui-linkbutton"
			data-options="iconCls:'icon-clear',plain:true"
			onclick="tag_queryClear('${whereName}','#${gridId}',${whereName}_count);">清空条件</a></td>
	</tr>
</table>
</form>
</div>
<div id="${gridQuery}" style="display:none;">
	<span onclick="tag_ddlSreach('${whereName}','${headName}',${whereName}_fieldData,'${grid}')" style="cursor:pointer"><font style="font-weight:900;"><font color="#22196a">高级查询</font></font><a class="icon-search"></a></span>
</div>
<script>
	var ${whereName}_count = 1;
	var ${whereName}_fieldData =[];
	
	$(function($) {
		rainbowAjax.post('./dispatcherAction/query.do?service=whereexService&method=query&whereName=${whereName}',function(data) {
			var rows = data.rows;
			if (rows.length > 0) {
				${whereName}_fieldData = rows;
				$('#${whereName}_field_combobox0').combobox('loadData', rows);
			} else {
				$('#tag_query_table_${whereName}').hide();
			};
		});
	});
	
	var tag_query_colse = function(){
		$('#${headName}').layout('remove','north');
	};
</script>