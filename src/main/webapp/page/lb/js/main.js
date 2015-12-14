/**
 * @author coder_czp@126.com
 * @desc Mycat-LB
 * @date 2015/11/15
 */

function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

$(function() {
	$('#mainfrm').load(getQueryString('to'));
});