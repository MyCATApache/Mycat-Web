<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="product_datagrid" title="应用系统维护" class="easyui-datagrid" 
	data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=productService&method=queryByPage',pagination:true,striped:true,idField:'guid',
			singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,lines:true,fitColumns:true,autoRowHeight:false,toolbar : '#authoriztion_toolbar'">
	<thead>
			<tr>
				<th data-options="field:'productCode',width:100,align:'left',sortable:true">产品代码</th>
				<th data-options="field:'productName',width:200,align:'left',sortable:true">产品名称</th>
				<th data-options="field:'company',width:200,align:'left',sortable:true">公司名称</th>
				<th data-options="field:'address',width:200,align:'left',sortable:true">公司地址</th>
				<th data-options="field:'customerService',width:100,align:'left',sortable:true">客服</th>
				<th data-options="field:'operationPhone',width:100,align:'left',sortable:true">运维电话</th>
				<th data-options="field:'mobilePhone',width:100,align:'left',sortable:true">移动电话</th>
				<th data-options="field:'shouquan',width:100,align:'center',sortable:true,formatter:shouquan">授权</th>
			</tr>
	</thead>
</table>


<div id="authoriztion_toolbar" style="display: none;">
	<a href="javascript:void(0);" onclick="authoriztion_reload();" class="easyui-linkbutton" data-options="iconCls:'icon-reload',plain:true" style="float: left;">刷新</a>
	<div class="datagrid-btn-separator"></div>
	<div id="mm" style="width:90px" >
		<div data-options="name:'productCode'">产品代码</div>
		<div data-options="name:'productName'">产品名称</div>
		<div data-options="name:'company'">公司名称</div>
	</div>
	<input class="easyui-searchbox" data-options="searcher:product_search,prompt:'请选择搜索条件,输入值后,回车',menu:'#mm'" style="width:300px;" />
</div>
<script type="text/javascript" src="./page/sa/js/product.js"></script>