<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div align="center">
	<br/>
	<form id="oc_datahost_addForm" method="post">
		<input name="guid"  type="hidden" />
			<table class="tableForm" style="width: 90%">
				<tr>
					<th style="width:20%;">物理节点</th>
					<td><input name="name" style="width:90%;"/></td>
				</tr>
				<tr>
					<th style="width:20%;">均衡策略</th>
					<td><input name="balance" style="width:90%;"/></td>
				</tr>
				<tr>
					<th style="width:20%;">最大连接数</th>
					<td><input name="maxCon" style="width:90%;" value="500"/></td>
				</tr>
				<tr>
					<th style="width:20%;">最小连接数</th>
					<td><input name="minCon" style="width:90%;" value="100"/></td>
	
				</tr>
				<tr>
					<th style="width:20%;">数据库类型</th>
					<td><input name="dbtype" style="width:90%;"/></td>
				</tr>
				<tr>
					<th style="width:20%;">连接驱动</th>
					<td><input name="dbdriver" style="width:90%;"/></td>
				</tr>
				<tr>
					<th style="width:20%;">心跳检查</th>
					<td><input name="hearbeat" style="width:90%;" value="select user()"/></td>
				</tr>
			</table>
	</form>
</div>