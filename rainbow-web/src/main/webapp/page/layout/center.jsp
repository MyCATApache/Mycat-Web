<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
	$(function() {
		$('#layout_center_tabsMenu').menu(
				{
					onClick : function(item) {
						var curTabTitle = $(this).data('tabTitle');
						var type = $(item.target).attr('type');

						if (type === 'refresh') {
							layout_center_refreshTab(curTabTitle);
							return;
						}

						if (type === 'close') {
							var t = $('#layout_center_tabs').tabs('getTab',
									curTabTitle);
							if (t.panel('options').closable) {
								$('#layout_center_tabs').tabs('close',
										curTabTitle);
							}
							return;
						}

						var allTabs = $('#layout_center_tabs').tabs('tabs');
						var closeTabsTitle = [];

						$.each(allTabs, function() {
							var opt = $(this).panel('options');
							if (opt.closable && opt.title != curTabTitle
									&& type === 'closeOther') {
								closeTabsTitle.push(opt.title);
							} else if (opt.closable && type === 'closeAll') {
								closeTabsTitle.push(opt.title);
							}
						});

						for ( var i = 0; i < closeTabsTitle.length; i++) {
							$('#layout_center_tabs').tabs('close',
									closeTabsTitle[i]);
						}
					}
				});

		$('#layout_center_tabs').tabs({
			fit : true,
			border : false,
			onContextMenu : function(e, title) {
				e.preventDefault();
				$('#layout_center_tabsMenu').menu('show', {
					left : e.pageX,
					top : e.pageY
				}).data('tabTitle', title);
			},
			onSelect:function(title){
				var opts = $('#layout_center_tabs').tabs('getSelected').panel('options');
				var display = opts.isDisplay;
				var url = opts.href;
				if(display!='undefined' && display=='1'){
					$('#sys_frame_bottom').html(url);
				}else{
					$('#sys_frame_bottom').html('');
				}
			}
		});
	});
	
	var rainbow_center_refresh = function(url) {
		var href = $('#layout_center_tabs').tabs('getSelected')
				.panel('options').href;
		if (href) {/*说明tab是以href方式引入的目标页面*/
			var index = $('#layout_center_tabs').tabs('getTabIndex',
					$('#layout_center_tabs').tabs('getSelected'));
			$('#layout_center_tabs').tabs('getTab', index).panel('refresh');
		} else {/*说明tab是以content方式引入的目标页面*/
			var panel = $('#layout_center_tabs').tabs('getSelected').panel(
					'panel');
			var frame = panel.find('iframe');
			try {
				if (frame.length > 0) {
					for ( var i = 0; i < frame.length; i++) {
						frame[i].contentWindow.document.write('');
						frame[i].contentWindow.close();
						frame[i].src = frame[i].src;
					}
					if ($.browser.msie) {
						CollectGarbage();
					}
				}
			} catch (e) {
			}
		}
	};
	var rainbow_center_closewindow = function() {
		var index = $('#layout_center_tabs').tabs('getTabIndex',
				$('#layout_center_tabs').tabs('getSelected'));
		var tab = $('#layout_center_tabs').tabs('getTab', index);
		if (tab.panel('options').closable) {
			$('#layout_center_tabs').tabs('close', index);
		} else {
			$.messager.alert('提示',
					'[' + tab.panel('options').title + ']不可以被关闭', 'error');
		}
	};

	function layout_center_refreshTab(title,url) {
		$('#layout_center_tabs').tabs('getTab', title).panel('refresh',url);
	}

	function layout_center_addTabFun(opts) {
		var t = $('#layout_center_tabs');
		if (t.tabs('exists', opts.title)) {
			t.tabs('select', opts.title);
			if(!opts.cache || opts.flag == 1){
				layout_center_refreshTab(opts.title,opts.href);
			}
		} else {
			var tabs = t.tabs('tabs');
			if(tabs.length > 8){
				$.messager.alert('温馨提示','您打开的页面太多，请关掉部分页面!', 'warning');
				return;
				
			}
			t.tabs('add', opts);
		}
	}
	var sys_fullScreen = function(){
		var iconcls = $("#fullScreen_button").linkbutton('options').iconCls;
		if(iconcls == 'icon-fullScreen'){
			$("#rainbowIndex").layout("full");
			$("#fullScreen_button").linkbutton({   
			    iconCls: 'icon-resetScreen'  
			});
			$("#fullScreen_button").attr('title','取消全屏');
		}else{
			$("#rainbowIndex").layout("unFull");
			$("#fullScreen_button").linkbutton({   
			    iconCls: 'icon-fullScreen'  
			});
			$("#fullScreen_button").attr('title','全屏显示');
		}
	};
	
</script>
<!-- data-options="href:'./page/layout/portal.jsp'" -->
<div id="layout_center_tabs" style="overflow: hidden;"
	data-options="tools:'#ayout_center_tabs_tools'">
	<div title="首页" data-options="href:'./page/layout/portal.jsp'"></div>
</div>

<div id="ayout_center_tabs_tools"
	style="border-top: 0px; border-right: 0px; border-left: 0px;">
	<a href="#" class="easyui-linkbutton" plain="true" title="刷新"
		onclick="rainbow_center_refresh();" iconCls="icon-reload"></a> <a
		href="#" class="easyui-linkbutton" plain="true" title="关闭"
		onclick="rainbow_center_closewindow();" iconCls="icon-closewindow"></a>
		<a id="fullScreen_button" href="#" class="easyui-linkbutton" plain="true" title="全屏显示"
		onclick="sys_fullScreen();" iconCls="icon-fullScreen"></a>
</div>

<div id="layout_center_tabsMenu" style="width: 120px; display: none;">
	<div type="refresh">刷新</div>
	<div class="menu-sep"></div>
	<div type="close">关闭</div>
	<div type="closeOther">关闭其他</div>
	<div type="closeAll">关闭所有</div>
</div>