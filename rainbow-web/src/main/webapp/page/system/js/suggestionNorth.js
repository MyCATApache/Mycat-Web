	$(function() {
		$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUGGESTION',null, function(data) {
			var datas = data.rows;
			$('#north_type_combobox').combobox({
				panelHeight : 'auto',
				textField : 'text',
				valueField : 'value',
				value : 0,
				data : datas
			});
		});
		$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUG_LEVEL',null, function(data) {
			var datas = data.rows;
			$('#north_sugLevel_combobx').combobox({
				panelHeight : 'auto',
				textField : 'text',
				valueField : 'value',
				value : 0,
				data : datas
			});
		});
		$.get('./dispatcherAction/query.do?service=codeService&method=getCode&code=SYS_SUG_STATUS',null, function(data) {
			var datas = data.rows;
			$('#north_sugStatus_combobx').combobox({
				panelHeight : 'auto',
				textField : 'text',
				valueField : 'value',
				value : 0,
				data : datas
			});
			$('#north_sugStatus_combobx').combobox('disable');
		});
	});
	var left_suggestion_onClick = function(rowIndex, rowData){
		var status = rowData.sugStatus; 
		if(status == '2' || status == '3'){
			$('#north_suggestion_addForm input').attr('disabled','disabled');
			$('#north_suggestion_addForm textarea').attr('disabled','disabled');
		}else{
			$('#north_suggestion_addForm input').removeAttr('disabled','disabled');
			$('#north_sug_suggestion').removeAttr('disabled','disabled');
			$('#north_sugStatus_combobx').combobox('disable');
		}
		$('#north_suggestion_addForm').form('load',rowData);
	};
	var left_suggestion_caozuo = function(value, row, index) {
		return  formatString('<img title="删除" onclick="left_suggestion_delete(\'{0}\',{1});" src="{2}"/>&nbsp;',row.guid,row.sugStatus, './ui/style/images/extjs_icons/delete.png');
	};
	var left_suggestion_delete =function(guid,status){
		if(!status || status <= 1){
			$.messager.confirm('询问', '您确定要删除当前记录？', function(b) {
				if(b){
					$.messager.progress();
					var rainbow = new Rainbow();
					rainbow.addRows({"guid":guid});
					rainbow.setService("suggestionService");
					rainbow.setMethod("delete");
					rainbowAjax.excute(rainbow,new left_suggestion_callback(null,function(){
						$('#north_type_combobox').combobox('setValue',0);
						$('#left_suggestion_datagrid').datagrid('clearSelections');
						$('#left_suggestion_datagrid').datagrid('reload',{});}));
				}
			});
		}else{
			$.messager.show({
				title : '提示',
				msg : '删除失败，该反馈已受理'
			});
		}
	};
	//ajax回调处理
	var left_suggestion_callback = function(dialog,relod){
			this.onSuccess=function(data){
				try {
					if (data.success) {
						relod();
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