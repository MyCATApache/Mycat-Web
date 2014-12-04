<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',split:true,title:'反馈列表'" style="width:220px;">
		<table id="left_suggestion_datagrid" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=suggestionService&method=queryByUser',striped:true,idField:'guid',
					fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,singleSelect:true,
					onSelect:left_suggestion_onClick,
					frozenColumns:[[
				        {field:'caozuo',width:50,align:'center',sortable:true,formatter:left_suggestion_caozuo,frozen:true,title:'操作'}
				  ]]">
		<thead>
				<tr>
					<th data-options="field:'guid',hidden:true"></th>
					<th data-options="field:'title',width:80,align:'left',sortable:true">意见标题</th>
					<th data-options="field:'suggestion',hidden:true"></th>
					<th data-options="field:'type',hidden:true"></th>
					<th data-options="field:'funModules',hidden:true"></th>
					<th data-options="field:'sugLevel',hidden:true"></th>
					<th data-options="field:'sugStatus',hidden:true"></th>
					<th data-options="field:'reply',hidden:true"></th>
				</tr>
		</thead>
	</table>
	</div>
	<div data-options="region:'center',title:'意见反馈'" align="center">
	<form id="north_suggestion_addForm" method="post">
			<input name="guid" type="hidden" />
			<table class="tableForm" style="width:90%">
				<tr>
					<th>意见类型</th>
					<td><input id="north_type_combobox" name="type" /></td>
					<th >级别</th>
					<td><input id="north_sugLevel_combobx" name="sugLevel"  /></td>
					<th>状态</th>
					<td><input id="north_sugStatus_combobx" name=sugStatus  readonly="readonly"/></td>
				</tr>
				<tr>
					<th >意见标题</th>
					<td colspan="5"><input name="title"  style="width:100%;"/></td>
				</tr>
				<tr>
					<th >意见涉及模块</th>
					<td colspan="5"><input name="funModules"  style="width:100%;"/></td>
				</tr>
				<tr>
					<th >意见内容</th>
					<td colspan="5"><textarea id="north_sug_suggestion" name="suggestion" style="width:100%;height:150px;"></textarea></td>
				</tr>
				<tr>
					<th >系统回复</th>
					<td colspan="5"><textarea name="reply"  style="width:100%;height:150px;"  disabled="disabled"></textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<script type="text/javascript" src="./page/system/js/suggestionNorth.js"></script>