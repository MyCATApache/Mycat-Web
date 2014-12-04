<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="product_datagrid" title="应用系统维护" class="easyui-datagrid" 
	data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=productService&method=queryByPage',pagination:true,striped:true,idField:'guid',
			singleSelect:false,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:false,autoRowHeight:false,toolbar : '#product_toolbar',
			frozenColumns:[[
				{field:'guid',width:80,checkbox:true,title:'guid'},
		        {field:'caozuo',width:80,align:'center',sortable:true,formatter:caozuo,frozen:true,title:'操作'},
		        {field:'productCode',width:60,align:'left',sortable:true,frozen:true,title:'产品代码'},
		        {field:'productName',width:200,align:'left',sortable:true,frozen:true,title:'产品名称'}
		    ]]">
		<thead>
				<tr>
					<th data-options="field:'productType',width:100,align:'left',sortable:true,formatter:farmatterProductType">产品类型</th>
					<th data-options="field:'bizField',width:100,align:'left',sortable:true,formatter:farmatterBizField">业务板块</th>
					<th data-options="field:'productDigest',width:200,align:'left',sortable:true">产品描述</th>
					<th data-options="field:'productManager',width:100,align:'left',sortable:true">产品经理</th>
					<th data-options="field:'managerPhone',width:100,align:'left',sortable:true">联系电话</th>
					<th data-options="field:'company',width:200,align:'left',sortable:true">公司名称</th>
					<th data-options="field:'address',width:100,align:'left',sortable:true">公司地址</th>
					<th data-options="field:'customerService',width:100,align:'left',sortable:true">客服电话</th>
					<th data-options="field:'operationPhone',width:100,align:'left',sortable:true">运维电话</th>
					<th data-options="field:'mobilePhone',width:100,align:'left',sortable:true">移动电话</th>
				</tr>
		</thead>
</table>


<div id="product_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="add_product()" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">增加</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="copy_add_product();" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" style="float: left;">复制新增</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="batch_delete_product();" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" style="float: left;">批量删除</a>
	<div class="datagrid-btn-separator"></div>
	<a href="javascript:void(0);" onclick="product_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="product_mm" style="width:90px" >
		<div data-options="name:'productCode'">产品代码</div>
		<div data-options="name:'productName'">产品名称</div>
		<div data-options="name:'company'">公司名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:product_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#product_mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/sa/js/product.js"></script>