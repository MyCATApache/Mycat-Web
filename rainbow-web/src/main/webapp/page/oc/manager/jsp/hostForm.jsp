<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="oc_host_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width:20%">物理节点名称</th>
				<td><input name="host" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">链接地址</th>
				<td><input name="url" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">用户</th>
				<td><input name="dUser" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">密码</th>
				<td><input name="password" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">服务器类型</th>
				<td><input id="oc_host_state" class="easyui-combobox"
					name="state"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=IS_MASTER',
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							onSelect:oc_host_stateSelect
					"/></td>
			</tr>
			<tr>
				<th style="width:20%">主服务器</th>
				<td><input id="oc_host_parentHost" name="parentHost" class="easyui-combobox" 
						   data-options="
							url:'./dispatcherAction/comboxCode.do?service=hostService&method=queryCombox',
							valueField:'id',
							textField:'text',
							panelHeight:'auto'"/>
				</td>
			</tr>
			
		</table>
	</form>
</div>

<script type="text/javascript">
var oc_host_stateSelect = function(record){
	if(record.value == "closed"){
		$('#oc_host_parentHost').combobox('clear');
		$('#oc_host_parentHost').combobox('disable');
	}else{
		$('#oc_host_parentHost').combobox('enable');
	}
}
</script>