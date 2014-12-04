<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="sys_user_addForm" method="post">
	<input name="guid"  type="hidden"  />
		<table class="tableForm" style="width: 90%">
			<tr>
				<th style="width:20%">工号</th>
				<td><input name="loginId" class="easyui-validatebox" data-options="required:true"  style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">姓名</th>
				<td><input name="name" data-options="required:true"  class="easyui-validatebox" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">年龄</th>
				<td><input name="age" class="easyui-numberbox" value="0" data-options="min:0,max:120" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">是否有效</th>
				<td>
				<select  class="easyui-combobox" name="aliveFlag"  style="width:90%;" data-options="panelHeight:'auto',required:true" >  
					    <option value="0">有效</option>  
					    <option value="1">无效</option>
				</select>  
				</td>
			</tr>
			<tr>
				<th style="width:20%">是否需要验证码</th>
				<td>
				<select  class="easyui-combobox" name="isVerify"  style="width:90%;" data-options="panelHeight:'auto',required:true" >  
					    <option value="0">否</option>  
					    <option value="1">是</option>
				</select>  
				</td>
			</tr>
			<tr>
				<th style="width:20%">性别</th>
				<td><select class="easyui-combobox" name="sex"  style="width:90%;" data-options="required:true,panelHeight:'auto'" >  
					    <option value="0">男</option>  
					    <option value="1">女</option>
				</select>  
				</td>
			</tr>
			<tr>
				<th style="width:20%">移动电话</th>
				<td><input name="mobilePhone" data-options="required:true" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">电子邮箱</th>
				<td><input name="eMail" data-options="required:true" style="width:90%;" class="easyui-validatebox" data-options="required:true,validType:'email'"/></td>
			</tr>
			<tr>
				<th style="width:20%">入职时间</th>
				<td><input name="inJoinTime" class="easyui-datebox" required="required" data-options="required:true"  /></td>
			</tr>
			<tr>
				<th style="width:20%">在职状态</th>
				<td><input class="easyui-combobox" 
					name="workStatus"
					data-options="
							url:'./dispatcherAction/comboxCode.do?code=USER_STATUS',
							valueField:'value',
							textField:'text',
							panelHeight:'auto',
							required:true
					"/>
					</td>
				</tr>
			<tr>
				<th style="width:20%">所在部门</th>
				<td ><input name="orgCode"  style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">地址</th>
				<td ><input name="address" style="width:90%;"/></td>
			</td>
			</tr>
		</table>
	</form>
</div>
