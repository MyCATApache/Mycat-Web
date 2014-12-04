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
<HTML><LINK href="./ui/style/images/login/css1.css?v=1" type=text/css rel=stylesheet><LINK 
href="./ui/style/images/login/newhead.css" type=text/css rel=stylesheet>
<HEAD><TITLE>SAVE信息管理系统</TITLE>
<link rel="Shortcut Icon" href="./ui/style/images/save.ico?v=0.1">
<META http-equiv=Content-Language content=zh-cn>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
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
// 	function clear(){
		
// 		$('#adminlogin').
// 	}
</script>
</HEAD>
<BODY style="overflow: hidden;" scroll="no" bgColor='#e1e5ee' leftMargin=0 topMargin=0 MARGINWIDTH="0" MARGINHEIGHT="0">
<form name="adminlogin" id="adminlogin" action="./rainbowlogin" method="post">
<div align="center" style="margin-top:50px;height:100%;width:100%">
 <table  border="0" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td><img src="./ui/style/images/login/login_01.png" width="700" height="166"></td>
      <td><img src="./ui/style/images/login/login_02.png" width="324" height="166"></td>
    </tr>
    <tr>
      <td><img src="./ui/style/images/login/login_03.png" width="700" height="402"></td>
      <td align="left" valign="top" width="324" height="402" background="./ui/style/images/login/login_04.png" style="padding:0px;margin:0px;">
      <table width="286" border="0" cellpadding="2" cellspacing="0" style="margin-top: 30px;">
      	<tr>
          <td>&nbsp;</td>
          <td colspan="2"><div id="message" class="message"><%=errorMsg%></div></td>
        </tr>
        <tr>
          <td width="99" align="right"><font color="#ffffff">用户名：</font></td>
          <td colspan="2" align="left"><input id="txt_account" class='regtxt' title='请填写用户'名 maxLength='16' size='16' name="userName"></td>
        </tr>
        <tr>
          <td align="right"><font color="#ffffff">密码：</font></td>
          <td colspan="2" align="left"><input id = "txt_password" class=regtxt title='请填写密'码 type='password' maxLength='16'
size='16' name="password"></td>
        </tr>
         <tr >
          <td colspan="3" align="center" valign="middle"><input type=image src="./ui/style/images/login/login_btn.png" width="85" height="36" style="margin-left:60px;"><a href="javascript:location.reload();" style="text-decoration: none;"><img src="./ui/style/images/login/cancel_btn.png" width="85" height="36" border="0"></a></td>
        </tr>
      </table></td>
    </tr>
    <tr>
    	<td colspan="2">
    		<div align="center"  style="position:relative:20px;line-height:20px;"><span style="font-family:arial;">Copyright &copy;2013 sinopharmlog.com. All right Reserve</span></div>
    	</td>
    </tr>
   </tbody>
 </table>
</div>
</form></BODY></HTML>
