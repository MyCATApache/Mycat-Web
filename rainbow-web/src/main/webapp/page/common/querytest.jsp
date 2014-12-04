<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="whereex" tagdir="/WEB-INF/tags"%>
<form id="test" method="post">
<%-- <whereex:whereex whereName ="vtransportaddress" queryEvent="test()" clearEvent="cleal1()"/> --%>
<!-- <input type="text" id="ss"/> -->
<whereex:query whereName ="vtransportaddress" queryEvent="test()" clearEvent="cleal1()"/>
</form>
<script type="text/javascript">
// var datas = {'total':2,'rows':[{'title':'nima','value':'111'},{'title':'nima','value':'111'}]};
// var colunm = [[{field:'text',title:'text',width:100},{field:'value',title:'value',width:100}]]
var clear1 = function(){
	alert('clear');
};
var test = function(){
// 	var service = "hello";
// 	var method = "world";
// 	var key = service+method;
// 	put("'"+key+"'",'hello world');
	var node = serializeObject($('#test'));
	//JSON.stringify(datas)
	document.write(JSON.stringify(node));
// 	$('#ss').combogrid({panelWidth:200,
// 		panelHeight:100,
// 		idField:'value',
// 		textField:'text',
// 		columns:colunm
// 	});
// 	$('#ss').combogrid('grid').datagrid('loadData',datas);
};

var put = function(key,value){
	var o = new Object();
	o[key]=value;
	alert(o.helloworld);
}
</script>

