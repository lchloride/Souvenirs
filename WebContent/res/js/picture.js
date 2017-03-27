/**
 * 
 */
	function displayLikingPersons() {
		liking_person_obj = JSON.parse(liking_person_json);
		display_str = "";
		if (liking_person_obj.length == 0) {
			;//document.getElementById("liking_persons").style.display = "none";
		} else {
			//document.getElementById("liking_persons").style.display = "block";
			for (i = 0; i < liking_person_obj.length - 1; i++) {
				display_str += liking_person_obj[i] + ", ";
			}
			display_str += liking_person_obj[liking_person_obj.length - 1];
		}
		document.getElementById("liking_persons_name").innerHTML = display_str;
		if (display_str.indexOf(Username) >= 0)
			$('.glyphicon-thumbs-up').css("color", "#cd201d");
		else
			$('.glyphicon-thumbs-up').css("color", "#286090");
	}
	
	function changeShareStatus(s) {
		//alert(s);
		if (salbum_obj[s].is_shared)
			document.getElementById("onoffswitch").checked = "checked";
		else
			document.getElementById("onoffswitch").checked = "";
	}
	
	function resetSAlbumJSON() {
		var original_json = $('#salbum_json').val();
		salbum_obj = JSON.parse(original_json);
		if (salbum_obj.length > 0) {
			if (salbum_obj[0].is_shared)
				$("#onoffswitch").attr("checked", "checked");
			else
				$("#onoffswitch").removeAttr("checked");
		}
	}
	
	function prepareSubmit() {
		$('#salbum_json').attr("value", JSON.stringify(salbum_obj));
	}
	


	function checkLikeOrDislike(Username) {
		like_flag = false;
		for (var i = 0; i < liking_person_obj.length; i++) {
			if (liking_person_obj[i] == Username) {
				like_flag = true;
				break;
			}
		}
		return like_flag;
	}
	
	function clickLike(Login_user_id, Picture_user_id, Album_name, Picture_name, Format, Username) {
		if (checkLikeOrDislike(Username))
			ajaxProcess(clickLikeCallback,"/Souvenirs/dislikePicture?like_user_id="+encodeURIComponent(Login_user_id)+
							"&picture_user_id="+encodeURIComponent(Picture_user_id)+
							"&album_name="+encodeURIComponent(Album_name)+
							"&picture_name="+encodeURIComponent(Picture_name+'.'+Format));
		else
			ajaxProcess(clickLikeCallback,"/Souvenirs/likePicture?like_user_id="+encodeURIComponent(Login_user_id)+
					"&picture_user_id="+encodeURIComponent(Picture_user_id)+
					"&album_name="+encodeURIComponent(Album_name)+
					"&picture_name="+encodeURIComponent(Picture_name+'.'+Format));
	}
	
	function clickLikeCallback(result) {
		like_flag = checkLikeOrDislike(Username);
		if (like_flag)
			oper_text = "Dislike";
		else
			oper_text = "Like";
		
		if (result.indexOf('[')==0) {
			$.bootstrapGrowl(oper_text+" it successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
			liking_person_json = result;
			displayLikingPersons();
		} else
			$.bootstrapGrowl(oper_text+" it unsuccessfully.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	}
	
	function reply(idx) {
		if (reply_idx > -1)
			$('#reply_btn_'+reply_idx).css('font-weight', "normal");
		reply_idx = idx;
		$('#reply_msg').css("display", "block");
		$('#reply_text').text("Reply "+comment_list_obj[reply_idx].comment_username+" (Comment ID: "+(reply_idx+1)+")");
		$('#reply_btn_'+reply_idx).css('font-weight', "bold");
	}
	
	function discardReply() {
		if (reply_idx > -1)
			$('#reply_btn_'+reply_idx).css('font-weight', "normal");
		$('#reply_msg').css("display", "none");
		reply_idx = -1;
	}
	
	function sendComment(picture_user_id, album_name, picture_name, format) {
		var text = document.getElementById("comment").value;
		ajaxProcess(sendCommentCallback, "/Souvenirs/addComment?comment="+encodeURIComponent(text)+
				"&reply_comment_id="+(reply_idx+1)+
				"&picture_user_id="+encodeURIComponent(picture_user_id)+
				"&album_name="+encodeURIComponent(album_name)+
				"&picture_name="+encodeURIComponent(picture_name+"."+format));
		discardReply();
	}
	
	function sendCommentCallback(result) {
		//result = decodeURIComponent(result); 
		alert(result);
		if (result.indexOf('{') == 0) {
			var cobj = JSON.parse(result);
			comment_list_obj.push(cobj);
			var idx = comment_list_obj.length;
			var html = '<div class="media comment">'+
			'<a class="pull-left" href="#">'+
				'<img class="media-object user-avatar-img" id="comment_user_avatar_'+idx+'" src="'+cobj.comment_user_avatar+'" alt="'+cobj.comment_username+'" width="40" height="40">'+
			'</a>'+
			'<div class="media-body">'+
				'<h5 class="media-heading" id="comment_username_'+idx+'" style="font-weight:bold;">'+cobj.comment_username+'</h5>'+
				'<small id="comment_time_'+idx+'" style="color:#999">'+cobj.comment_time+'</small>'+
				'<div id="comment_content_'+idx+'">'+cobj.comment_content+'</div>';
			if (cobj.replied_comment_id > 0) {
				var reply_id = cobj.replied_comment_id-1;
				alert(reply_id+" "+cobj.replied_comment_id);
				html += '<div class="media reply" id="reply_'+idx+'" style="display:block">'+
					'<a class="pull-left" href="#">'+
						'<img class="media-object user-avatar-img" id="replied_avatar_'+idx+'"src="'+comment_list_obj[reply_id].comment_user_avatar+'" alt="'+comment_list_obj[reply_id].comment_username+'" width="40" height="40">'+
					'</a>'+
					'<div class="media-body">'+
						'<h5 class="media-heading" id="replied_username_'+idx+'"style="font-weight:bold;"><span style="color:#337ab7">@'+comment_list_obj[reply_id].comment_username+'</span></h5>'+
						'<div id="replied_content_'+idx+'">'+comment_list_obj[reply_id].comment_content+'</div>'+
					'</div>'+
				'</div>';
			}
			html +='</div></div>';
			document.getElementById("comments_display").innerHTML += html;
			if (cobj.replied_comment_id > 0) {
				replied_comment_obj = comment_list_obj[cobj.replied_comment_id-1];
				document.getElementById("comment_username_"+ idx).innerHTML += 
					"<span style='font-weight:normal'> Reply "+
					"<span style='color:#337ab7'>@"	+ replied_comment_obj.comment_username+'</span></span>';
			}
			document.getElementById("comment_username_"+ idx).innerHTML += 
				"<span style='font-weight:normal;float:right;'>"+
				"<button class='btn btn-link' id='reply_btn_"+(idx-1)+"' style='padding:0px;' onclick='reply("+(idx-1)+")'>Reply</button> | "+
				"<button class='btn btn-link' style='padding:0px;' data-toggle='modal' data-target='#myModal' onclick='report_idx="+idx+"'>Report</button></span>";
			$('#comments_count').text(comment_list_obj.length);
			$.bootstrapGrowl("Comment successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
		}else
			$.bootstrapGrowl("Comment unsuccessfully. Error Description: "+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	} 
		
	function checkCommentContent() {
		text = $('#comment').val();
		if (text.length == 0)
			$('#send_btn').attr("disabled","disabled");
		else
			$('#send_btn').removeAttr("disabled");
	}
	
	function reportComment(radio_count, Picture_user_id, Album_name, Picture_name, Format) {
		for (var i=1; i<=radio_count; i++) {
			if (document.getElementById("optionsRadios"+i).checked) {
				reason_idx = i;
				break;
			}
		}
		var report_label = (reason_idx != 6 ?$('#optionsRadios'+reason_idx).val():$('#optionsRadios'+reason_idx).val()+":"+$("#other_reason_text").val());
		ajaxProcess(reportCommentCallback, "/Souvenirs/reportComment?report_content="+encodeURIComponent($("#report_content").val())+
				"&comment_id="+(report_idx)+
				"&picture_uid="+encodeURIComponent(Picture_user_id)+
				"&album_name="+encodeURIComponent(Album_name)+
				"&picture_name="+encodeURIComponent(Picture_name+'.'+Format)+
				"&report_label="+encodeURIComponent(report_label));		
	}
	
	function reportCommentCallback(result) {
		if (result.trim() == "true")
			$.bootstrapGrowl("Report it successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
		else
			$.bootstrapGrowl("Report failed. Error:"+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	}
	
	function checkOtherReasonText() {
		var count = $('#other_reason_text').val().length;
		if (count > 30) {
			$('#other_reason_text').addClass("error-form");
			$('#other_reason_text_too_long_msg').css('display', 'inline');
			$('#report_btn').attr('disabled', 'disabled');
		} else {
			$('#other_reason_text').removeClass("error-form");
			$('#other_reason_text_too_long_msg').css('display', 'none');
			$('#report_btn').removeAttr('disabled');
		}
	}
	
	function checkReportContent() {
		var count = $('#report_content').val().length;
		$('#word_count').text(count);
		if (count > 300) {
			$('#report_content').addClass('error-form');
			$('#word_count').css('color', '#b3414c');
			$('#report_btn').attr('disabled', 'disabled');
		} else {
			$('#report_content').removeClass('error-form');
			$('#word_count').css('color', '#000');
			$('#report_btn').removeAttr('disabled');
		}
	}