
//div id
var alert_div_id = "_alert_div_id";

var sec;

var _interval;

var after ;

var AlertMsg = function(msg,seconds,_after){
	
	after = _after;
	if(seconds!=null){
		sec = seconds;
	}else{
		sec = 1;
	}
	
	
	var div = $("#"+alert_div_id);
	
	if(div.length==0){
		 //create div
		var div = "<div style=\"display: none;\" id=\""+alert_div_id+"\" title=\"提示框\"><p align=\"center\"></p><p align=\"right\" style=\"font-size: xx-small;\"></p></div>";
		$(document.body).append(div);
	}
	
	$("#"+alert_div_id).find("p:eq(0)").html(msg);


	$( "#"+alert_div_id).dialog();
	
	
	_interval = window.setInterval("changeMsg()",1000);
	
}




function mclearInterval(){
	window.clearInterval(_interval);
	$( "#"+alert_div_id).dialog("close");
	if(after!=null){
		after();
	}
}

function changeMsg(){
	if(sec==0){
		mclearInterval();
	}else{
		var msg = "窗口将在"+sec+"秒后关闭";
		$("#"+alert_div_id).find("p:eq(1)").html(msg);
	}
	--sec;
}
