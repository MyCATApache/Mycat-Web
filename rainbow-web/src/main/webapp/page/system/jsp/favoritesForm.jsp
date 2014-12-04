<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="system_favorites_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width: 80%">
			<tr>
				<th >guid</th>
				<td><input name="guid" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >收藏人</th>
				<td><input name="loginId" style="width:200px;" /></td>
			</tr>
			<tr>
				<th >收藏的页面代码</th>
				<td><input name="pageCode" style="width:200px;" /></td>
			</tr>
		</table>
	</form>
</div>