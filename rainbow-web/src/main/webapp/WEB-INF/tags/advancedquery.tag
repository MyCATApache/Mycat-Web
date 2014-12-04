<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag body-content="empty"%>
<%@ tag dynamic-attributes="tagAttrs"%>
<%@ attribute name="title" required="false"%>
<%@ attribute name="whereName" required="true"%>
<%@ attribute name="gridQuery" required="true"%>
<%@ attribute name="gridId" required="true"%>
<div id="${gridQuery}" style="display:none;">
	<span onclick="tag_aq_ddlSreach('${whereName}','${gridId}','${title}',${whereName}_fieldData,'${whereName}_title')" style="cursor:pointer"><font style="font-weight:900;"><font color="#22196a">高级查询</font></font><a class="icon-search"></a></span>
</div>
<script>
var ${whereName}_count = 1;
var ${whereName}_title;
var ${whereName}_fieldData =[];
$(function($) {
	rainbowAjax.post('./dispatcherAction/query.do?service=whereexService&method=query&whereCode=${whereName}',function(data) {
		var rows = data.rows;
		if (rows.length > 0) {
			${whereName}_fieldData = rows;
			${whereName}_title = rows[0]["whereName"];
		} else {
			$('#tag_aq_table_${whereName}').hide();
		};
	});
});
</script>