<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	session.invalidate();
	String error = request.getParameter("error");
	String errorMsg = "";
	if(error != null){
		if(error.equals("0")){
			errorMsg = "验证码错误!";
		}else if(error.equals("2")){
			errorMsg = "用户名或密码错误!";
		}else if(error.equals("3")){
			errorMsg = "该账户已经被锁定!";
		}
	}
	System.out.println(errorMsg);
	
%>
<HTML><HEAD><TITLE>Mycat 管理平台</TITLE>
<link rel="Shortcut Icon" href="./ui/style/images/save.ico?v=0.1">
<META http-equiv=Content-Language content=zh-cn>
<META http-equiv=Content-Type content="text/html; charset=UTF-8">
<META content="MSHTML 6.00.2800.1611" name=GENERATOR>
<script type="text/javascript" src="./ui/jslib/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript">
	$(function() {
		$('#kaptchaImage').click(
				function() {
					$(this).attr('src','Kaptcha.jpg?' + Math.floor(Math.random() * 100));
				});
	});
	
	function refreshCode(){
		$('#kaptchaImage').attr('src','Kaptcha.jpg?' + Math.floor(Math.random() * 100));
	}

	$(document).ready(function(){
		var $account 	= $("#txt_account");
		var $passWord 	= $("#txt_password");
		var $verificationCode 	= $("#txt_code");
		var strDefTips = "请输入用户名";

		//表单提交事件
		$("form").submit(function(){
				var passWord 	= $.trim($passWord.val());
				var code        = $.trim($verificationCode.val());
				//获得提示
				var $msg = $("#message");
		
				//设置错误消息
				var accountEmpty = '用户名为空，请输入用户名';
				var passWordEmpty = '密码为空，请输入密码';
				var codeEmpty = '验证码为空，请输入验证码';
				
				if($account.val() == "") {
					$msg.html(accountEmpty);
					$account.focus();
					return false;
				}else if($account.val() == strDefTips){
					$account.val("");
					$msg.html(accountEmpty);
					$account.focus();
					return false;
				}
				if(passWord == "") {
					$msg.html(passWordEmpty);
					$passWord.focus();
					return false;
				}
				if(account != "admin"){
					if(code == "") {
						$msg.html(codeEmpty);
						$verificationCode.focus();
						return false;
					}
				}
		});
		
		$account.keydown(function(e){
			
			if($(this).val() == strDefTips){
				$account.addClass("gray");
			}else{
				$account.removeClass("gray");
			}

			if(e.keyCode == 9)
			{
				e.preventDefault();
				$passWord.focus();
			}
		});
		
		DisplayAccount("select");
		
		function EventBind()
		{
			$account.blur(function(){
				DisplayAccount("blur");
			}).focus(function(){
				DisplayAccount("focus");
			}).click(function(){
				DisplayAccount("click");
			});
		}

		setTimeout(EventBind,1000);
		
		function DisplayAccount(state)
		{
			if(state == "blur")
			{
				if($.trim($account.val()) == "")
				{
					$account.val(strDefTips).addClass("gray");
				}
				else if($account.val() == strDefTips)
				{
					$account.addClass("gray");
				}
				else
				{
					$account.removeClass("gray");
				}
			}
			else if(state == "focus")
			{
				if($account.val() == strDefTips)
				{
					$account.val("").removeClass("gray");
				}
			}
			else if(state == "click")
			{
				if($account.val() == strDefTips)
				{
					$account.val("").removeClass("gray");
				}
			}
			else if(state == "select")
			{
				if($account.val() == "")
				{
					$account.val(strDefTips).addClass("gray").select();
				}
				else if($account.val() == strDefTips)
				{
					$account.addClass("gray");
				}
				else
				{
					$account.removeClass("gray").select();
				}
			}
		}
	});
	function get_login_code()
	{
		document.getElementById('LoginImg').src="/getverifyimg?id="+Math.random()+'&type=login';
	}
