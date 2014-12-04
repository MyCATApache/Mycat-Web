<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="org.hx.rainbow.common.context.RainbowProperties"%>
<%
String loginUrl = RainbowProperties.getProperties("client.service") + "/index.jsp";
response.sendRedirect(loginUrl);
%>