//扩展控件
var easyui_combobox_validate = null;
$.extend($.fn.datagrid.defaults.editors, {   
    combobox:{
    	init:function(container,options){
    		var input=$('<input type="text"/>').appendTo(container);
    		input.combobox(options||{});
    		var isShow = false;
    		var isInput = false;
    		input.combobox('textbox').focus(function (){
    			input.combobox("options").keyHandler.enter = function(){
    				
					if(isShow || isInput){
						input.combobox('hidePanel');
						isShow = false;
						isInput = false;
					}else{
						input.combobox('showPanel');
						isShow = true;
					}
    			};
    		});
    		
    		input.combobox('textbox').keypress(function(e){//KeyPress主要用来捕获数字(注意：包括Shift+数字的符号)、字母（注意：包括大小写）、小键盘等除了F1-12、SHIFT、Alt、Ctrl、Insert、Home、PgUp、Delete、End、PgDn、ScrollLock、Pause、NumLock、{菜单键}、{开始键}和方向键外的ANSI字符
    			isInput = true;
    		});
    		
    		input.combobox('textbox').blur(function (){
    			if (easyui_combobox_validate == null || easyui_combobox_validate.input === input){
    				var validate = false;
    				var id = input.combobox('getValue');
    				var text = input.combobox('getText');
    				var data = input.combobox('getData');
    				for(var i=0;i<data.length;i++){
    					if (data[i].id == id && data[i].text == text){
    						validate = true;
    						break;
    					}
    				}
    				if (!validate){//值不能通过
    					easyui_combobox_validate = {'input':input,'flag':false};
    					input.combobox('textbox').focus().select();
    					return false;
    				}else{
    					easyui_combobox_validate = null;
    				}
    			}
    		});
    		
    		input.combobox({//输入提示不区别大小写
	    			filter:function(q, row){
		        			var opts = $(this).combobox('options');
		        			return row[opts.textField].toUpperCase().indexOf(q.toUpperCase()) == 0;
	    			}
    		});
    		return input;
    	},
    	destroy:function(target){
    		$(target).combobox("destroy");
    	},
    	getValue:function(target){
    		return $(target).combobox("getValue");
    	},
    	setValue:function(target,value){
    		$(target).combobox("setValue",value);
    	},
    	resize:function(target,width){
    		$(target).combobox("resize",width);
    	}
    },
    combotree:{
    	init:function(container,options){
	    	var input=$("<input type=\"text\">").appendTo(container);
	    	input.combotree(options);
    		var isShow = false;
    		input.combotree('textbox').focus(function (){
    			input.combotree("options").keyHandler.enter = function(){
					if(!isShow){
						input.combotree('showPanel');
						isShow = true;
					}else{
						input.combotree('hidePanel');
						isShow = false;
					}
    			};
    			input.combotree("options").keyHandler.up = function(){
					if(isShow){
						var tree = input.combotree('tree');
						var node = tree.tree('getSelected');
						var rootNode = tree.tree('getRoot');
						if(node){
							if(rootNode != node.id){
								var id = node.id - 1;
								tree.tree('select',tree.tree('find',id).target);
							}
						}else{
							tree.tree('select',rootNode.target);
						}
					}
    			};
    			input.combotree("options").keyHandler.down = function(){
					if(isShow){
						var tree = input.combotree('tree');
						var node = tree.tree('getSelected');
						var rootNode = tree.tree('getRoot');
						if(node){
							var id = node.id + 1;
							tree.tree('select',tree.tree('find',id).target);
						}else{
							tree.tree('select',rootNode.target);
						}
					}
    			};
    		});
    		jQuery.hotkeys.add('shift+q',function(){
    			
    			if(isShow){
    				var tree = input.combotree('tree');
    				var node = tree.tree('getSelected');
    				if(node){
    					if(tree.tree('isLeaf',node)){
	    					if(node.state == 'open'){
	    						tree.tree("collapse",node.target);
	    					}else{
	    						tree.tree("expand",node.target);
	    					}
    					}
    				}
    			}
			});
    		input.combotree({onHidePanel:function(){
    			var tree = input.combotree('tree');
    			var node = tree.tree('getSelected');
    			if(node){
    				input.combotree('setValue',node.id);
    			}
    		}});
    		
    		input.combotree('textbox').blur(function (){input.combotree("options").keyHandler.enter=null;
    		input.combotree("options").keyHandler.up=null;
    		input.combotree("options").keyHandler.down=null;});
	    	return input;
    	},
    	destroy:function(target){
    		$(target).combotree("destroy");
    	},
    	getValue:function(target){
    		return $(target).combotree("textbox").val();
    	},
    	setValue:function(target,value){
    		$(target).combotree("setValue",value);
    	},
    	resize:function(target,width){
    		$(target).combotree("resize",width);
    	}
    },
    textDiv:{
    	init: function(container, options){ 
    		var input = $('<input type="text" class="datagrid-editable-input">').appendTo(container);
    		$(input).focus(function(){
    		});
            return input;
        },   
        getValue: function(target){   
            return $(target).val();   
        },   
        setValue: function(target, value){ 
            $(target).val(value);   
        },   
        resize: function(target, width){   
            var input = $(target);   
            if ($.boxModel == true){   
                input.width(width - (input.outerWidth() - input.width()));   
            } else {   
                input.width(width);   
            }
        }   
    },
    combogrid:{
    	init:function(container,options){
    		var input=$('<input type="text"/>').appendTo(container);
    		input.combogrid(options);
    		input.combogrid('grid').datagrid('loadData',options.data);
    		input.combogrid('textbox').focus(function (){
    		input.combogrid("options").keyHandler.enter = function(){
					if($(input.combogrid('panel')).is(":visible")){
						var row = input.combogrid('grid').datagrid('getSelected');
						if(row){
							input.combogrid('textbox').val(row[options.textField]);
						}
						input.combogrid('hidePanel');
					}else{
						input.combogrid('showPanel');
					}
    			};
    		});

    		input.combogrid('textbox').keyup(function(event){
    				if($.inArray(event.keyCode, keys) == -1){
    					var val = input.combogrid('textbox').val();
    					var rows2 = options.data.rows;
    					var data = {"total":0, "rows":[]};
    					if(rows2){
    						for(var i = 0; i < rows2.length; i++){
    							var flag = false;
    							for(var j = 0; j < options.keyField.length; j++){
    								if(rows2[i][options.keyField[j]].toUpperCase().indexOf(val.toUpperCase()) == 0){
    									flag = true;
    									break;
    								}
    							}
    							if(flag){
    								data.total++;
    								data.rows.push(rows2[i]);
    							 }
    						}
    					}
    					input.combogrid('grid').datagrid('loadData',data);
    					input.combogrid('textbox').val(val);
    				}else{
    					var val = input.combogrid('textbox').val();
    					if(val == ""){
    						input.combogrid('grid').datagrid('loadData',options.data);
    						var val = input.combogrid('textbox').val("");
    					}
    					
    				}
			});
    		
//    		input.combogrid('textbox').keyup(function(event){
//				var val = input.combogrid('textbox').val();
//				if(val == ""){
//					input.combogrid('grid').datagrid('loadData',options.data);
//				}
//    		});
    		input.combogrid({
				filter: function(q, row){
					var opts = $(this).combogrid('options');
					var pass =  row[opts.textField].toUpperCase().indexOf(q.toUpperCase()) == 0 
								|| row[opts.idField].toUpperCase().indexOf(q.toUpperCase()) == 0;
					return pass;
				}
			});

    		return input;
    	},
    	destroy:function(target){
    		$(target).combogrid("destroy");
    	},
    	getValue:function(target){
    		return $(target).combogrid("getValue");
    	},
    	setValue:function(target,value){
    		$(target).combogrid("setValue",value);
    	},
    	resize:function(target,width){
    		$(target).combogrid("resize",width);
    	}
    },
    numberbox:{
    		init:function(q, row){
    			var input=$("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(q);
    			input.numberbox(row);
    			input.focus(function(){
    				input.select();
    			});
    			return input;
    		},
    		destroy:function(target){
    			$(target).numberbox("destroy");
    		},
    		getValue:function(target){
    			$(target).blur();
    			var value = $(target).numberbox("getValue");
    			return value;
    		},
    		setValue:function(target,value){
    			$(target).numberbox("setValue",value);
    		},
    		resize:function(target,width){
    			$(target)._outerWidth(width);
    		}
    	}
    
});