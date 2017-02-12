<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<script src="/Souvenirs/res/js/jquery.bootstrap-growl.min.js"></script>
<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">

<title>Picture Information</title>
<style type="text/css">
.picture-management {
	padding-left: 10px;
	padding-right: 10px;
}

.info-content {
	margin-top: 25px;
	padding-top: 5px;
	background-color: rgba(248, 248, 255, 0.5);
	margin-left: -10px;
	margin-right: -10px;
	padding-left: 10px;
	padding-right: 10px;
	background-color: rgba(248, 248, 255, 0.5);
}

div.information {
	border-left: solid;
	border-right: solid;
	border-width: 1px;
	border-color: rgba(192, 192, 192, 0.8);
}

div.comments-display {
	margin-top: 15px;
}

div.comment-item {
	background: #ffeeff;
	margin-top: 15px;
	clear: both;
}

div.user-avatar {
	display: inline;
	float: left;
}

img.user-avatar-img {
	border-style: solid;
	border-width: 1px;
	border-radius: 5px;
	border-color: #6495ED;
	width: 46px;
	height: 46px;
	padding: 0px;
	/*margin-left: 10px;*/
	/*margin-right: 10px;*/
}

img.reply_user-avatar-img {
	border-style: solid;
	border-width: 1px;
	border-radius: 5px;
	border-color: #6495ED;
	width: 40px;
	height: 40px;
	padding: 0px;	
}

div.reply {
	background-color: #e0ecff;
	margin-top:5px;
	padding-top:5px;
	padding-bottom:5px;
}

