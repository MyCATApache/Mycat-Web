<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<table id="author_datagrid" title="客户信息" class="easyui-datagrid" 
			data-options="url:'${pageContext.request.contextPath}/dispatcherAction/query.do?service=productService&method=queryAuthrCustomer&guid=${param.guid}',pagination:true,striped:true,idField:'guid',
					singleSelect:true,fit:true,remoteSort:false,rownumbers:true,border:false,fitColumns:true,autoRowHeight:false">
		<thead>
				<tr>
					<th data-options="field:'guid',width:80,hidden:true">guid</th>
					<th data-options="field:'code',width:100,align:'left',sortable:true">客户编号</th>
					<th data-options="field:'name',width:100,align:'left',sortable:true">客户名称</th>
					<th data-options="field:'linkman',width:100,align:'left',sortable:true">联系人</th>
					<th data-options="field:'telephony',width:100,align:'left',sortable:true">联系电话</th>
					<th data-options="field:'eMail',width:200,align:'left',sortable:true">邮箱地址</th>
					<th data-options="field:'licensesId',width:100,align:'center',sortable:true,formatter:author"><font color="red">授权</font></th>
				</tr>
		</thead>
</table>

<script type="text/javascript">
var author = function(value, row, index){
	if(value == null){
		return formatString('<a href="javascript:void(0);" onclick="author_start(\'{0}\');" class="easyui-linkbutton" data-options="plain:true" style="float: center;" >授权</a>',row.guid);
	}else{
		return formatString('<a href="javascript:void(0);" onclick="cancel_author(\'{0}\');" class="easyui-linkbutton" data-options="plain:true" style="float: center;" >取消授权</a>&nbsp;<a href="./licensesAction/download.do?guid={1}"  class="easyui-linkbutton" data-options="plain:true" style="float: center;" >证书下载</a>', row.guid,row.licensesId);
	}
};

var author_start = function(code){
	if (code != undefined) {
		$('#author_datagrid').datagrid('selectRecord', code);
	}
	var customer = $('#author_datagrid').datagrid('getSelected');
	var product = $('#product_datagrid').datagrid('getSelected');
	var datas = {"productCode":product.productCode,
				"productName":product.productName,
				"customerCode":customer.code,
				"customerName":customer.name};
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-save',
		handler : function() {
			$.messager.progress();
			var isValid = $('#licenses_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#licenses_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("licensesService");
			rainbow.setMethod("insert");
			sendCommand(rainbow,"post",new authorBack(d));
			}
		}];
	rainbowDialog.editDialog('./page/sa/jsp/licensesForm.jsp',buttons,800,400,true,'授权信息维护','licenses_addForm',datas);
};


var cancel_author = function(code){
	if (code != undefined) {
		$('#author_datagrid').datagrid('selectRecord', code);
	}
	var customer = $('#author_datagrid').datagrid('getSelected');
	var rainbow = new Rainbow();
	var data = [{"guid":customer.licensesId}];
	rainbow.setRows(data);
	rainbow.setService("licensesService");
	rainbow.setMethod("delete");
	rainbowAjax.excute(rainbow,new Callback());
};

var download_license = function(code){
	if (code != undefined) {
		$('#author_datagrid').datagrid('selectRecord', code);
	}
	var customer = $('#author_datagrid').datagrid('getSelected');
	$.get("./licensesAction/download.do?guid=" + customer.licensesId);
};
var authorBack=function(dialog){
	this.onSuccess=function(data){
		try {
			if (data.success) {
				$('#author_datagrid').datagrid('reload');
				if(dialog){
					dialog.dialog('destroy');
				}
			}
			$.messager.progress('close');
			$.messager.show({
				title : '提示',
				msg : data.msg
			});
		} catch (e) {
			$.messager.progress('close');
			$.messager.alert('提示', "系统异常!");
		}
	};
	this.onFail = function(jqXHR, textStatus, errorThrown){
		$.messager.progress('close');
		$.messager.alert('提示', "系统异常!");
	};
};


</script>