</script>
<LINK href="./ui/style/images/login/css1.css" type=text/css rel=stylesheet><LINK 
href="./ui/style/images/login/newhead.css" type=text/css rel=stylesheet></HEAD>
<BODY bgColor=#eef8e0 leftMargin=0 topMargin=0 MARGINWIDTH="0" MARGINHEIGHT="0">
<form name=adminlogin id = "adminlogin" action="./rainbowlogin" method="post">
<div align="center">
<TABLE cellSpacing=0 cellPadding=0 width=1004 border=0>
  <TBODY>
  <TR>
    <TD colSpan=6><IMG height=92 alt="" src="./ui/style/images/login/crm_1.gif" 
    width=345></TD>
    <TD colSpan=4><IMG height=92 alt="" src="./ui/style/images/login/crm_2.gif" 
    width=452></TD>
    <TD><IMG height=92 alt="" src="./ui/style/images/login/crm_3.gif" width=207></TD></TR>
  <TR>
    <TD colSpan=6><IMG height=98 alt="" src="./ui/style/images/login/crm_4.gif" 
    width=345></TD>
    <TD colSpan=4><IMG height=98 alt="" src="./ui/style/images/login/crm_5.gif" 
    width=452></TD>
    <TD><IMG height=98 alt="" src="./ui/style/images/login/crm_6.gif" width=207></TD></TR>
  <TR>
    <TD rowSpan=5><IMG height=370 alt="" src="./ui/style/images/login/crm_7.gif" 
    width=59></TD>
    <TD colSpan=5><IMG height=80 alt="" src="./ui/style/images/login/crm_8.gif" 
    width=286></TD>
    <TD colSpan=4><IMG height=80 alt="" src="./ui/style/images/login/crm_9.gif" 
    width=452></TD>
    <TD><IMG height=80 alt="" src="./ui/style/images/login/crm_10.gif" width=207></TD></TR>
  <TR>
    <TD><IMG height=110 alt="" src="./ui/style/images/login/crm_11.gif" width=127></TD>
    <TD background=./ui/style/images/login/crm_12.gif colSpan=6>
      <TABLE id=table1 cellSpacing=0 cellPadding=0 width="98%" border=0>
        <TBODY>
        <TR>
          <TD>
            <TABLE id=table2 cellSpacing=1 cellPadding=0 width="100%" 
              border=0><TBODY>
              <tr>
              	  <TD align=middle width=81>&nbsp;</TD>	
	              <TD>
		              <div id="message" class="message"><%=errorMsg%></div>
	              </TD>
	          </tr>
              <TR>
                <TD align=middle width=81><FONT color=#ffffff>用户名：</FONT></TD>
                <TD><INPUT id="txt_account" class=regtxt title=请填写用户名 maxLength=16 size=16 name="userName"></TD></TR>
              <TR>
                <TD align=middle width=81><FONT color=#ffffff>密&nbsp;&nbsp;&nbsp;&nbsp;码：</FONT></TD>
                <TD><INPUT id = "txt_password" class=regtxt title=请填写密码 type=password maxLength=16 
                  size=16 name="password"></TD></TR>
              <TR>
                <TD align=middle width=81><FONT color=#ffffff></FONT></TD>
                <TD>
                <TABLE cellSpacing=0 cellPadding=0 border=0>
                <tr><td>
               	</td>
                  <td>
                  </td></tr></TABLE></TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD>
    <TD colSpan=2 rowSpan=2><IMG height=158 alt="" 
      src="./ui/style/images/login/crm_13.gif" width=295></TD>
    <TD rowSpan=2><IMG height=158 alt="" src="./ui/style/images/login/crm_14.gif" 
      width=207></TD></TR>
  <TR>
    <TD rowSpan=3><IMG height=180 alt="" src="./ui/style/images/login/crm_15.gif" 
      width=127></TD>
    <TD rowSpan=3><IMG height=180 alt="" src="./ui/style/images/login/crm_16.gif" 
    width=24></TD>
    <TD><INPUT title=登录后台 type=image height=48 alt="" width=86 
      src="./ui/style/images/login/crm_17.gif" name=image></TD>
    <TD><IMG height=48 alt="" src="./ui/style/images/login/crm_18.gif" width=21></TD>
    <TD colSpan=2><A href="http://www.fdkjgz.com/shop/index.asp"><IMG 
      title=返回首页 height=48 alt="" src="./ui/style/images/login/crm_19.gif" width=84 
      border=0></A></TD>
    <TD><IMG height=48 alt="" src="./ui/style/images/login/crm_20.gif" width=101></TD></TR>
  <TR>
    <TD colSpan=5 rowSpan=2><IMG height=132 alt="" 
      src="./ui/style/images/login/crm_21.gif" width=292></TD>
    <TD rowSpan=2><IMG height=132 alt="" src="./ui/style/images/login/crm_22.gif" 
      width=170></TD>
    <TD colSpan=2><IMG height=75 alt="" src="./ui/style/images/login/crm_23.gif" 
    width=332></TD></TR>
  <TR>
    <TD colSpan=2><IMG height=57 alt="" src="./ui/style/images/login/crm_24.gif" 
    width=332></TD></TR>
  <TR>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=59></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=127></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=24></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=86></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=21></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=28></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=56></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=101></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=170></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" width=125></TD>
    <TD><IMG height=1 alt="" src="./ui/style/images/login/spacer.gif" 
  width=207></TD></TR></TBODY></TABLE>
  </div>
  </form></BODY></HTML>
