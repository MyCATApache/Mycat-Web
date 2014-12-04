var build_init = function(input) {
		if ($(input).val() == "请填写模块代码" || $(input).val() == "") {
			$(input).val('');
			$(input).css('color', '#000000');
		}
	};
	
	var build_getColnum = function(rowIndex, rowData) {
		var dblinkName = $('#build_dblink').combogrid('getValue');
		var o = {"tableName":rowData.tableName,"dblinkName":dblinkName};
		$('#build_tableName').val(rowData.tableName);
		$('#build_datagrid').datagrid('clearSelections');
		$('#build_datagrid').datagrid('load', o);

	};

	var build_add = function() {
		var rows = $('#build_datagrid').datagrid('getRows');
		if (rows != null && rows != "") {
			var o = new Object();
			o['columnName'] = 'columnName';
			o['dataType'] = 'dataType';
			o['columnComment'] = 'columnComment';
			o['columnKey'] = '';
			$('#build_datagrid').datagrid('appendRow',
					$.parseJSON(JSON.stringify(o)));
			var changeRows = $('#build_datagrid').datagrid('getChanges');
			for ( var i = 0; i < changeRows.length; i++) {
				var index = $('#build_datagrid').datagrid('getRowIndex',
						changeRows[i]);
				$('#build_datagrid').datagrid('beginEdit', index);
			}
		}
	};

	var build_commitEditor = function() {

		var changeRows = $('#build_datagrid').datagrid('getChanges');
		if (changeRows != null && changeRows != "") {
			for ( var i = 0; i < changeRows.length; i++) {
				var index = $('#build_datagrid').datagrid('getRowIndex',
						changeRows[i]);
				$('#build_datagrid').datagrid('endEdit', index);
			}
			$('#build_datagrid').datagrid('clearSelections');
		} else {
			var selectedRows = $('#build_datagrid').datagrid('getSelections');
			for ( var i = 0; i < selectedRows.length; i++) {
				var index = $('#build_datagrid').datagrid('getRowIndex',
						selectedRows[i]);
				$('#build_datagrid').datagrid('endEdit', index);
			}
			$('#build_datagrid').datagrid('clearSelections');
		}
	};

	var build_editRow = function(rowIndex, rowData) {
		$('#build_datagrid').datagrid('selectRow', rowIndex);
		$('#build_datagrid').datagrid('beginEdit', rowIndex);
	};
	
	var build_caozuo = function(value, row, index) {
		return formatString(
				'<img title="删除" onclick="build_delete(\'{0}\');" src="{1}"/>&nbsp;',
				index, './ui/style/images/extjs_icons/delete.png');
	};
	
	var build_delete = function(index) {
		$('#build_datagrid').datagrid('deleteRow', index);
	};
	
	var build_buildJsp = function(){
		build_builder('./builderAction/jsp.do');
	};
	var build_buildJspForm = function(){
		build_builder('./builderAction/jspForm.do');
	};
	var build_buildJs = function(){
		build_builder('./builderAction/js.do');
	};
	var build_buildService = function(){
		build_builder('./builderAction/service.do');
	};
	var build_buildMapper = function(){
		build_builder('./builderAction/mapper.do');
	};

	var build_insertIntoWhereEx = function(){
		var rows = $('#build_datagrid').datagrid('getRows');
		var whereName = $('#build_whereName').val();
		if(!whereName){
			$.messager.show({
				title : '提示',
				msg : '请填写查询控件名称!'
			});
			return;
		}
		if (rows.length > 0) {
			$.messager.confirm('询问', '您确定要添加查询控件条件吗？', function(b) {
				if(b){
					$.messager.progress();
					var tableName = $('#build_tableName').val();
					var data = JSON.stringify(rows);
					rainbowAjax.post('./builderAction/whereex.do',{'colunms':data,'tableName':tableName,'modelName':whereName},function(data){
						$.messager.show({
							title : '提示',
							msg : '新增成功!'
						});
						$.messager.progress('close');
						});
					}
				});
		}else {
			$.messager.alert("Info", "请选择一张表", "info", function() {
			});
		}
	};
	
	var build_builder = function(url) {
		var modelName = $('#build_modelName').val();
		if (modelName == null || modelName == "" || modelName == "请填写模块代码") {
			$.messager.alert("Info", "请填写模块代码", "info", function() {
				if ($('#build_modelName').val() == "") {
					$('#build_modelName').val('请填写模块代码');
				}
				$('#build_modelName').css('color', 'red');

			});
			return false;
		}
		var rows = $('#build_datagrid').datagrid('getRows');
		if (rows != null && rows != "") {
			var tableName = $('#build_tableName').val();
			var data = JSON.stringify(rows);
			var form = $("<form>");
			form.attr('style','display:none');
		    form.attr('target','');
			form.attr('method','post');
			form.attr('action',url);
			var rowsParam = $('<input>');  
			rowsParam.attr('type','hidden');  
			rowsParam.attr('name','colunms');  
			rowsParam.attr('value',data);
			var tableNameParam = $('<input>');  
			tableNameParam.attr('type','hidden');  
			tableNameParam.attr('name','tableName');  
			tableNameParam.attr('value',tableName);
			var modelNameParam = $('<input>');  
			modelNameParam.attr('type','hidden');  
			modelNameParam.attr('name','modelName');  
			modelNameParam.attr('value',modelName);
			$('body').append(form);
			form.append(rowsParam);
			form.append(tableNameParam);
			form.append(modelNameParam);
			form.submit();
			form.remove();
		} else {
			$.messager.alert("Info", "请选择一张表", "info", function() {
			});
		}
	};

	var build_updateRow = function(rowIndex, rowData) {
		$('#build_datagrid').datagrid('endEdit', rowIndex);
	};
	
	var build_changeBuilderMode = function(record){
		var value = record.value;
		if(value==2){
			$('#build_changeModel').combogrid('clear');
			$('#build_datagrid').datagrid('loadData', { total: 0, rows: [] });
			$('#build_changeModel').combogrid({url:'./dispatcherAction/query.do?service=builderService&method=queryViewCombox'});
		}else{
			$('#build_changeModel').combogrid('clear');
			$('#build_datagrid').datagrid('loadData', { total: 0, rows: [] });
			$('#build_changeModel').combogrid({url:'./dispatcherAction/query.do?service=builderService&method=queryCombox'});
		}
	};
	var queryParams = {};
	var build_loadTableName = function(rowIndex, rowData){
		var grid = $('#build_changeModel').combogrid('grid');
		queryParams = {"dblinkName":rowData.dbLink};
		grid.datagrid('load',queryParams);
		$('#build_datagrid').datagrid('loadData',[]);
	};
	var build_onChange = function(newValue, oldValue){
		var options = $('#build_changeModel').combogrid('options');
		options.queryParams = queryParams;
	};