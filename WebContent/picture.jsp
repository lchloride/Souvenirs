<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	padding-top: 5px; background-color : rgba( 248, 248, 255, 0.5);
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
$(document).ready(function() {  
    $("#onoffswitch").on('click', function(){  
        clickSwitch()  
    });  
  
    var clickSwitch = function() {  
        if ($("#onoffswitch").is(':checked')) {  
            ;//console.log("在ON的状态下");  
        } else {  
            ;//console.log("在OFF的状态下");  
        }  
    };  
});  
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
					<li>
						<a href="making">Making</a>
					</li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li>
						<img class="navbar-form" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}" alt="avatar" width="32"
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
				Picutre Management <small>
					<ol class="breadcrumb"
						style="background-color: transparent; padding-bottom: 0px; margin-bottom: 0px; display: inline-block;">
						<li>
							<a href="#">${Username }</a>
						</li>
						<li>
							<a href="#">${Album_name }</a>
						</li>
						<li class="active">${Picture_name }</li>
					</ol>
				</small>
			</h3>

			<div class="info-content">
				<div class="container" style="width: 100%;">
					<div class="row">
						<div class="col-sm-3">

							<div class="picture-preview">
								<h4>Image Preview</h4>
								<img id="picture_preview" src="${Picture }res/image/BBB.png" alt="${Picture_name }" style="width: 100%">
								<button type="button" class="btn btn-default btn-sm" style="margin: 5px auto; width: 120px">Original Size</button>
							</div>

							<div>
								<span class="glyphicon glyphicon-thumbs-up"></span> <span id="liking_persons"></span>
							</div>
						</div>

						<div class="col-sm-4 information">
							<h4>Information</h4>
							<form class="form-horizontal" role="form">
								<div class="form-group">
									<label for="picture_name" class="col-sm-5 col-md-4 col-lg-3 control-label">Name</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<input type="text" class="form-control" id="picture_name" value="${Picture_name}">
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
										<input type="text" class="form-control" id="owner" value="${Username }" disabled>
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
										<textarea id="description" class="form-control" rows="3">${Description }</textarea>
									</div>
								</div>
								<div class="form-group">
									<label for="share" class="col-sm-5 col-md-4 col-lg-3 control-label">Share to</label>
									<div class="col-sm-7 col-md-8 col-lg-9">
										<select class="form-control">
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
										</select>
<!-- 										<div class="btn-group btn-group-sm" style="margin-top: 10px;">
											<button type="button" class="btn btn-success" style="width: 70px">Share</button>
											<button type="button" class="btn btn-danger" style="width: 70px" disabled>Unshare</button>
										</div> -->
										<div class="testswitch" style="margin-top:10px;">
											<input class="testswitch-checkbox" id="onoffswitch" type="checkbox">
											<label class="testswitch-label" for="onoffswitch">
												<span class="testswitch-inner" data-on="Shared" data-off="Unshared"></span> <span class="testswitch-switch"></span>
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
							</form>
						</div>

						<div class="col-sm-5">
							<h4>Comments</h4>
							<div id="write_comment" style="display: inline-block; margin-top: 10px;">
								<img class="user-avatar-img" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}" alt="avatar"
									width="32" height="32" style="float: left" />
								<textarea id="comment" class="form-control" style="width: auto; display: inline; vertical-align: bottom;" rows="1"
									placeholder="Write your comment."></textarea>
								<button class="btn btn-info" style="width: 57px;">Send</button>
							</div>
							<!-- Comment list -->
							<div class="comments-display">
								<!-- One comment item -->
								<div class="comment-item">
									<div class="user-avatar">
										<img class="user-avatar-img" src="res/image/prepare.png" alt="avatar" />
									</div>
									<div class="comment-content">
										<div class="meta-data">
											<div class="comment-username">
												<strong>123</strong>
											</div>
											<div class="comment-time">
												<small>2016-12-15 0:0:0</small>
											</div>
										</div>
										<div class="comment">Nice moment!1234567899874563210.311254657898445</div>
									</div>
								</div>
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