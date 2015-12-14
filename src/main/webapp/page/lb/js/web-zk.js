/**
 * @author coder_czp@126.com
 * @desc Mycat-LB
 * @date 2015/11/15
 */

var appNavTree;

$(function() {
	createTree();
	loadAllZone();
	bindClick();
});

function bindClick() {
	var link = $("a,.app");
	for (var i = 0; i < link.length; i++) {
		link[i].onclick = onAppClick;
	}
}

function onAppClick() {
	window.open('/main.html?to=' + this.href);
	return false;
}

function loadAllZone() {
	$.get("/lb/loadAllZone", function(data) {
		if (!data || data.err)
			return alert("Fail to load mycat zone");
		updateTree(data);
	});
}
function disableObj(id) {
	$("#" + id).attr({
		"disabled" : "disabled"
	});
}
function enableObj(id) {
	$("#" + id).removeAttr("disabled");
}

/**
 * 查找节点
 */
function queryZkChildWhenPressEnter(e) {
	if (e.which == 13) {
		e.preventDefault();
		// queryZkChild();
	}
}
/**
 * 更新树,未指定icon则用css/tree.png
 * 
 * @param data
 */
function updateTree(data) {
	if (!data.icon) {
		data.icon = "css/tree.png";
	}
	appNavTree.settings.core.data = data;
	appNavTree.refresh();
}
/**
 * 转化zk路径
 */
function convertToZkPath(path) {
	if (path.length > 2 && path[0] == '/' && path[1] == '/')
		path = path.substr(1, path.length);
	return path;
}
/**
 * 树节点事件
 * 
 * @param data
 */
function onTreeNodeSelect(e, data) {
	var e = window.event || e;
	var isRigthClick = (e.button == 2 || e.button == 3);
	if (isRigthClick)
		return false;
	var path = data.instance.get_path(data.node, '/');
	var zkpath = convertToZkPath(path);
	var data = data.node.original.obj;
	$('#ctx').text("Path:"+zkpath+" Content:"+JSON.stringify(data));
}

/**
 * 创建导航树
 */
function createTree() {
	var ctxmenu = {
		'items' : function(node) {
			return {
				"Detail" : {
					"label" : "Detail",
					"action" : function(obj) {
						var path = appNavTree.get_path(node, '/');
						alert(convertToZkPath(path));
					}
				},
				"Delete" : {
					"label" : "Delete",
					"action" : function(obj) {
						var path = appNavTree.get_path(node, '/');
						alert(convertToZkPath(path));
					}
				},
			};
		}
	};
	var core = {
		"data" : [ {
			"text" : "All Mycat Zone",
			"state" : {
				opened : true,
				disabled : false,
				selected : false
			}
		} ],
	};
	var types = {
		"default" : {
			// 'icon' : 'glyphicon glyphicon-leaf',
			'icon' : 'css/mycat_16px.png',
		}
	};
	var tree = $('#jstree');
	tree.jstree({
		'core' : core,
		'types' : types,
		'contextmenu' : ctxmenu,
		'plugins' : [ 'contextmenu', 'types' ]
	}).on('select_node.jstree', onTreeNodeSelect);

	appNavTree = tree.jstree(true);

};

