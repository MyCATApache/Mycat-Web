<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',border:false" style="height: 65px;overflow: hidden;" align="center">
			<form id="build_form" >
				<table class="queryForm" style="width: 100%;">
					<tr>
						<th style="width:90px;">dblink名称：</th>
						<td><input class="easyui-combogrid"
									data-options="pagination:true,
													fitColumns:true,
													mode:'remote',
													idField:'dbLink',
													textField:'dbLink',
													panelWidth:350,
						            				panelHeight:340,
													method:'get',
													onSelect:build_loadTableName,
													url:'./dispatcherAction/query.do?service=builderService&method=queryDBLink',
													columns:[[   
						                				{field:'dbLink',title:'dbLink名称',width:200}
						                			]]"
									style="width:250px;"
									id="build_dblink"/></td>
						<th><input style="width:60px;" class="easyui-combobox" data-options="panelWidth:60,panelHeight:44,data:[{'value':1,'text':'表名'},{'value':2,'text':'视图名'}],value:'表名',valueField:'value',textField:'text',onSelect:build_changeBuilderMode"/></th>
						<td><input id="build_changeModel" class="easyui-combogrid" style="width: 250px;"
							data-options="
						            panelWidth:350,
						            panelHeight:340,   
						            fitColumns:true,
						            textField:'tableName',   
						            pagination:true,
						            mode:'remote',
						            onChange:build_onChange,
						            queryParams:{'test':'test'},
						            onSelect:build_getColnum,
							        url:'./dispatcherAction/query.do?service=builderService&method=queryCombox',   
						            columns:[[   
						                {field:'tableName',title:'表名',width:200}]]
						     ">
						</td>
					</tr>
					<tr>
						<th>模块代码：</th>
						<td><input id="build_modelName" name="modelName"
						     style="color: #cbcac9; width: 250px;"
							onfocus="build_init(this)"></td>
						<th>查询控件名称：</th>
						<td><input id="build_whereName" name="whereName"
						     style="color: #cbcac9; width: 250px;"></td>
					</tr>
				</table>
			</form>	
	</div>
	<div data-options="region:'center',border:false">
		<table id="build_datagrid" class="easyui-datagrid" title="脚本生成"
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=builderService&method=query',striped:true,pagination:false,
					onUnselect:build_updateRow,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar:'#build_toolbar'
					,onDblClickRow:build_editRow,frozenColumns:[[
				        {field:'build_caozuo',width:50,align:'center',sortable:true,formatter:build_caozuo,frozen:true,title:'操作'}
				    ]]">
			<thead>
				<tr>
					<th
						data-options="field:'columnName',width:120,align:'left',sortable:true,editor:'validatebox'">字段名</th>
					<th
						data-options="field:'dataType',width:200,align:'left',sortable:true,editor:'validatebox'">数据类型</th>
					<th
						data-options="field:'columnComment',width:150,align:'left',sortable:true,editor:'validatebox'">备注</th>
					<th
						data-options="field:'columnKey',width:150,align:'left',sortable:true">是否主键</th>
				</tr>
			</thead>
		</table>
	</div>
</div>
<input id="build_tableName" type="hidden" name="tableName" />
<div id="build_toolbar" style="display: none;">
		<table style="border: 0;height:25px;" >
		<tr>
			<td>
			<a href="javascript:void(0);" onclick="build_add()"
				class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true"
				style="float: left;">新增字段</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>	
			<a href="javascript:void(0);" onclick="build_commitEditor()"
				class="easyui-linkbutton"
				data-options="iconCls:'icon-save',plain:true" style="float: left;">保存</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" class="easyui-linkbutton" onclick="build_buildJsp();" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">生成JSP</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" class="easyui-linkbutton" onclick="build_buildJspForm();" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">生成JSPFORM</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" onclick="build_buildJs();"
				class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">生成JS</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" onclick="build_buildService();"
				class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">生成SERVICE</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" onclick="build_buildMapper();"
				class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">生成MAPPER</a>
			<div class="datagrid-btn-separator"></div>
			</td>
			<td>
			<a href="javascript:void(0);" onclick="build_insertIntoWhereEx();"
				class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"
				style="float: left;">whereEx</a>
			<div class="datagrid-btn-separator"></div>
			</td>
		</tr>
	</table>
</div>
<script src="./page/build/js/scriptbuild.js"></script>