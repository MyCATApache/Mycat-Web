<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
	<div align="center">
	<br/>
	<form id="sys_whereex_addForm" method="post">
	<input name="guid"  type="hidden" />
		<table class="tableForm" style="width:90%;">
			<tr>
				<th style="width:20%">条件名称：</th>
				<td><input name="whereName" style="width:90%;" /></td>
			</tr>		
			<tr>
				<th style="width:20%">条件代码：</th>
				<td><input name="whereCode" style="width:90%;" /></td>
			</tr>		
			<tr>
				<th style="width:20%">字段代码：</th>
				<td><input name="code" style="width:90%;" /></td>
			</tr>
			<tr>
				<th style="width:20%">字段名称：</th>
				<td><input name="describe" style="width:90%;" /></td>
			</tr>
			
			<tr>
				<th style="width:20%">值类型：</th>
				<td>
				<input class="easyui-combobox" style="width:408px;"
					name="valueType" value="text"
					data-options="url:'./dispatcherAction/comboxCode.do?code=VALUETYPE',
								valueField:'value',
								textField:'text',
								panelHeight:'auto',
								multiple:false"/>
				</td>		
			</tr>
			<tr>
				<th style="width: 20%;">值代码：</th>
				<td><input class="easyui-combogrid" style="width:408px;"
							name="valueCode"
							data-options="
						            panelWidth:408,
						            panelHeight:340,   
						            idField:'id',   
						            fitColumns:true,
						            textField:'name',   
						            pagination:true,
						            mode:'remote',
							        url:'./dispatcherAction/query.do?service=codeService&method=queryCombox',   
						            columns:[[   
						                {field:'id',title:'代码',width:80},
						                {field:'name',title:'名称',width:200}
						            ]] 
							"/>
				</td>
			</tr>
				
			<tr>
				<th style="width:20%">业务下拉框服务：</th>
				<td><input name="valueService" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">业务下拉框方法：</th>
				<td><input name="valueMethod" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">数据表格文本：</th>
				<td><input name="bizComboxText" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">数据表格值域：</th>
				<td><input name="bizComboxId" style="width:90%;"/></td>
			</tr>
			<tr>
				<th style="width:20%">业务下拉框字段：</th>
				<td><textarea name="bizColumns" style="height:100px;width:90%;"></textarea></td>
			</tr>
			<tr>
				<th style="width:20%">备注：</th>
				<td><input name="remark" style="width:90%;" /></td>
			</tr>
		</table>
	</form>
</div>