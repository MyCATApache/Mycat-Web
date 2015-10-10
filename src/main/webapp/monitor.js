var app = "";
var msgUrl = "/jmsg";
var gcUrl = "/jm/doGC";
var hdUrl = "/jm/doHeapDump";
var thUrl = "/jm/dumpThead";
var sendUrl = "/jm/sendMessage"
var clsUrl = "/jm/loadCluster";
var dlUrl = "/jm/deadlockCheck";
var mtUrl = "/jm/loadMonitorData";
var rtUrl = "/jm/loadRuntimeInfo";
var ldThUrl = "/jm/loadThreadInfo";

var THREAD_STATE = {
	'BLOCKED' : "阻塞",
	'RUNNABLE' : "运行",
	'WAITING' : "等待",
	'TIMED_WAITING' : "限时等待 "
};
var GC_TOTAL = "总次数";
var GC_TIMES = "总耗时";
var MONITOR = "正在监控";
var FREEMEMORY = "空闲内存:";
var REFRESH_PERIOD = "刷新频率";
var NO_DEAD_LOACK = "没有检测到死锁";
var DUMP_SUCCESS = "Dump成功,文件已保存到:";
var GC_WAARING = "执行GC会影响目标应用,是否继续?";
var NO_APP_SELECT_WARNING = "请先选择javaApp";
var DUMP_WAARING = "执行Dump会影响目标应用,是否继续?";

var lastgc;
var gcChart;
var cpuChart;
var edenChart;
var heapChart;
var threadChart;
var lineMax = 20;
var globtime = 3000;
var mbSize = 1024 * 1024;
var nodeSelectedFunction;

String.format = function() {
	if (arguments.length == 0)
		return null;
	var tmp = arguments;
	var format = tmp[0];
	var size = tmp.length;
	for (var i = 1; i < size; i++) {
		format = format.replace("%s", tmp[i]);
	}
	return format;
};

function compare(a, b) {
	return a == b ? 0 : a > b ? 1 : -1;
}
function buildColumn(size, data) {
	return '<div class="autoline col-lg-' + size + '">' + data + '</div>';
}
function buildToast(message) {
	return '<span class="badge">' + message + '</span>';
}
function buildTips(message) {
	return '<label class="label label-success">' + message + '</label>';
}
function updateSeries(series, time, data, field) {
	try {
		var shift = series[0].data.length > lineMax;
		for (i in series) {
			var ser = series[i];
			var item = data[ser.name];
			var xy = [ time, field ? item[field] : item ];
			ser.addPoint(xy, true, shift);
		}
	} catch (e) {
		console.log(data, field);
	}
}

function createChart(oldChart, names, renderId, chart_type) {
	if (oldChart)
		return oldChart;
	var labels = {};
	var sers = new Array();
	var isArr = names instanceof Array;
	for (i in names) {
		sers.push({
			'name' : isArr ? names[i] : i,
			data : []
		});
	}
	if (chart_type == 'area') {
		labels['formatter'] = function() {
			return parseInt(this.value / mbSize) + 'MB';
		}
	}
	return new Highcharts.Chart({
		chart : {
			renderTo : renderId,
			type : chart_type
		},
		credits : {
			enabled : false
		},
		title : {
			text : null
		},
		xAxis : {
			tickPixelInterval : 150,
			type : 'datetime',
			gridLineWidth : 1,
			rotation : 0
		},
		yAxis : {
			min : 0,
			gridLineWidth : 1,
			title : {
				text : null
			}
		},
		exporting : {
			enabled : false
		},
		series : sers
	});
}

function changeGlobTime(time) {
	globtime = time;
}
function buildMenuItem(click, txt) {
	return String.format('<li><a href="#" onclick="%s">%s<a></li>', click, txt);
}
function buildDropmenu(renderId) {
	var menu = '<div class="btn-group">';
	menu += '<button type="button" class="btn btn-danger btn-sm dropdown-toggle"';
	menu += 'data-toggle="dropdown" aria-expanded="false"><div class="btn-group">';
	menu += REFRESH_PERIOD + '<span class="caret"></span>'
	menu += '</button><ul class="dropdown-menu" role="menu">';
	menu += buildMenuItem('changeGlobTime(3000)', '3s');
	menu += buildMenuItem('changeGlobTime(1000)', '1s');
	menu += buildMenuItem('changeGlobTime(3000)', '10s');
	menu += buildMenuItem('changeGlobTime(30000)', '30s');
	menu += '</ul></div>';
	$('#' + renderId).html(menu);
}
function updateURL(node) {
	var tips = $('#app');
	if (tips) {
		tips.text(String.format('%s:(%s-[%s:%s])', MONITOR, node.text,
				node.host, node.port));
	}
}
function checkHasSelectApp() {
	if (app == '') {
		alert(NO_APP_SELECT_WARNING);
		return false;
	}
	return true;
}
function handleAjaxErr(jq, status, err) {
	Timer.stop();
	if (jq.responseText) {
		alert(jq.responseText);
	} else if (err) {
		alert(err);
	} else if (status) {
		alert(status);
	} else {
		alert('request server error');
	}
}
function beforeAjaxSend(jq) {
	if (clsUrl == this.url)
		return true;
	if (!checkHasSelectApp())
		return false;
	if (this.data) {
		this.data += '&app=' + app;
	} else if (this.url.indexOf('?') > 0) {
		this.url += '&app=' + app;
	} else {
		this.url += '?app=' + app;
	}

}

