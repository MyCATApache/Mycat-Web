<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.hx.rainbow.common.security.login.RainbowUser"%>
<%@page import="org.hx.rainbow.common.web.session.ThreadConstants" %>
<%@page import="org.hx.rainbow.common.core.SpringApplicationContext"%>
<%@page import="org.springframework.security.cas.ServiceProperties"%>
<%@page import="org.hx.rainbow.common.context.RainbowProperties"%>
<%@page import="org.jasig.cas.client.util.CommonUtils"%>
<%	
	String username = (String)request.getSession().getAttribute(ThreadConstants.RAINBOW_USERNAME);
	String ip = request.getRemoteAddr();
%>
<script type="text/javascript" charset="utf-8">
 	var sys_changepwd_callback = function(dialog){
		this.onSuccess=function(data){
			try {
				if (data.success) {
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
	var sys_changgepwd = function() {
		var buttons = [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
					if(isValid('sys_changepwd_addForm') ==  true){
						var d = $(this).closest('.window-body');
						var data =serializeObject($('#sys_changepwd_addForm'),true);
						var rainbow = new Rainbow();
						if(data.newPassword != data.rePassword){
							$.messager.show({
								title : '提示',
								msg : '新密码与确认密码一致!'
							});
							$.messager.progress('close');
						}else{
							rainbow.setAttr(data);
							rainbow.setService("userService");
							rainbow.setMethod("changepwd");
							rainbowAjax.excute(rainbow,new sys_changepwd_callback(d));
						}
					}
				}
		},{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				$(this).closest('.window-body').dialog('destroy');
			}
		}];
		rainbowDialog.editDialog('./page/system/jsp/changepwd.jsp',buttons,600,300,true,'修改密码','ws_globalvariable_addForm',null);
	};
 
	function logout(){
		$.messager.confirm('温馨提示!', '您确定你要退出当前系统?', function(r){
			if (r){
				window.location.href="${pageContext.request.contextPath}/logout.jsp";
			}
		});
	}
	$(function() {
		var initIcon = '${cookie.easyuiThemeName.value}';
		if(initIcon == null || initIcon == ''){
			$('#save').attr('iconCls','icon-ok');
		}else{
			$('#'+initIcon).attr('iconCls','icon-ok');
		}
		
	});
	
</script>
<div id="sessionInfoDiv" style="position: absolute;right: 5px;top:10px;">
	<font color="#34288c">[<strong><%=username%></strong>]，欢迎您使用MyCat管理系统!</font>
</div>
<div style="position: absolute; right: 0px; bottom: 0px; " >
	 <a href="javascript:void(0);" class="easyui-linkbutton" onclick="sys_changgepwd()" data-options="plain:true,iconCls:'icon-lock'">修改密码</a><a href="javascript:void(0);" class="easyui-menubutton"  data-options="menu:'#layout_north_pfMenu',iconCls:'icon-huanf'">更换皮肤</a>   <a href="javascript:void(0);" onclick="logout();" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-exit'">安全退出</a>
</div>
<div id="layout_north_pfMenu" style="width: 120px; display: none;">
	<div id="save" onclick="changeTheme('save');">save</div>
	<div id="metro-blue" onclick="changeTheme('metro-blue');">metro-blue</div>
	<div id="metro-gray" onclick="changeTheme('metro-gray');">metro-gray</div>
	<div id="metro-orange" onclick="changeTheme('metro-orange');">metro-orange</div>
	<div id="metro-red" onclick="changeTheme('metro-red');">metro-red</div>
	<div id="bootstrap" onclick="changeTheme('bootstrap');">bootstrap</div>
	<div id="black" onclick="changeTheme('black');">black</div>
	<!-- <div onclick="changeTheme('gray');">gray</div>
	<div onclick="changeTheme('cupertino');">cupertino</div>
	<div onclick="changeTheme('dark-hive');">dark-hive</div>
	<div onclick="changeTheme('pepper-grinder');">pepper-grinder</div>
	 -->
</div>
<div id="layout_north_kzmbMenu" style="width: 100px; display: none;">
	<div onclick="sys_changgepwd();"><font color="#34288c">修改密码</font></div>
</div>
<div id="layout_north_zxMenu" style="width: 100px; display: none;">
	<div onclick="logout()"><font color="#34288c">安全退出</font></div>
</div>