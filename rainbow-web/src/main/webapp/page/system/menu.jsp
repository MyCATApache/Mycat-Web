<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
/* 	$(function() {
		$('#admin_jsgl_datagrid').datagrid({
			url : '${pageContext.request.contextPath}/roleController/datagrid.do',
			fit : true,
			fitColumns : true,
			border : false,
			pagination : true,
			idField : 'id',
			pageSize : 10,
			pageList : [ 10, 20, 30, 40, 50 ],
			sortName : 'text',
			sortOrder : 'asc',
			checkOnSelect : false,
			selectOnCheck : false,
			nowrap : false,
			frozenColumns : [ [ {
				title : '编号',
				field : 'id',
				width : 150,
				sortable : true,
				checkbox : true
			}, {
				title : '角色名称',
				field : 'text',
				width : 150,
				sortable : true
			} ] ],
			columns : [ [ {
				title : '可访问资源ID',
				field : 'resourceIds',
				width : 300,
				hidden : true
			}, {
				title : '可访问资源',
				field : 'resourceNames',
				width : 300,
				formatter : function(value, row, index) {
					if (row.id == '0') {
						return '可访问系统所有资源';
					} else {
						return value;
					}
				}
			}, {
				field : 'action',
				title : '动作',
				width : 100,
				formatter : function(value, row, index) {
					if (row.id == '0') {
						return '系统角色';
					} else {
						return formatString('<img onclick="admin_jsgl_editFun(\'{0}\');" src="{1}"/>&nbsp;<img onclick="admin_jsgl_deleteFun(\'{2}\');" src="{3}"/>', row.id, '${pageContext.request.contextPath}/style/images/extjs_icons/pencil.png', row.id, '${pageContext.request.contextPath}/style/images/extjs_icons/cancel.png');
					}
				}
			} ] ],
			toolbar : [ {
				text : '增加',
				iconCls : 'icon-add',
				handler : function() {
					admin_jsgl_appendFun();
				}
			}, '-', {
				text : '批量删除',
				iconCls : 'icon-remove',
				handler : function() {
					admin_jsgl_removeFun();
				}
			}, '-' ]
		});
	});

	function admin_jsgl_editFun(id) {
		$('#admin_jsgl_datagrid').datagrid('uncheckAll').datagrid('unselectAll').datagrid('clearSelections');
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/admin/jsglEdit.jsp',
			width : 500,
			height : 200,
			modal : true,
			title : '编辑角色',
			buttons : [ {
				text : '编辑',
				iconCls : 'icon-edit',
				handler : function() {
					var d = $(this).closest('.window-body');
					$('#admin_jsglEdit_editForm').form('submit', {
						url : '${pageContext.request.contextPath}/roleController/edit.action',
						success : function(result) {
							try {
								var r = $.parseJSON(result);
								if (r.success) {
									$('#admin_jsgl_datagrid').datagrid('updateRow', {
										index : $('#admin_jsgl_datagrid').datagrid('getRowIndex', id),
										row : r.obj
									});
									d.dialog('destroy');
								}
								$.messager.show({
									title : '提示',
									msg : r.msg
								});
							} catch (e) {
								$.messager.alert('提示', result);
							}
						}
					});
				}
			} ],
			onClose : function() {
				$(this).dialog('destroy');
			},
			onLoad : function() {
				var index = $('#admin_jsgl_datagrid').datagrid('getRowIndex', id);
				var rows = $('#admin_jsgl_datagrid').datagrid('getRows');
				var o = rows[index];
				o.resourceIds = stringToList(rows[index].resourceIds);
				$('#admin_jsglEdit_editForm').form('load', o);
			}
		});
	}
	function admin_jsgl_appendFun() {
		$('#admin_jsgl_datagrid').datagrid('uncheckAll').datagrid('unselectAll').datagrid('clearSelections');
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/admin/jsglAdd.jsp',
			width : 500,
			height : 200,
			modal : true,
			title : '添加角色',
			buttons : [ {
				text : '增加',
				iconCls : 'icon-add',
				handler : function() {
					var d = $(this).closest('.window-body');
					$('#admin_jsglAdd_addForm').form('submit', {
						url : '${pageContext.request.contextPath}/roleController/add.action',
						success : function(result) {
							try {
								var r = $.parseJSON(result);
								if (r.success) {
									$('#admin_jsgl_datagrid').datagrid('insertRow', {
										index : 0,
										row : r.obj
									});
									d.dialog('destroy');
								}
								$.messager.show({
									title : '提示',
									msg : r.msg
								});
							} catch (e) {
								$.messager.alert('提示', result);
							}
						}
					});
				}
			} ],
			onClose : function() {
				$(this).dialog('destroy');
			}
		});
	}
	function admin_jsgl_removeFun() {
		var rows = $('#admin_jsgl_datagrid').datagrid('getChecked');
		var ids = [];
		if (rows.length > 0) {
			$.messager.confirm('确认', '您是否要删除当前选中的项目？', function(r) {
				if (r) {
					for ( var i = 0; i < rows.length; i++) {
						ids.push(rows[i].id);
					}
					$.ajax({
						url : '${pageContext.request.contextPath}/roleController/remove.action',
						data : {
							ids : ids.join(',')
						},
						dataType : 'json',
						success : function(result) {
							if (result.success) {
								$('#admin_jsgl_datagrid').datagrid('load');
								$('#admin_jsgl_datagrid').datagrid('uncheckAll').datagrid('unselectAll').datagrid('clearSelections');
							}
							$.messager.show({
								title : '提示',
								msg : result.msg
							});
						}
					});
				}
			});
		} else {
			$.messager.show({
				title : '提示',
				msg : '请勾选要删除的记录！'
			});
		}
	}
	function admin_jsgl_deleteFun(id) {
		$('#admin_jsgl_datagrid').datagrid('uncheckAll').datagrid('unselectAll').datagrid('clearSelections');
		$('#admin_jsgl_datagrid').datagrid('checkRow', $('#admin_jsgl_datagrid').datagrid('getRowIndex', id));
		admin_jsgl_removeFun();
	} */
	
/* 	$(function() {
		var json = {"total":10,"rows":[{"sortIndex":0,"guid":"dbe62fce-8aee-46e4-b58d-78f8f05710a0","name":"系统菜单","code":"MENU","parentCode":"root","pageCode":"MENU_PAGE"}]};
		$('#menus_datagrid').datagrid('loadData',eval(json));
	}); */
	//url:'${pageContext.request.contextPath}/menusAction/allMenus.do',
</script>
<table id="menus_datagrid" title="TreeGrid" class="easyui-treegrid" style="height:300px"
		data-options="url:'${pageContext.request.contextPath}/menusAction/allMenus.do',idField:'code',treeField:'name',parentField:'parentCode',
				rownumbers:true,pagination:true,fitColumns:true,autoRowHeight:false">
	<thead>
			<tr>
				<th data-options="field:'guid',width:80,hidden:true">guid</th>
				<th data-options="field:'code',width:100,align:'center',sortable:true">菜单代码</th>
				<th data-options="field:'name',width:100,align:'center',sortable:true">菜单名称</th>
				<th data-options="field:'parentCode',width:100,sortable:true">上级代码</th>
				<th data-options="field:'pageCode',width:100,align:'center',sortable:true">页面编号</th>
				<th data-options="field:'sortIndex',width:100,align:'center',sortable:true">排序位置</th>
				<th data-options="field:'createUser',width:100,align:'center'">创建人</th>
				<th data-options="field:'createTime',width:100,align:'center',sortable:true">创建时间</th>
			</tr>
	</thead>
</table>