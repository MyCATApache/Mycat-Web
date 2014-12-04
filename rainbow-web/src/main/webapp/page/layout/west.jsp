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
								onContextMenu : collectPage,
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
		$('#west_tree_menu').menu({
			onClick:function(item){
				var datas = {'pageCode':$('#west_tree_menu').data('code')};
				$.post('${pageContext.request.contextPath}/dispatcherAction/query.do?service=favoritesService&method=insert',datas,function(data){
					if(data.success){
						$.messager.show({title:'提示', msg:"收藏成功!",timeout:3000});
					}else{
						$.messager.show({title:'提示', msg:data.msg,timeout:3000});
					}
				});
			}
		});
		
		$('#west_favorites_menu').menu({
			onClick:function(item){
				var type = $(item.target).attr('type');
				if(type=='remove'){
					var queryParams = {'rows':[{'pageCode':$('#west_favorites_menu').data('code')}]};
					$.post('./dispatcherAction/execute.do?service=favoritesService&method=delete',queryParams,function(data){
						if(data.success){
							$('#collection').tree('reload');
							$.messager.show({title:'提示', msg:"删除成功!",timeout:3000});
						}else{
							$.messager.show({title:'提示', msg:data.msg,timeout:3000});
						}
					});
				}else if(type=='clear'){
					$.post('./dispatcherAction/execute.do?service=favoritesService&method=clear',null,function(data){
						if(data.success){
							$('#collection').tree('reload');
							$.messager.show({title:'提示', msg:"清空成功!",timeout:3000});
						}else{
							$.messager.show({title:'提示', msg:data.msg,timeout:3000});
						}
						
					});
				}
			}
		});
		
		$('#collection').tree({
			url:'${pageContext.request.contextPath}/menusAction/favorites.do',
			method:'post',
			animate:false,
			onClick : showPage,
			onContextMenu:function(e, node){
				e.preventDefault();
				$('#west_favorites_menu').menu('show', {
					left : e.pageX,
					top : e.pageY
				}).data('code',node.id);
			}
		});
	});
	
	function showPage(node){
		if(node.attributes.isAuth!=undefined && node.attributes.isAuth==0){
			$.messager.confirm('提示','该权限已收回，建议删除该菜单！',function(b){
				if(b){
					var queryParams = {'rows':[{'pageCode':node.id}]};
					$.post('./dispatcherAction/execute.do?service=favoritesService&method=delete',queryParams,function(data){
						if(data.success){
							$('#collection').tree('reload');
							$.messager.show({title:'提示', msg:"删除成功!",timeout:3000});
						}else{
							$.messager.show({title:'提示', msg:data.msg,timeout:3000});
						}
					});
				}
			});
			return false;
		}
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
				$.post('./dispatcherAction/query.do?service=setupService&method=query&funCode=showMenu',null,function(data){
					var display = "";
					if(data.rows.length > 0){
						display = data.rows[0].isDisplay;
					}
					layout_center_addTabFun({
						id : node.id,
						title : node.text,
						closable : true,
						isDisplay:display,
						flag:flag,
						border : false,
						cache : isCache,
						href : url
					});
					flag=0;
				});
		}else{
			$('#sys_frame_bottom').html('');
		}
		
	}
	var flag = 0;
	var create_tabs = function(url, titles,isCache,orderflag) {
		var b = $('#layout_center_tabs').tabs('exists', titles);
		if (!b) {
			$('#layout_center_tabs').tabs('add', {
				title : titles,
				href : url,
				cache: isCache,
				closable : true
			});
			flag = orderflag;
		} else {
			 $('#layout_center_tabs').tabs('select', titles);
			 if(flag == 0){
				 layout_center_refreshTab(titles,url);
				 flag = orderflag;
			 }else{
				 var tabUrl =  $('#layout_center_tabs').tabs('getTab', titles).panel('options').href;
				 if(url != tabUrl){
					 layout_center_refreshTab(titles,url);
				 }
			 }
		}
	};
	var collectPage = function(e, node){
		e.preventDefault();
		var isLeaf = $('#'+node.id+'_west_tree').tree('isLeaf', node.target);
		if(isLeaf){
			$('#west_tree_menu').menu('show', {
				left : e.pageX,
				top : e.pageY
			}).data('code',node.id);
		}
	};
	var refresh_collection = function(title){
		if(title=="个人收藏夹"){
			$('#collection').tree('reload');
		}
	};
	
</script>
<div class="easyui-tabs" data-options="border:false,fit:true,onSelect:refresh_collection">
	<div title="功能菜单" data-options="border:false">
		<div id="menus" class="easyui-accordion" data-options="fit:true,border:false"/>
	</div>
	<div  title="个人收藏夹" iconCls="icon-favorites" data-options="border:false">
		<div id="collection" style="margin-top:10px;"></div>
	</div>
</div>
<div id="west_tree_menu" style="width: 120px; display: none;">
	<div iconCls="icon-favorites">添加到收藏夹</div>
</div>
<div id="west_favorites_menu" style="width: 120px; display: none;">
	<div iconCls="icon-remove" type="remove">移除</div>
	<div iconCls="icon-clearFavorites" type="clear">删除所有</div>
</div>
