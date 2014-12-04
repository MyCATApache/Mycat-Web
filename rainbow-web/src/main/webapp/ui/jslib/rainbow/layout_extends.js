$.extend($.fn.layout.methods, {
	full : function (jq) {
		return jq.each(function () {
			var layout = $(this);
			var center = layout.layout('panel', 'center');
			center.panel('maximize');
			center.parent().css('z-index', 10);
			$(window).on('resize.full', function () {
				layout.layout('unFull').layout('resize');
			});
		});
	},
	unFull : function (jq) {
		return jq.each(function () {
			var center = $(this).layout('panel', 'center');
			center.parent().css('z-index', 'inherit');
			center.panel('restore');
			$(window).off('resize.full');
		});
	}
});