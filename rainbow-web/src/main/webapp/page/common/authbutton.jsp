<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
$(function($){
	rainbowAjax.post("./dispatcherAction/query.do?service=authbuttonService&method=queryByPageCode&pageCode=${param.pageCode}",function(data){
		for(var i =0;i<data.rows.length;i++){
			var buttonCode = data.rows[i].buttonCode;
			if(buttonCode!=null && buttonCode != ""){
				$("#"+buttonCode).css("display","none");
			}
		}
	},"json");
});
</script>