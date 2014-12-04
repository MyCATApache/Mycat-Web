<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<center>
	<div id="sys_ownermapdb_margin" style="margin-top: 100px;">
		<form id="sys_ownermapdb_addForm" method="post">
			<input name="goodsownerid" type="hidden" />
			<table class="tableForm" style="width: 80%">
				<tr id="sys_ownermapdb_dbsource">
					<th style="width: 20%;">平台名称:</th>
					<td><input class="easyui-combobox" style="width: 90%;"
						data-options="url:'./dispatcherAction/comboxCode.do?code=OWNERPLAT',
										valueField:'value',
										textField:'text',
										panelHeight:'auto',
										onSelect:sys_ownermapdb_showAnother,
										multiple:false" />
					</td>
				</tr>
				<tr style="display: none;">
					<th style="width:20%;">数据源名称:</th>
					<td><input id="sys_ownermapdb_dbName" name="dbName"
						style="width: 90%;" readonly="readonly" /></td>
				</tr>
				<tr id="sys_ownermapdb_owner">
					<th style="width:20%;">货主:</th>
					<td><input id="sys_ownermapdb_goodsowner"
						class="easyui-combogrid" style="width: 90%;" /></td>
				</tr>
				<tr style="display: none;">
					<th style="width: 20%;">货主代码:</th>
					<td><input name="gomacode" style="width: 90%;"
						readonly="readonly" /></td>
				</tr>
				<tr style="display: none;">
					<th style="width: 20%;">货主名称:</th>
					<td><input name="goodsownername" style="width: 90%;"
						readonly="readonly" /></td>
				</tr>
				<tr>
					<th style="width: 20%;">状态:</th>
					<td><input id="sys_ownermapdb_status" type="hidden"
						name="status" value="1" /><input class="easyui-combobox"
						data-options="panelHeight:60,data:[{id:1,text:'可用'},{id:0,text:'无效'}],valueField:'id',textField:'text',value:'可用',onSelect:sys_ownermapdb_getStatus"
						style="width: 330px;" /></td>
				</tr>
			</table>
		</form>
	</div>
</center>
<script>
	$(function() {
		if ('${param.u}' == '1') {
			$('#sys_ownermapdb_dbsource').remove();
			$('#sys_ownermapdb_owner').remove();
			$('#sys_ownermapdb_margin').removeAttr('style');
			$('tr').removeAttr('style');
			$('input').removeAttr('readonly');
		}
	});
	var sys_ownermapdb_showAnother = function(record) {
		var dbname = record.value;
		$('#sys_ownermapdb_dbName').val(dbname);
		$('#sys_ownermapdb_goodsowner')
				.combogrid(
						{

							url : "./dispatcherAction/query.do?service=goodsownerService&method=queryByPage&dbname="
									+ (dbname.toLowerCase()),
							panelWidth : 350,
							panelHeight : 330,
							fitColumns : true,
							textField : 'goodsownername',
							pagination : true,
							mode : 'remote',
							onSelect : sys_ownermapdb_fillAdd,
							columns : [ [ {
								field : 'goodsownerid',
								width : 200,
								hidden : true
							},  {
								field : 'gomacode',
								title : '货主代码',
								width : 100
							},{
								field : 'goodsownername',
								title : '货主名称',
								width : 250
							}] ]
						});

	};
	var sys_ownermapdb_fillAdd = function(index, rowData) {
		$('#sys_ownermapdb_addForm').form('load', rowData);
	};
	var sys_ownermapdb_getStatus = function(record) {
		$('#sys_ownermapdb_status').val(record.id);
	};
</script>