function updateAppTree(event) {
	if (!event || 'appupdate' == event.data) {
		$.get(clsUrl, function(result) {
			$('#tree').treeview({
				data : result
			}).on('nodeSelected', nodeSelectedFunction);
		});
	}
}
Timer = {
	ids : [],
	add : function() {
		if (arguments.length < 2) {
			console.log('arguments length <2');
			return;
		}
		var args = Array.prototype.slice.call(arguments);
		this.ids.push(setTimeout(args.shift(), args.shift(), args));
	},
	stop : function() {
		var id;
		var ids = this.ids;
		while (id = ids.shift())
			clearTimeout(id);
	},
};
LongPolling = {
	state : 'init',
	listener : [],
	start : function() {
		if (LongPolling.state == 'start' || app == '')
			return;
		$.ajax({
			url : msgUrl,
			data : {
				'type' : 'recv'
			},
			error : function(jq) {
				LongPolling.state = 'init';
				console.log(jq);
			},
			success : function(json) {
				LongPolling.state = 'init';
				LongPolling.dispactch(json);
				LongPolling.start();
			},
			error : function(jq, status, err) {
				if (status == "timeout") {
					LongPolling.start();
					LongPolling.state = 'init';
				}
			},
		});
		LongPolling.state == 'start';
	},
	dispactch : function(event) {
		try {
			var listener = this.listener;
			for (i in listener) {
				listener[i](event);
			}
		} catch (e) {
			console.log(e);
		}
	},
	addListener : function(listener) {
		this.listener.push(listener);
	},
	removeListener : function(listener) {
		this.listener.remove(listener);
	},
	send : function(msg, id, callback) {
		LongPolling.start();
		$.post(msgUrl, {
			'type' : 'send',
			'data' : msg,
			'id' : id
		}, callback);
	},

};
MonitorServer = {
	loadData : function(cluster, app) {
		$.ajax({
			url : mtUrl,
			success : function(data) {
				if (!$('#main').is(":hidden")) {
					var time = data.time;
					var momory = data.memory;
					MonitorServer.updateGCLine(time, data.gc);
					MonitorServer.updateCpuLine(time, data.cpu);
					MonitorServer.updateHeapArea(time, momory.heap);
					MonitorServer.updateNonHeapArea(time, momory.nonheap);
				}
				Timer.add(MonitorServer.loadData, globtime, cluster, app);
			}
		});
	},
	updateGCLine : function(time, gc) {
		gcChart = createChart(gcChart, gc, 'gc', 'spline');
		var times = 0;
		var count = 0;
		var series = gcChart.series
		var shift = series[0].data.length > lineMax;
		for (i in series) {
			var ser = series[i];
			var key = ser.name;
			var item = gc[key];
			if (!lastgc)
				ser.addPoint([ time, item ], true, shift);
			else
				ser.addPoint([ time, item - lastgc[key] ], true, shift);

			if (key.indexOf('count') > 0)
				count += item;
			else if (key.indexOf('time') > 0)
				times += item;
		}
		lastgc = gc;
		$('#gcTitle').html(
				String.format('GC[%s:%s %s:%sms]', GC_TOTAL, count, GC_TIMES,
						Math.round(times * 1000)));
	},
	updateCpuLine : function(time, cpu) {
		cpuChart = createChart(cpuChart, [ 'OS', 'JVM' ], 'cpu', 'spline');
		var cpuser = cpuChart.series
		var shift = cpuser[0].data.length > lineMax;
		var oscpu = [ time, cpu.os ];
		var vmcpu = [ time, cpu.vm ];
		cpuser[0].addPoint(oscpu, true, shift);
		cpuser[1].addPoint(vmcpu, true, shift);

		var momory = parseInt(cpu.freememory / mbSize);
		var title = String.format('OS:%s% JVM:%s% Core:%s %s:%sMB', cpu.os,
				cpu.vm, cpu.cores, FREEMEMORY, momory);
		$('#cpuTitle').html(title);

	},
	updateHeapArea : function(time, heap) {
		var childs = heap.childs;
		heapChart = createChart(heapChart, childs, 'heap', 'area');
		updateSeries(heapChart.series, time, childs, 'used');
		$('#heapTitle').text(
				this.buildTitle('Heap', heap.max, heap.used, heap.init));
	},
	updateNonHeapArea : function(time, nonheap) {
		var childs = nonheap.childs;
		edenChart = createChart(edenChart, childs, 'eden', 'area');
		updateSeries(edenChart.series, time, childs, 'used');
		$('#edenTitle').text(
				this.buildTitle('Nonheap', nonheap.max, nonheap.used,
						nonheap.init));
	},
	buildTitle : function(name, max, used, init) {
		var max = parseInt(max / mbSize);
		var use = parseInt(used / mbSize);
		var init = parseInt(init / mbSize);
		return String.format('%s-Max:%sMB Init:%sMB,Used:%sMB', name, max,
				init, use);
	},
	doGC : function(obj) {
		if (confirm(GC_WAARING)) {
			$.get(gcUrl, function(result) {
				alert(result);
			});
		}
	},
	dumpHeap : function(obj) {
		if (confirm(DUMP_WAARING)) {
			$.get(hdUrl, function(res) {
				if (res.local == true) {
					window.open(res.file);
				} else {
					alert(DUMP_SUCCESS + res.file);
				}
			});
		}
	}
};
RuntimeServer = {
	loadData : function(app) {
		$.get(rtUrl, function(data) {
			var html = '';
			for (i in data) {
				html += '<div class="row mycol">';
				html += buildColumn(4, i);
				html += buildColumn(8, data[i]);
				html += "</div>";
			}
			$('#vminfoTbl').html(html);
			$('#title').text('StartTime:[' + data.starttime + ']');
		});
	}
};
ThreadServer = {
	sort : function(thead1, thead2) {
		return compare(thead2.cpu, thead1.cpu);
	},
	title : function(thread) {
		var tmp = thread.total / 100;
		var state = thread.state;
		var html = '';
		var value;
		html += buildToast(String.format('Total:%s', thread.total));
		for (key in state) {
			value = state[key];
			html += buildToast(String.format('%s:%s(%s%)', THREAD_STATE[key],
					value, parseInt(value / tmp)));
		}
		return html;

	},
	updateCount : function(data) {
		var items = data.state;
		threadChart = createChart(threadChart, items, 'threadCount', 'spline');
		updateSeries(threadChart.series, data.time, items);
	},
	loadData : function(app) {
		$.get(ldThUrl, function(data) {
			ThreadServer.updateCount(data);
			data.detail.sort(ThreadServer.sort);
			var threads = data.detail;
			var html = '';
			var thInfo;
			for (i in threads) {
				thInfo = threads[i];
				html += '<div class="row  mycol">';
				html += buildColumn(6, thInfo.name);
				html += buildColumn(2, thInfo.state);
				html += buildColumn(2, thInfo.cpu);
				html += buildColumn(2,
						'<button onclick="ThreadServer.dumpThead(this,'
								+ thInfo.id + ')">Dump</button>');
				html += '</div>';
			}
			$('#tableBody').html(html);
			$('#thinfoTitle').html(ThreadServer.title(data));
			Timer.add(ThreadServer.loadData, globtime, app);
		});
	},
	dumpThead : function(obj, threadId) {
		if (!checkHasSelectApp())
			return;
		if (threadId) {
			$.get(thUrl + '?threadId=' + threadId, function(result) {
				$('#detailPnl').show();
				$('#detailBody').text(result);
			});
		} else {
			window.open(thUrl + '?app=' + app);
		}
	},
	checkDeadLock : function(obj) {
		$.get(dlUrl, function(res) {
			if (res.hasdeadlock) {
				alert(res.thread);
			} else {
				alert(NO_DEAD_LOACK);
			}
		});
	},
	showHiddenDetail : function() {
		$('#detailPnl').toggle();
	}
};
Shell = {
	sendCmd : function(cmd) {
		LongPolling.send(cmd, 123456, function(t) {
			console.log(t);
		});
	},
	init : function(resId, source) {
		var cmd = $('#' + source);
		cmd.on('keyup', function(e) {
			if ((e.keyCode || e.which) == 13) {
				var text = cmd.val();
				if (text.length > 0) {
					Shell.sendCmd(text);
				}
			}
		});
		var obj = $('#' + resId);
		LongPolling.addListener(function(res) {
			if (res.id == 123456 && res.data.length > 0) {
				obj.val(res.data);
			} else {
				obj.val('no response');
			}
		});
	}
};
function buildNav(readerId) {
	var name = {
		'实时监控' : './index.html',
		'系统参数' : './runtime.html',
		'线程信息' : './thread.html',
		'LinuxSSH' : './shell.html'
	};
	var cur = window.location.pathname;
	var k = 0;
	var html = '';
	var active = false;
	for (i in name) {
		if ((k == 0 && cur == '/')) {
			active = true;
			k++;
		} else if (k == 0 && name[i].indexOf(cur) > 0) {
			active = true;
			k++;
		}
		if (active) {
			html += '<li class="active" role="presentation"><a href="';
		} else {
			html += '<li role="presentation"><a href="';
		}
		html += name[i] + '">' + i + '</a></li>';
		active = false;
	}
	$('#' + readerId).html(html);
}
$(function() {
	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});
	$.ajaxSetup({
		error : handleAjaxErr,
		beforeSend : beforeAjaxSend
	});
	$('[id=context]').each(function(i, item) {
		$(item).html('<img alt="logo" src="./flogo.png" width="95%">')
	});
	buildNav('nav');
	buildDropmenu('timemenu');
	updateAppTree();
	LongPolling.start();
	LongPolling.addListener(updateAppTree);
});