div.comment {
	border-top: solid;
	border-width:1px;
	border-color: #e0e0f0;
	padding-top:5px;
}
</style>
<script >
	<c:if test="${Is_personal}">
	var salbum_obj = new Array();//<%=((List<String>) request.getAttribute("SAlbum_own_pic_json_list")).size()%>
	</c:if>
	var liking_person_json = '${Liking_person_json}';
	var MSG_OFFSET = 50;
	var comment_list_obj = new Array();
	var reply_idx = -1;
	
	$(document).ready(function() {
		displayLikingPersons();
		$('#comment').css("width", ($('#write_comment').width() - $('.user-avatar-img').outerWidth(true) - $('#send_btn').outerWidth(true)
				- $('#comment').outerWidth(true) + $('#comment').width())
				+"px");
		$('#comments_count').text(comment_list_obj.length);
		<c:if test="${Is_personal}">
		changeShareStatus(0);
		$("#onoffswitch").on('click', function() {
			clickSwitch()
		});

		var clickSwitch = function() {
			if ($("#onoffswitch").is(':checked')) {
				var idx = document.getElementById("salbum_list").selectedIndex;//console.log("在ON的状态下");
				salbum_obj[idx].is_shared = true;
			} else {
				var idx = document.getElementById("salbum_list").selectedIndex;//console.log("在OFF的状态下");  
				salbum_obj[idx].is_shared = false;
			}
		};

		var success_msg = '${Success_msg}';
		var failure_msg = '${Failure_msg}';
		var success_msg_obj = JSON.parse(success_msg);
		var failure_msg_obj = JSON.parse(failure_msg);
		
		for (var i=0; i<success_msg_obj.length; i++)
			setTimeout($.bootstrapGrowl("Updating "+success_msg_obj[i]+" succeeded.", { type: 'success' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}}),1000);
		for (var i=0; i<failure_msg_obj.length; i++)
			setTimeout($.bootstrapGrowl("Error when "+failure_msg_obj[i], { type: 'danger' , delay:5000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
		</c:if>
	});

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
		if (display_str.indexOf('${Username}') >= 0)
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
/* 		if (salbum_obj.length > 0 && $("#onoffswitch").is(':checked') != salbum_obj[0].is_shared) {
			alert($("#onoffswitch").is(':checked')+" "+salbum_obj[0].is_shared);
			//$("#onoffswitch").click();
			$("#onoffswitch").is(':checked');
		} */
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
	
	function ajaxProcess(callback, URL, send)
	{

	var xmlhttp;
		if (window.XMLHttpRequest) {
			// IE7+, Firefox, Chrome, Opera, Safari 浏览器执行代码
			xmlhttp = new XMLHttpRequest();
		} else {
			// IE6, IE5 浏览器执行代码
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				callback(xmlhttp.responseText);
			}
		}
		if (send == undefined || send == "") {
			xmlhttp.open("GET", URL, true);
			xmlhttp.send();
		} else {
			xmlhttp.open("POST", URL, true);
			xmlhttp.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xmlhttp.send(send);
		}
	}

	function checkLikeOrDislike() {
		like_flag = false;
		for (var i = 0; i < liking_person_obj.length; i++) {
			if (liking_person_obj[i] == '${Username}') {
				like_flag = true;
				break;
			}
		}
		return like_flag;
	}
	
	function clickLike() {
		if (checkLikeOrDislike())
			ajaxProcess(clickLikeCallback,"/Souvenirs/dislikePicture?like_user_id="+encodeURIComponent('${Login_user_id}')+
							"&picture_user_id="+encodeURIComponent("${Picture_user_id}")+
							"&album_name="+encodeURIComponent("${Album_name}")+
							"&picture_name="+encodeURIComponent("${Picture_name}.${Format}"));
		else
			ajaxProcess(clickLikeCallback,"/Souvenirs/likePicture?like_user_id="+encodeURIComponent('${Login_user_id}')+
					"&picture_user_id="+encodeURIComponent("${Picture_user_id}")+
					"&album_name="+encodeURIComponent("${Album_name}")+
					"&picture_name="+encodeURIComponent("${Picture_name}.${Format}"));
	}
	
	function clickLikeCallback(result) {
		like_flag = checkLikeOrDislike();
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
	
	function sendComment() {
		var text = document.getElementById("comment").value;
		ajaxProcess(sendCommentCallback, "/Souvenirs/addComment?comment="+encodeURIComponent(text)+
				"&reply_comment_id="+(reply_idx+1)+
				"&picture_user_id="+encodeURIComponent("${Picture_user_id}")+
				"&album_name="+encodeURIComponent("${Album_name}")+
				"&picture_name="+encodeURIComponent("${Picture_name}.${Format}"));
		discardReply();
	}
	
	function sendCommentCallback(result) {
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
				"<button class='btn btn-link' style='padding:0px;' disabled>Report</button></span>";
			$('#comments_count').text(comment_list_obj.length);
			$.bootstrapGrowl("Comment successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
		}else
			$.bootstrapGrowl("Comment unsuccessfully. Error Description: "+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	} 
</script>
</head>
<body>
	<div class="mainbody">
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li class="active">
						<a href="homepage">HomePage</a>
					</li>
					<li>
						<a href="#">Group</a>
					</li>
					<li>
						<a href="upload">Upload</a>
					</li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li>
						<img class="navbar-form" src="${empty Avatar?'':Avatar}" alt="avatar" width="32"
							height="32">
					</li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">${sessionScope.username} <b class="caret"></b>
						</a>
						<ul class="dropdown-menu">
							<li>
								<a href="account.jsp">Account</a>
							</li>
							<li class="divider"></li>
							<li>
								<a href="logout">Logout</a>
							</li>
						</ul>
					</li>
				</ul>
			</div>
		</div>
		</nav>

		<div class="picture-management">
			<h3 style="padding-left: 15px;">
				<c:if test="${Is_personal }">
					Picture Management <small>
						<ol class="breadcrumb"
							style="background-color: transparent; padding-bottom: 0px; margin-bottom: 0px; display: inline-block;">
							<li>
								<a href="homepage">${Username }</a>
							</li>
							<li>
								<a href="album?album_name=${Album_name }">${Album_name }</a>
							</li>
							<li class="active">${Picture_name }</li>
						</ol>
					</small>
				</c:if>
				<c:if test="${not Is_personal }">
					Picture Details <small>
						<ol class="breadcrumb"
							style="background-color: transparent; padding-bottom: 0px; margin-bottom: 0px; display: inline-block;">
							<li class="active" style="color: #337ab7">${Username }</li>
							<li>
								<a href="sharedAlbum?group_id=${Group_id }">${Group_name }</a>
							</li>
							<li class="active">${Picture_name }</li>
						</ol>
					</small>
				</c:if>
			</h3>

			<div class="info-content">
				<div class="container" style="width: 100%;">
					<div class="row">
						<div class="col-sm-3">

							<div class="picture-preview">
								<h4>Image Preview</h4>
								<div class="gallery">
									<div>
										<a href="res/image/laugh.gif"> <img id="picture_preview" src="${Picture }" alt="${Picture_name }"
												style="width: 100%">
										</a>
									</div>
								</div>
								<a href="/Souvenirs/showPicture?addr=${Picture }">
									<button type="button" class="btn btn-default btn-sm"
										style="margin: 5px auto; width: 120px">Original Size</button></a>
								<a href="${Picture }"><button type="button" class="btn btn-default btn-sm"
										style="margin: 5px auto; width: 120px">Download</button>
								</a>
							</div>

							<div id="liking_persons">
								<span class="glyphicon glyphicon-thumbs-up" style="font-size:1.4em;cursor:pointer" onclick="clickLike()"></span>
								 <span id="liking_persons_name"></span>
							</div>
						</div>

						<div class="col-sm-4 information">
							<h4>Information</h4>
							<form class="form-horizontal" role="form" action="/Souvenirs/updatePictureInfo" method="post">
								<div class="form-group">
									<label for="picture_name" class="col-sm-5 col-md-4 col-lg-3 control-label">Name</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="picture_name" name="picture_name" value="${Picture_name}" ${not Is_personal?"disabled":"" }>
										<input type="hidden" class="form-control" name="original_picture_name" value="${Picture_name}">
									</div>
								</div>
								<div class="form-group">
									<label for="album_name" class="col-sm-5 col-md-4 col-lg-3 control-label">Album</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="album_name" value="${Album_name }" disabled>
										<input type="hidden" class="form-control" name="album_name" value="${Album_name }">
									</div>
								</div>
								<div class="form-group">
									<label for="owner" class="col-sm-5 col-md-4 col-lg-3 control-label">Owner</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="owner" value="${Owner }" disabled>
									</div>
								</div>
								<div class="form-group">
									<label for="format" class="col-sm-5 col-md-4 col-lg-3 control-label">Format</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="format" value="${Format }" disabled>
										<input type="hidden" class="form-control" name="format" value="${Format }" >
									</div>
								</div>
								<div class="form-group">
									<label for="description" class="col-sm-5 col-md-4 col-lg-3 control-label" style="letter-spacing: -1px;">Description</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<textarea id="description" class="form-control" rows="3" ${not Is_personal?"disabled":"" } name="description">${Description }</textarea>
										<input type="hidden" class="form-control" name="original_description" value="${Description }" >
									</div>
								</div>
								
								<c:if test="${Is_personal }">
									<div class="form-group">
										<label for="share" class="col-sm-5 col-md-4 col-lg-3 control-label">Share to</label>
										<div class="col-sm-7 col-md-8 col-lg-9">
											<select class="form-control" id="salbum_list" onchange="changeShareStatus(this.options.selectedIndex)" name="share">
												<c:forEach var="salbum_name" items="${SAlbum_own_pic_json_list }" varStatus="idx">
													<option id="salbum_name_${idx.count }" value="${idx.count }"></option>

													<script>
														idx = ${idx.count};
														salbum_json = '${salbum_name}';
														obj = JSON.parse(salbum_json);
														document	.getElementById("salbum_name_"	+ idx).innerHTML = obj.salbum_name;
														salbum_obj.push(obj);
													</script>
												</c:forEach>
											</select>

											<div class="testswitch" id="test_switch" style="margin-top: 10px;">
												<input class="testswitch-checkbox" id="onoffswitch" type="checkbox">
												<label class="testswitch-label" for="onoffswitch">
													<span class="testswitch-inner" data-on="Shared" data-off="Unshared"></span> <span class="testswitch-switch"></span>
												</label>
											</div>

										</div>
										<input type="hidden" id="salbum_json" name="salbum_json" value="">
										<script type="text/javascript">$('#salbum_json').attr("value", JSON.stringify(salbum_obj));</script>
									</div>

									<div class="form-group">
										<div class="col-sm-offset-5 col-md-offset-4 col-lg-offset-3 col-sm-7 col-md-8 col-lg-9">
											<button type="submit" class="btn btn-primary" onclick="return prepareSubmit()">Save Changes</button>
											<button type="reset" class="btn btn-default" onclick="resetSAlbumJSON()">Discard Changes</button>
										</div>
									</div>
								</c:if>
							</form>
						</div>

						<div class="col-sm-5">
							<h4 >Comments<span class="badge" id="comments_count"  style="margin-left:5px;vertical-align:top;"></span></h4>
							<div id="write_comment" style="display: inline-block; margin-top: 10px;width:100%;">
								<img class="user-avatar-img" src="${empty Avatar?'':Avatar}" alt="avatar"
									width="40" height="40" style="float: left" />
								<textarea id="comment" class="form-control" style="width: auto; display: inline;vertical-align:bottom;margin-left:10px;height:46px" rows="2"
									placeholder="Write your comment."></textarea>
								<button class="btn btn-info btn-lg" id="send_btn"style="width: 74px;"onclick="sendComment()">Send</button>
							</div>
							<!-- Replying comment message box -->
							<div class="alert alert-info alert-dismissable" id="reply_msg" style="display:none;padding-top:10px;padding-bottom:10px;margin-top:5px;">
								<button type="button" class="close"  onclick="discardReply()">&times;</button>
								<span id="reply_text"></span>
							</div>
							
							<!-- Comment list -->
							<div class="comments-display" id="comments_display">
								<c:forEach var="comment_item" items="${Comment_json_list }" varStatus="idx">
									<div class="media comment">
										<a class="pull-left" href="#">
											<img class="media-object user-avatar-img" id="comment_user_avatar_${idx.count }" src="" alt="avatar" width="40" height="40">
										</a>
										<div class="media-body">
											<h5 class="media-heading" id="comment_username_${idx.count }" style="font-weight:bold;"></h5>
											<small id="comment_time_${idx.count }" style="color:#999">2016-01-01 00:00:00</small>
											<div id="comment_content_${idx.count }"></div>
											<!-- Replied comment -->
											<div class="media reply" id="reply_${idx.count }" style="display:none">
												<a class="pull-left" href="#">
													<img class="media-object reply_user-avatar-img" id="replied_avatar_${idx.count }"src="" alt="Avatar" width="40" height="40">
												</a>
												<div class="media-body">
													<h5 class="media-heading" id="replied_username_${idx.count }"style="font-weight:bold;"></h5>
													<div id="replied_content_${idx.count }"></div>
												</div>
											</div>
											<!-- Replied comment END -->
										</div>
									</div>

									<script>
										idx = ${	idx.count};
										comment_json = '${comment_item}';
										comment_obj = JSON.parse(comment_json);
										comment_list_obj.push(comment_obj);
										document
												.getElementById("comment_username_"
														+ idx).innerHTML = comment_obj.comment_username;
										document.getElementById("comment_time_"
												+ idx).innerHTML = comment_obj.comment_time;
										document
												.getElementById("comment_user_avatar_"
														+ idx).src = comment_obj.comment_user_avatar;
										document
												.getElementById("comment_content_"
														+ idx).innerHTML = comment_obj.comment_content;
										if (comment_obj.replied_comment_id > 0) {
											document.getElementById("reply_"+idx).style.display = "block";
											replied_comment_obj = comment_list_obj[comment_obj.replied_comment_id-1];
											document.getElementById("comment_username_"+ idx).innerHTML += 
												"<span style='font-weight:normal'> Reply "+
												"<span style='color:#337ab7'>@"	+ replied_comment_obj.comment_username+'</span></span>';
											document.getElementById("replied_avatar_"+idx).src = replied_comment_obj.comment_user_avatar;
											document.getElementById("replied_avatar_"+idx).alt = replied_comment_obj.comment_username;
											document.getElementById("replied_username_"+idx).innerHTML = 
												"<span style='color:#337ab7'>@"+replied_comment_obj.comment_username+"</span>";
											document.getElementById("replied_content_"+idx).innerHTML = replied_comment_obj.comment_content;
										}
										document.getElementById("comment_username_"+ idx).innerHTML += 
											"<span style='font-weight:normal;float:right;'>"+
											"<button class='btn btn-link' id='reply_btn_"+(idx-1)+"' style='padding:0px;' onclick='reply("+(idx-1)+")'>Reply</button> | "+
											"<button class='btn btn-link' style='padding:0px;' disabled>Report</button></span>";
									</script>
								</c:forEach>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>


	</div>
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>