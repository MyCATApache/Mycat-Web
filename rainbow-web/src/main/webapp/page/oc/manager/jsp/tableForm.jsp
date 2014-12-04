<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div id="oc_table_formTab" class="easyui-tabs" data-options="fit:true,boder:false,onSelect:oc_table_form">
<input type="hidden" id="oc_table_formFlag"/>
	<div align="center" title="全局表">
	<br>
	<form id="oc_table_globalForm" method="post">
		<input name="guid"  type="hidden" />
		<input id="od_table_type" type="hidden" name="type" value="global">
		<input type="hidden" name="state" value="closed">
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width:20%">表名称</th>
				<td><input name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">分片节点</th>
				<td><input id="oc_table_datanode" name="datanode" class="easyui-combobox" style="width:370px;"
						   data-options="
							url:'./dispatcherAction/comboxCode.do?service=datanodeService&method=query',
							valueField:'name',
							textField:'name',
							multiple:true,
							panelHeight:'auto'"/></td>
			</tr>
		</table>
	</form>
</div>
<div align="center" title="一般表">
	<br>
	<form id="oc_table_defaultForm" method="post">
	<input name="guid"  type="hidden" />
	<input id="od_table_type" type="hidden" name="type" value="default">
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width:20%">表名称</th>
				<td><input name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">分片节点</th>
				<td><input id="oc_table_datanode" name="datanode" class="easyui-combobox" style="width:370px;"
						   data-options="
							url:'./dispatcherAction/comboxCode.do?service=datanodeService&method=query',
							valueField:'name',
							textField:'name',
							multiple:true,
							panelHeight:'auto'"/></td>
			</tr>
			<tr>
				<th style="width:20%">分片规则</th>
				<td><input  id="oc_table_rule" name="rule" class="easyui-combobox" style="width:370px;"
						   data-options="
							url:'./dispatcherAction/comboxCode.do?service=tableruleService&method=query',
							valueField:'name',
							textField:'name',
							panelHeight:'auto'"/></td>
			</tr>
			<tr>
				<th style="width:20%">是否有子表</th>
				<td><input id="oc_table_state" name="state" class="easyui-combobox" style="width:370px;"
						   data-options="
							url:'./dispatcherAction/comboxCode.do?code=IS_MASTER_TABLE',
							valueField:'value',
							textField:'text',
							panelHeight:'auto'"/></td>
			</tr>
			<tr>
				<th style="width:20%">父表</th>
				<td>
					<input id="oc_table_parentName" name="parentName" class="easyui-combotree" data-options="url:'./dispatcherAction/queryComboxTree.do?service=tableService&method=queryComboxTree',parentField:'name',lines:true,editable:true" style="width:360px;"><img src="./ui/style/images/extjs_icons/cut_red.png" onclick="$('#oc_table_parentName').combotree('clear');" />
				</td>
			</tr>
			<tr>
				<th style="width:20%">关联KEY</th>
				<td><input name="joinkey" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">父级KEY</th>
				<td><input name="parentkey" style="width:90%;"/></td>
			</tr>
		</table>
	</form>
</div>
</div>
<script type="text/javascript">
var oc_host_stateSelect = function(record){
	
	if(record.value == "closed"){
		$('#oc_table_parentName').combobox('enable');
	}else{
		$('#oc_table_parentName').combobox('clear');
		$('#oc_table_parentName').combobox('disable');
	}
};
var oc_table_form = function(title){
	if(title == '全局表'){
		$('#oc_table_formFlag').val('oc_table_globalForm');
	}else{
		$('#oc_table_formFlag').val('oc_table_defaultForm');
	}
};
var oc_table_globalLoad = function(data){
	$('#oc_table_formTab').tabs('select','全局表');
};
var oc_table_defaultLoad = function(data){
	$('#oc_table_formTab').tabs('select','一般表');
};
$(function($){
	
	$('#oc_table_globalForm').form({onLoadSuccess:oc_table_globalLoad});
	$('#oc_table_defaultForm').form({onLoadSuccess:oc_table_defaultLoad});
	
});
</script>