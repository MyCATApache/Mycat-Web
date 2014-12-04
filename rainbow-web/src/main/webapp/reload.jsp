<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
session.invalidate();
%>
<script type="text/javascript">
alert("当前会话过期,请重新登录!");
window.location.href="./login.jsp";
</script>