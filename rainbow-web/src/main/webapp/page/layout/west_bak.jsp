<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">	
	$(function() {
		$.post("${pageContext.request.contextPath}/menusAction/getMenus.do",null,function(data){
			if(data.result == undefined){
				var length = data.length;
				if(length == 0){
					return;
				}
				for(var i = 0; i < length ; i++){
						$('#menus').accordion('add',{
							title:data[i].text,
							iconCls:'icon-reload',
							content:$('<ul id="'+data[i].id+'_west_tree" data-options="animate:false"></ul>').tree({
								lines : true,
								onClick : showPage,
								url:"${pageContext.request.contextPath}/menusAction/getMenus.do?code=" + data[i].id
							})
						});
				}
				 $('#menus').accordion({onSelect:function(title,index){
					var tree_id = data[index].id;
					var treeObject = $('#'+tree_id+'_west_tree');
					var node = treeObject.tree('find',tree_id);
					if(node){
						treeObject.tree('expand',node.target);
					}
				}}); 
			}else{
				$.messager.alert('提示', "系统异常!");
			}
		},"json");
	});
	
	function showPage(node){
		var isCache = true;
		if(node.attributes.isCache=='0'){
			isCache = false;
		}
		var isLeaf = $('#'+node.id+'_west_tree').tree('isLeaf', node.target);
		if(isLeaf){
				if (node.attributes.url) {
					url = '${pageContext.request.contextPath}' + node.attributes.url+'?flag=0';
				} else {
					url = '${pageContext.request.contextPath}/page/error/dog.jsp';
				}
				$.get('./dispatcherAction/query.do?service=setupService&method=query&funCode=showMenu',null,function(data){
					var display = data.rows[0].isDisplay;
					layout_center_addTabFun({
						id : node.id,
						title : node.text,
						closable : true,
						isDisplay:display,
						border : false,
						cache : isCache,
						href : url
					});
				});
		}else{
			$('#sys_frame_bottom').html('');
		}
	}
	//
</script>
<div id="menus" class="easyui-accordion" data-options="fit:true,border:false"/>
