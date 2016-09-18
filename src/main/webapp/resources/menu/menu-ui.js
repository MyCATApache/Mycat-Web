(function(window, $) {
	/**
	 * 获得菜单 唯一id
	 */
	function getopenId(url) {
		url = url.replace(/\//g, "");
		url = url.replace(/\./g, "");
		return url;
	}

	function selectedMenu(_this) {
		$(".treeview li").removeClass("active");
		var $this = $(_this);
		$this.addClass("active");
	}

	var defulatOp = {
		icons : {
			"1" : iconClass = "fa-cloud",// Zone
			"2" : iconClass = "fa-cubes",// clust group
			"3" : iconClass = "fa-cogs",// clust node
			"4" : iconClass = "fa-codepen",// host group
			"5" : iconClass = "fa-circle",// host node
			"6" : iconClass = "fa-th",// project group
			"7" : iconClass = "fa-file",// project node
			"8" : iconClass = "fa-circle-o"// node
		},
		data : []
	};

	function setMenu($this, menus, option) {
		var menuhtm = $this;
		$.each(menus, function(n, menudata) {
			var sli = $("<li id=\"menu" + menudata.menuId
					+ "\" class=\"treeview\"></li>");
			var sa = $("<a href=\"#\"><i class=\"fa "
					+ option.icons[menudata.menuType] + "\"></i> <span>"
					+ menudata.menuName + "</span></a>");
			sa.click(function() {
				// $(".treeview.active").removeClass("active").children("ul").hide().removeClass("menu-open");
				// $(this).parent().addClass("active").children("ul").show().addClass("menu-open");

			});
			sli.append(sa);

			if (menudata.subMenus.length > 0) {
				sa.append("<i class=\"fa fa-angle-left pull-right\"></i>");
				setSecondLevelMenu(sli, menudata.subMenus, option);
			}
			menuhtm.append(sli);
		})
	}

	function setSecondLevelMenu(sli, menus, option) {

		var sul = $("<ul class=\"treeview-menu\" style=\"display: none;\"></ul>");
		sli.append(sul);

		$.each(menus, function(n, menudata) {
			var sli2 = $("<li class='" + getopenId(menudata.menuUrl)
					+ "'></li>");
			sli2.click(function() {
				selectedMenu(this)
			});
			sul.append(sli2);
			var sa2 = $("<a openUrl='" + menudata.menuUrl + "' ><i class=\"fa "
					+ option.icons[menudata.menuType] + "\"></i>"
					+ menudata.menuName + "</a>");
			sli2.append(sa2);
			sa2.click(function() {
				var $this = $(this);
				loadContext($this.attr("openUrl"));
			});
			if (menudata.subMenus.length > 0) {
				sa2.append("<i class=\"fa fa-angle-left pull-right\"></i>");
				setThirdLevelMenu(sli2, menudata.subMenus, option);
			}
		})
	}

	function setThirdLevelMenu(sli2, menus, option) {
		var sul3 = $("<ul class=\"treeview-menu\"></ul>");
		sli2.append(sul3);
		$.each(menus, function(n, menudata) {
			var sli3 = $("<li  class='" + getopenId(menudata.menuUrl)
					+ "'></li>");
			sli3.click(function() {
				selectedMenu(this);
			});
			sul3.append(sli3);
			var sa3 = $("<a openUrl='" + menudata.menuUrl + "' ><i class=\"fa "
					+ option.icons[menudata.menuType] + "\"></i>"
					+ menudata.menuName + "</a>")
			sli3.append(sa3);
			sa3.click(function() {
				var $this = $(this);
				loadContext($this.attr("openUrl"));
			});
		})
	}

	$.fn.extend({
		mtMenu : function(Op) {
			var option = {};
			$.extend(option, defulatOp, Op);
			var $this = $(this);
			setMenu($this, option.data, option);
		}
	});

})(undefined, jQuery);

(function(window, $) {
	window["request"] = {
		params : null
	};
	
	if(!window["mwtools"]){
		window["mwtools"] = {};
	}

	function getParams(url) {
		var urls = url.split("&");

		var params = {};
		for (var i = 0; i < urls.length; i++) {
			var urln = urls[i];
			var param = urln.split("=");
			params[param[0]] = decodeURI(param[1]);
		}
		return params;
	}

	$(window).on("hashchange", function() {
		chnageUrl();
	});
	
	function chnageUrl(){
		var url = window.location.hash;
		if (url && url.length > 1) {
			url = url.substring(1);
			if (url.indexOf("?") > -1) {
				var datas = url.substring(url.indexOf("?")+1);
				var params = getParams(datas);
				mwtools.openContext(url, null, params);
			} else {
				mwtools.openContext(url, null);
			}
		}
	}
	
	/**
	 * 获得菜单 唯一id
	 */
	function getopenId(url) {
		url = url.replace(/\//g, "");
		url = url.replace(/\./g, "");
		return url;
	}

	$.extend(window["mwtools"],{
		setSessionParam : function(name, data) {
			if (window.sessionStorage) {
				sessionStorage.setItem(name, data);
			} else {
				$.cookie(name, data);
			}
		},
		getSessionParam : function(name) {
			if (window.sessionStorage) {
				return sessionStorage.getItem(name);
			} else {
				return $.cookie(name);
			}
		},
		getParam : function(name) {
			if (window["request"].params) {
				return window["request"].params[name];
			} else {
				return null;
			}
		},
		loadContext : function(url, copy,params) {
			
			/**
			 * 兼容以前的代码
			 */
			if (copy) {
				mwtools.openContext(url, copy)
			} else {
				if(params){
					window["request"].params = null;
					var datas = [];
					for ( var key in params) {
						datas.push(key+"="+encodeURI(params[key]));
					}
					window.location.hash = "#" + url+"?"+datas.join("&");
				}else{
					window.location.hash = "#" + url;
				}
			}
		},
		openContext : function(url, copy, params) {
			
			if (params) {
				window["request"].params = params;
			}

			if (intervalId)
				clearInterval(intervalId);
			if (copy && mmgrid) {
				var rows = mmgrid.selectedRows();
				var length = rows.length;
				if (length > 0) {
					data = rows[0];
				} else {
					alert("请选择复制的条目!");
					return;
				}
			}

			window.zkPath = getParam(url, "zkpath");
			window.zkId = getParam(url, "zkid");
			$(".content-wrapper").load(url, function(response, status, xhr) {
				$(".content").resize(function() {
				});

			});
		},
		/**
		 * 携带展开菜单功能的方法
		 * 
		 * @param url
		 */
		openMenu : function(url) {
			$(".treeview li").removeClass("active");
			var url2 = url;
			if(url.indexOf("?") > -1){
				url2 = url2.substring(0,url2.indexOf("?"));
			}
			var $act = $(".treeview ." + getopenId(url2));
			if ($act.length > 0) {
				var $parent = $act.parents(".treeview");
				if (!$parent.hasClass("menu-open")) {
					$parent.find("span").click();
				}
				chnageUrl();
				$act.addClass("active");
			}
		}
	});
})(window, jQuery);
