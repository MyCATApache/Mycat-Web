<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="easyui-layout" data-options="fit : true,border : false">
	<div data-options="region:'north',title:'查询条件',border:false" style="height: 135px;overflow: hidden;" align="center">
			<form id="licenses_queryForm" >
				<table class="tableForm" style="width: 100%">
					<tr>
						<th style="width: 90px;">许可证代码</th>
						<td><input name="licenseCode" style="width: 200px;" /></td>
						<th style="width: 90px;">客户名称</th>
						<td><input name="customerName" style="width: 200px;" /></td>
						<th style="width: 90px;">产品名称</th>
						<td><input name="productName" style="width: 200px;" /></td>
					</tr>
					<tr>
						<th style="width: 90px;">签发时间</th>
						<td><input name="signingDate" class="easyui-datebox"  style="width:205px;"  /></td>
						<th style="width: 90px;">起始时间</th>
						<td><input name="startDate" class="easyui-datebox"  style="width:205px;"  /></td>
						<th style="width: 90px;">过期时间</th>
						<td><input name="expiringDate" class="easyui-datebox"  style="width:205px;"  /></td>
					</tr>
					<tr>
						<td colspan="6" align="left"><div align="right" style="vertical-align: top;"><a href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="licenses_query();">条件过滤</a> <a
							href="javascript:void(0);" class="easyui-linkbutton"
							data-options="iconCls:'icon-cancel',plain:true"
							onclick="licenses_query_clear();">清空条件</a></div></td>
					</tr>
				</table>
			</form>
			
	</div>
	<div data-options="region:'center',border:false">
	<table id="licenses_datagrid" title="查询结果" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=licensesService&method=queryByPage',pagination:true,striped:true,idField:'guid',
					singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:false,autoRowHeight:false,toolbar : '#licenses_toolbar',
					frozenColumns:[[
				        {field:'guid',width:80,hidden:true,title:'guid'},
				        {field:'caozuo',width:80,align:'center',sortable:true,formatter:caozuo,frozen:true,title:'操作'},
				        {field:'licenseCode',width:100,align:'left',sortable:true,title:'许可证代码'},
				        {field:'customerName',width:200,align:'left',sortable:true,title:'客户名称'},
				        {field:'productName',width:200,align:'left',sortable:true,title:'产品名称'},
				    ]]">
		<thead>
				<tr>
					<th data-options="field:'versionNumber',width:100,align:'left',sortable:true">产品版本</th>
					<th data-options="field:'signingDate',width:100,align:'left',sortable:true">签发时间</th>
					<th data-options="field:'startDate',width:100,align:'left',sortable:true">起始时间</th>
					<th data-options="field:'expiringDate',width:100,align:'left',sortable:true">过期时间</th>
					<th data-options="field:'licVersion',width:100,align:'left',sortable:true">许可证版本</th>
					<th data-options="field:'productCompany',width:200,align:'left',sortable:true">签发单位</th>
					<th data-options="field:'licenseMode',width:100,align:'left',sortable:true,formatter:authorModelsFormatter">许可证模式</th>
					<th data-options="field:'encryptModel',width:100,align:'left',sortable:true,formatter:encryptModelsFormatter">加密模式</th>
				</tr>
		</thead>
	</table>
	</div>
</div>

<div id="licenses_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="add_licenses()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="copy_add_licenses();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="licenses_mm" style="width:90px" >
		<div data-options="name:'licenseCode'">证书代码</div>
		<div data-options="name:'customerName'">客户名称</div>
		<div data-options="name:'productName'">产品名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:licenses_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#licenses_mm'" style="width:300px;" />
</div>

<script type="text/javascript" src="./page/sa/js/licenses.js"></script>
<div id="licenses_dlg"  class="easyui-dialog" style="width: 420px; height: 520px; padding: 10px 20px" closed="true" modal="true">
<br/>
	<div align="center">
	<form id="licenses_show" method="post">
		<table class="tableForm" style="width: 80%">
			<tr>
				<th style="width: 120px">客户名称</th>
				<td><input class="easyui-combobox" style="width:205px;" 
					name="customerName" /></td>
			</tr>
			<tr>
				<th style="width: 120px">产品名称</th>
				<td><input class="easyui-combobox" style="width:205px;" 
					name="productName" /></td>
			</tr>
			<tr>
				<th style="width: 120px">许可证代码</th>
				<td><input name="licenseCode" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>	
				<th style="width: 120px">许可证版本</th>
				<td><input name="licVersion" class="easyui-validatebox" data-options="required:true" style="width:200px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">许可证模式</th>
				<td><input class="easyui-combobox" style="width:205px;" value="01"
					name="licenseMode"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=AUTHOR_MODEL',
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							editable:false,
							required:true
					"/></td>
				</tr>
			<tr>
				<th style="width: 120px">加密模式</th>
				<td><input class="easyui-combobox" style="width:205px;" value="SYSTEM"
					name="encryptModel"
					data-options="
							url:'./dispatcherAction/comboxCode.do?service=rsaService&method=queryCombox',
							valueField:'code',
							textField:'name',
							panelHeight:'auto',
							editable:false,
							required:true
					"/>
				</td>
			</tr>
			<tr>
				<th style="width: 120px">起始时间</th>
				<td><input name="startDate"  style="width:200px;" /></td>
			</tr>
			<tr>	
				<th style="width: 120px">过期时间</th>
				<td><input name="expiringDate"  style="width:200px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">签发时间</th>
				<td><input name="signingDate"  style="width:200px;" /></td>
			</tr> 
			<tr>	
				<th style="width: 120px">产品版本</th>
				<td><input name="versionNumber" class="easyui-validatebox" required="required" style="width:200px;" /></td>
			</tr>
			<tr>
				<th style="width: 120px">签发单位</th>
				<td colspan="3"><input name="productCompany" class="easyui-validatebox" required="required" style="width:200px;" /></td>
			</tr>
		</table>
	</form>
	</div>
</div>