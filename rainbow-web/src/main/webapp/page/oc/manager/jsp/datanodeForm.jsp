<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<br/>
	<form id="oc_datanode_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width:20%">分片节点</th>
				<td><input name="name" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">物理节点</th>
				<td><input id="oc_host_parentHost" name="datahost" class="easyui-combobox" style="width:370px;"
						   data-options="
							url:'./dispatcherAction/comboxCode.do?service=datahostService&method=queryCombox',
							valueField:'name',
							textField:'name',
							panelHeight:'auto'"/></td>
			</tr>
			<tr>
				<th style="width:20%">物理库</th>
				<td><input name="database" style="width:90%;"/></td>
			</tr>
		</table>
	</form>
</div>