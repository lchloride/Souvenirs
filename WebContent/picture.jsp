<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %> 
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
	width: 40px;
	height: 40px;
	padding: 0px;
	margin-left: 10px;
	margin-right: 10px;
}

div.comment-content {
	float: left;
	width: 75%;
}

div.meta-data {
	
}

div.comment-username {
	/* 	float: left; */
	color: #696969;
}

div.comment-time {
	/* 	float: right; */
	color: #888888;
}

div.comment {
	clear: both;
	word-break: break-all;
	border-bottom: solid;
	border-width: 1px;
	margin-top: 5px;
	padding-bottom: 10px;
	border-color: rgba(153, 153, 153, 0.7);
}
</style>
<script type="text/javascript">
	var salbum_obj = new Object(<%=((List<String>)request.getAttribute("SAlbum_own_pic_json_list")).size()%>);
	var liking_person_json = '${Liking_person_json}';
	
	$(document).ready(function() {
		displayLikingPersons();
		<c:if test="${Is_personal}">
		changeShareStatus(0);
		$("#onoffswitch").on('click', function() {
			clickSwitch()
		});

		var clickSwitch = function() {
			if ($("#onoffswitch").is(':checked')) {
				;//console.log("在ON的状态下");  
			} else {
				;//console.log("在OFF的状态下");  
			}
		};
		</c:if>
	});
	
	function displayLikingPersons() {
		liking_person_obj = JSON.parse(liking_person_json);
		display_str = "";
		if (liking_person_obj == 0) {
			document.getElementById("liking_persons").style.display = "none";
		}else {
			document.getElementById("liking_persons").style.display = "block";
			for (i=0; i<liking_person_obj.length-1; i++) {
				display_str += liking_person_obj[i]+", ";
			}
			display_str += liking_person_obj[liking_person_obj.length-1];
			document.getElementById("liking_persons_name").innerHTML = display_str;
		}
	}
	function changeShareStatus(s) {
		//alert(s);
		if (salbum_obj[s+1].is_shared)
			document.getElementById("onoffswitch").checked = "checked";
		else
			document.getElementById("onoffswitch").checked = "";
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
					<li class="active"><a href="homepage">HomePage</a></li>
					<li><a href="#">Group</a></li>
					<li><a href="upload">Upload</a></li>
					<li><a href="making">Making</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}"
						alt="avatar" width="32" height="32"></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${sessionScope.username} <b
							class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li><a href="account.jsp">Account</a></li>
							<li class="divider"></li>
							<li><a href="logout">Logout</a></li>
						</ul></li>
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
							<li><a href="homepage">${Username }</a></li>
							<li><a href="album?album_name=${Album_name }">${Album_name }</a></li>
							<li class="active">${Picture_name }</li>
						</ol>
					</small>
				</c:if>
				<c:if test="${not Is_personal }">
					Picture Details <small>
						<ol class="breadcrumb"
							style="background-color: transparent; padding-bottom: 0px; margin-bottom: 0px; display: inline-block;">
							<li class="active" style="color:#337ab7">${Username }</li>
							<li><a href="sharedAlbum?group_id=${Group_id }">${Group_name }</a></li>
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
									<div><a href="res/image/laugh.gif">
										<img id="picture_preview" src="${Picture }" alt="${Picture_name }" style="width: 100%">
									</a></div>
								</div>
								<a href="/Souvenirs/showPicture?addr=${Picture }"><button type="button" class="btn btn-default btn-sm" style="margin: 5px auto; width: 120px">Original
									Size</button></a>
							</div>

							<div id="liking_persons">
								<span class="glyphicon glyphicon-thumbs-up"></span> <span id="liking_persons_name"></span>
							</div>
						</div>

						<div class="col-sm-4 information">
							<h4>Information</h4>
							<form class="form-horizontal" role="form">
								<div class="form-group">
									<label for="picture_name" class="col-sm-5 col-md-4 col-lg-3 control-label">Name</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="picture_name" value="${Picture_name}" ${not Is_personal?"disabled":"" }>
									</div>
								</div>
								<div class="form-group">
									<label for="album_name" class="col-sm-5 col-md-4 col-lg-3 control-label">Album</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="album_name" value="${Album_name }" disabled>
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
									</div>
								</div>
								<div class="form-group">
									<label for="description" class="col-sm-5 col-md-4 col-lg-3 control-label" style="letter-spacing: -1px;">Description</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<textarea id="description" class="form-control" rows="3" ${not Is_personal?"disabled":"" }>${Description }</textarea>
									</div>
								</div>
								<c:if test="${Is_personal }">
									<div class="form-group">
										<label for="share" class="col-sm-5 col-md-4 col-lg-3 control-label">Share to</label>
										<div class="col-sm-7 col-md-8 col-lg-9">
											<select class="form-control" onchange="changeShareStatus(this.options.selectedIndex)">
												<c:forEach var="salbum_name" items="${SAlbum_own_pic_json_list }" varStatus="idx">
													<option id="salbum_name_${idx.count }" value="${idx.count }"></option>
													
													<script>
														idx=${idx.count};
														salbum_json = '${salbum_name}';
														salbum_obj[idx] = JSON.parse(salbum_json);
														document.getElementById("salbum_name_"+idx).innerHTML = salbum_obj[idx].salbum_name;
													</script>
												</c:forEach>
											</select>
											<!-- 										<div class="btn-group btn-group-sm" style="margin-top: 10px;">
												<button type="button" class="btn btn-success" style="width: 70px">Share</button>
												<button type="button" class="btn btn-danger" style="width: 70px" disabled>Unshare</button>
											</div> -->
											<div class="testswitch" id="test_switch"style="margin-top: 10px;">
												<input class="testswitch-checkbox" id="onoffswitch" type="checkbox" >
												<label class="testswitch-label" for="onoffswitch"> <span class="testswitch-inner" data-on="Shared"
													data-off="Unshared"></span> <span class="testswitch-switch"></span>
												</label>
											</div>
												
										</div>
									</div>

									<div class="form-group">
										<div class="col-sm-offset-5 col-md-offset-4 col-lg-offset-3 col-sm-7 col-md-8 col-lg-9">
											<button type="submit" class="btn btn-primary">Save Changes</button>
											<button type="submit" class="btn btn-default">Discard Changes</button>
										</div>
									</div>
								</c:if>
							</form>
						</div>

						<div class="col-sm-5">
							<h4>Comments</h4>
							<div id="write_comment" style="display: inline-block; margin-top: 10px;">
								<img class="user-avatar-img" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}" alt="avatar"
									width="32" height="32" style="float: left" />
								<textarea id="comment" class="form-control" style="width: auto; display: inline; vertical-align: bottom;"
									rows="1" placeholder="Write your comment."></textarea>
								<button class="btn btn-info" style="width: 57px;">Send</button>
							</div>
							<!-- Comment list -->
							<div class="comments-display">
								<c:forEach  var="comment_item" items="${Comment_json_list }" varStatus="idx">
									<!-- One comment item -->
									<div class="comment-item">
										<div class="user-avatar">
											<img class="user-avatar-img" id="comment_user_avatar_${idx.count }" src="" alt="avatar" />
										</div>
										<div class="comment-content">
											<div class="meta-data">
												<div class="comment-username">
													<strong id="comment_username_${idx.count }" style="line-height:200%;">username</strong>
													
												</div>
												<div class="comment-time">
													<small id="comment_time_${idx.count }">2016-01-01 00:00:00</small>
												</div>
											</div>
											<div class="comment" >
												<span id="reply_title_${idx.count }" style="display:none;">Reply </span>
												<span id="reply_username_${idx.count }" style="color:#337ab7;"></span>
												<span id="reply_end_${idx.count }" style="display:none;">: </span>
												<span id="comment_content_${idx.count }">This is comment content.</span>
											</div>
										</div>
									</div>

									<script>
										idx = ${idx.count};
										comment_json = '${comment_item}';
										comment_obj = JSON.parse(comment_json);
										document.getElementById("comment_username_"+idx).innerHTML = comment_obj.comment_username;
										document.getElementById("comment_time_"+idx).innerHTML = comment_obj.comment_time;
										document.getElementById("comment_user_avatar_"+idx).src = comment_obj.comment_user_avatar;
										document.getElementById("comment_content_"+idx).innerHTML = comment_obj.comment_content;
										if (comment_obj.reply_username != "") {
											document.getElementById("reply_username_"+idx).innerHTML = "@"+comment_obj.reply_username;
											document.getElementById("reply_title_"+idx).style.display = "inline";
											document.getElementById("reply_end_"+idx).style.display = "inline";
										}
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