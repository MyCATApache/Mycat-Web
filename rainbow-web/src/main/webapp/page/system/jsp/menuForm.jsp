<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
<br/>
	<form id="sys_menu_addForm" method="post" style="width: 90%;">
	<input name="guid"  type="hidden"  />
		<table class="tableForm">
			<tr>
				<th style="width: 20%;">菜单代码</th>
				<td><input name="code" class="easyui-validatebox" data-options="required:true" style="width: 90%;"/>
				</td>
			</tr>
			<tr>
				<th style="width: 20%;">菜单名称</th>
				<td><input name="name" class="easyui-validatebox" data-options="required:true" style="width: 90%;"/>
				</td>
			</tr>
			<tr>
				<th style="width: 20%;">页面代码</th>
				<td><input name="pageCode" style="width: 90%;"/>
			</tr>
			<tr>
				<th style="width: 20%;">节点状态</th>
				<td><input class="easyui-combobox" style="width:380px;"
					name="state"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=IS_LEAF',
							valueField:'value',
							textField:'text',
							panelHeight:'auto'
					"/>
					</td></td>
				</tr>
			<tr>
			<tr>
				</td>
				<th style="width: 20%;">位置排序</th>
				<td><input name="sortIndex" style="width: 380px;" class="easyui-numberspinner" data-options="min:0,max:999,editable:false,required:true,missingMessage:'请选择菜单排序'" value="0" style="width: 155px;" />
				</td>
			</tr>
			<tr>
				<th style="width: 20%;">上级资源</th>
			 	<td ><input id="parentCodes" name="parentCode" class="easyui-combotree" data-options="url:'${pageContext.request.contextPath}/menusAction/queryComboxTree.do',parentField:'parentCode',lines:true,editable:true" style="width:380px;"><img src="./ui/style/images/extjs_icons/cut_red.png" onclick="$('#parentCodes').combotree('clear');" /> 
				</td>
			</tr>
		</table>
	</form>
</div>