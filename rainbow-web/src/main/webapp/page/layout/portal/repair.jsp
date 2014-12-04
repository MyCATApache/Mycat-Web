<%@ page language="java" pageEncoding="UTF-8"%>
<div>
	<!-- <a href="${pageContext.request.contextPath}/init.jsp">修复异常数据<br />恢复到初始化数据库状态</a> -->
</div>
<br />
<br />
<div>
	<a href="javascript:void(0);" onclick="$.cookie('portal-state', null);$.messager.show({title:'提示',msg:'初始化成功！请刷新页面！'});" class="easyui-linkbutton">恢复初始portal顺序</a>
</div>
<script type="text/javascript">
	function shows(){
		var json = {                                                      
				"total":239,                                                      
				"haha":"huangxin"                                               
			};   
		$.ajax({
            url: "./menusAction/query.do",
            data: { data: json },
            type: "post",
            success: function (text) {
                alert(text);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseText);
            }
        });
	}
</script>