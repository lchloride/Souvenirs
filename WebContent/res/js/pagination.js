/**
 * 七段式分页插件样式控制js
 * 需要引用先jquery库
 * html中以bootstrap的形式插入七个分页列表
 */
	function pagination(id, active_idx, total_page) {
		for (var i=0; i<7; i++) {
			$('#'+id+' li:eq('+i+') a').text(i+1);
			$('#'+id+' li:eq('+i+')').removeClass('active');
			$('#'+id+' li:eq('+i+')').removeClass('disabled');
			$('#'+id+' li:eq('+i+')').removeClass('active');
			$('#'+id+' li:eq('+i+')').css('display', 'inline');
			$('#'+id+' li:eq('+i+') a').css('border-top-right-radius','0px');
			$('#'+id+' li:eq('+i+') a').css('border-bottom-right-radius','0px');
		}
		if (total_page <= 7) {
			for (var i=0; i<total_page; i++) {
				$('#'+id+' li:eq('+i+') a').text(i+1);
			}
			for (var i=total_page; i<7; i++) {
				$('#'+id+' li:eq('+i+')').css('display', 'none');
			}
			$('#'+id+' li:eq('+(total_page-1)+') a').css('border-top-right-radius','4px');
			$('#'+id+' li:eq('+(total_page-1)+') a').css('border-bottom-right-radius','4px');
			$('#'+id+' li:eq('+(active_idx-1)+')').addClass('active');
		} else {
			if (active_idx >=1 && active_idx <= 4) {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('2');
				$('#'+id+' li:eq(2) a').text('3');
				$('#'+id+' li:eq(3) a').text('4');
				$('#'+id+' li:eq(4) a').text('5');
				$('#'+id+' li:eq(5) a').text('...');
				$('#'+id+' li:eq(5) a').addClass('disabled');
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq('+(active_idx-1)+')').addClass('active');
			} else if (active_idx >=total_page-3 && active_idx <= total_page) {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('...');
				$('#'+id+' li:eq(1) a').addClass('disabled');
				$('#'+id+' li:eq(2) a').text(total_page-4);
				$('#'+id+' li:eq(3) a').text(total_page-3);
				$('#'+id+' li:eq(4) a').text(total_page-2);
				$('#'+id+' li:eq(5) a').text(total_page-1);
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq('+(6-total_page+active_idx)+')').addClass('active');
			} else {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('...');
				$('#'+id+' li:eq(1) a').addClass('disabled');
				$('#'+id+' li:eq(2) a').text(active_idx-1);
				$('#'+id+' li:eq(3) a').text(active_idx);
				$('#'+id+' li:eq(4) a').text(active_idx+1);
				$('#'+id+' li:eq(5) a').text('...');
				$('#'+id+' li:eq(5) a').addClass('disabled');
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq(3)').addClass('active');
			}
			
			$('#'+id+' li:eq(6) a').css('border-top-right-radius','4px');
			$('#'+id+' li:eq(6) a').css('border-bottom-right-radius','4px');
		}
	}
