<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="org.hx.rainbow.common.security.login.RainbowUser"%>

<%@page import="org.hx.rainbow.common.web.session.ThreadConstants" %>
<%
	RainbowUser user = (RainbowUser)request.getSession().getAttribute(ThreadConstants.RAINBOW_USER);
	if(user == null){
		response.sendRedirect("./reload.jsp");
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<title>MyCat 管理系统</title>
<meta http-equiv="content-type" content="text/html;charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta name="MSSmartTagsPreventParsing" content="True" />
<meta http-equiv="MSThemeCompatible" content="Yes" />
<meta http-equiv="x-ua-compatible" content="ie=8" />
<meta http-equiv="imagetoolbar" content="no" />
<jsp:include page="inc.jsp"></jsp:include>
</head>
<body id="rainbowIndex" class="easyui-layout">
	<div data-options="region:'north',href:'./page/layout/north.jsp',split:true" style="height: 65px;overflow: hidden;" class="logo"></div>
	<div data-options="region:'west',title:'功能导航菜单',href:'./page/layout/west.jsp',split:true" style="width: 200px;overflow: hidden;"></div>
	<div data-options="region:'center',href:'./page/layout/center.jsp'" style="overflow: hidden;"></div>
	<!-- <div data-options="region:'east',title:'日历',split:true" style="width: 1px;overflow: hidden;"></div> -->
	 <div data-options="region:'south',href:'./page/layout/south.jsp',split:true" style="height: 30px;overflow: hidden;"></div> 

</body>
</html>
<script type="text/javascript">
$(function() {
//$('#rainbowIndex').layout('collapse','east');
});
window.onload=function(){
	document.getElementsByTagName("body")[0].onkeydown =function(){
		if(event.keyCode==8){
			var elem = event.srcElement;
			var name = elem.nodeName;
			
			if(name!='INPUT' && name!='TEXTAREA'){
				event.returnValue = false ;
				return ;
			}
			var type_e = elem.type.toUpperCase();
			if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){
				event.returnValue = false ;
				return ;
			}
			if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){
				event.returnValue = false ;
				return ;
			}
		}
	};
};
</script>