<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- homepage页面，是用户的个人主页，这个页面上有少许的展示和较多的指向基本操作的链接 -->
<!-- 在页面上完成的操作：显示自己的相册；显示别人分享的相册；创建一个新相册；用户注销 -->
<!-- 可以跳转的链接：用户主页；上传照片；制作Souvenir；显示帮助；个人相册管理；共享相册管理；个人信息维护； 进入小组操作面板-->

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<link href="/Souvenirs/res/css/website.css" rel="stylesheet"
	type="text/css">
<!-- <link href="/Souvenirs/res/css/homepage.css" rel="stylesheet"
	type="text/css"> -->
<title>Homepage</title>
<style type="text/css">
.three-steps {
	margin-top: 50px;
}

.step {
	height: 180px;
	padding: 5px;
	margin-top: 20px;
	padding: 5px;
	/* 		border-style:solid; */
}

.step .step-up {
	height: 60px;
	border-style: solid;
	border-width: 1px;
	border-radius: 5px;
	text-align: center;
	background-color: rgba(192, 192, 192, .3);
	min-width: 130px;
}

.step .step-up .step-title {
	margin-top: 16px;
	margin-down: 20px;
	font-size: 20px;
	font-weight: bold;
}

.step .step-down {
	height: 120px;
	border-style: solid;
	border-width: 1px;
	border-radius: 5px;
	text-align: center;
	background-color: rgba(192, 192, 192, .3);
	min-width: 130px;
}

.step .step-down .step-content {
	margin-top: 35px;
	margin-down: 40px;
	margin-left: 10px;
	margin-right: 10px;
	font-size: 16px;
}

.step span {
	height: 65px;
	display: inline-block;
	vericle-align: middle;
}

.step img {
	vericle-align: middle;
	height: 50px;
	margin: 0 auto;
}

.album_manage {
	margin-top: 50px;
	margin-right: 0px;
	padding-right: 3%;
	width: 100%;
}

div.img {
	margin: 5px;
	padding: 3px;
	border: 2px solid #ffffff;
	border-radius: 5px;
	height: auto;
	width: auto;
	float: left;
	text-align: center;
	height: auto;
	border: 2px solid #ffffff;
}

div.img img {
	display: inline;
	margin: 3px;
	border: 1px solid #ffffff;
}

div.img:hover {
	background-color: rgba(232, 232, 232, .8);
	border: 2px solid #cccccc;
}

div.desc {
	text-align: center;
	font-weight: normal;
	width: 120px;
	margin: 2px;
	margin-top: 5px;
	white-space: nowrap;
	text-overflow: ellipsis;
	overflow: hidden;
}

div.desc:hover {
	text-overflow: inherit;
	overflow: visible;
}

div.desc a:link {
	color: #000000;
} /* 未访问链接*/
div.desc a:visited {
	color: #000000;
} /* 已访问链接 */
div.desc a:hover {
	color: #000000;
} /* 鼠标移动到链接上 */
div.desc a:active {
	color: #000000;
} /* 鼠标点击时 */
.manage-content {
	border-left-style: solid;
	border-width: 1px;
	text-align: center;
}

.small-grid {
	padding-left: 0px;
	padding-right: 5px;
}

.page-number {
	background-color: rgba(30, 144, 255, 0.8);
	color: white;
	padding: 5px;
	padding-left: 10px;
	padding-right: 10px;
	border-radius: 5px;
}
</style>
</head>

<body>
	<!-- mainbody is the content part except footer of website infomation -->
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
					<li><img class="navbar-form"
						src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}" alt="avatar"
						width="32" height="32"></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">${sessionScope.username} <b
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

		<div class="three-steps">
			<h2 class="text-center">Three Steps to Make A Souvenirs</h2>
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-2 step">
					<div class="step-up">
						<div class="step-title">Step One</div>
					</div>
					<div class="step-down">
						<div class="step-content">
							Upload Pictures<br />Edit Description
						</div>
					</div>
				</div>
				<div class="col-md-2 step">
					<span></span><img alt="Right Arrow"
						src="/Souvenirs/res/image/arrow.png" class="img-responsive">
				</div>
				<div class="col-md-2 step">
					<div class="step-up">
						<div class="step-title">Step Two</div>
					</div>
					<div class="step-down">
						<div class="step-content">
							Choose Style<br /> You Like
						</div>
					</div>
				</div>
				<div class="col-md-2 step">
					<span></span><img alt="Right Arrow"
						src="/Souvenirs/res/image/arrow.png" class="img-responsive">
				</div>
				<div class="col-md-2 step">
					<div class="step-up">
						<div class="step-title">Step Three</div>
					</div>
					<div class="step-down">
						<div class="step-content">
							Make Your <br />Souvenirs
						</div>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
		</div>

		<div class="row album_manage">
			<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">

				<ul id="myTab" class="nav nav-tabs nav-stacked">
					<li><a href="#my_albums" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.0em">My Albums</a></li>
					<li><a href="#shared_albums" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.0em">Shared Albums</a></li>
					<li><a href="#create_album" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.0em">Create Album</a></li>
				</ul>
			</div>

			<div class=" col-lg-10 col-md-10 col-sm-10 col-xs-10 manage-content">
				<form>
					<div id="TabContent" class="tab-content">
						<div class="tab-pane fade" id="my_albums">

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/default_avatar.png"
									alt="default_avatar" width="120" height="120"></a>
								<div class="desc">
									<a target="_blank" href="#">My Album1</a>
								</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_1.png" alt="Carousel_1"
									width="120" height="120"></a>
								<div class="desc">
									<a target="_blank" href="#">My Album2</a>
								</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_2.png" alt="Carousel_2"
									width="120" height="120"></a>
								<div class="desc">
									<a target="_blank" href="#">My Album3</a>
								</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_3.png" alt="Carousel_3"
									width="120" height="120"></a>
								<div class="desc">
									<a target="_blank" href="#">My Album4</a>
								</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_4.png" alt="Carousel_4"
									width="120" height="120"></a>
								<div class="desc">
									<a target="_blank" href="#">My Album5</a>
								</div>
							</div>

							<div style="clear: both; padding-top: 5px;">
								<ul class="pager">
									<li style="margin-right: 10%"><a href="#">&larr;
											Previous</a></li>
									<li class="page-number">Page 1 of 3</li>
									<li style="margin-left: 10%"><a href="#">Next &rarr;</a></li>
								</ul>
							</div>
						</div>

						<div class="tab-pane fade" id="shared_albums">
							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/default_avatar.png"
									alt="default_avatar" width="120" height="120"></a>
								<div class="desc">Shared Album1</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_1.png" alt="Carousel_1"
									width="120" height="120"></a>
								<div class="desc">Shared Album2</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_2.png" alt="Carousel_2"
									width="120" height="120"></a>
								<div class="desc">Shared Album3</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_3.png" alt="Carousel_3"
									width="120" height="120"></a>
								<div class="desc">Shared Album4</div>
							</div>

							<div class="img">
								<a target="_blank" href="#"><img
									src="/Souvenirs/res/image/Carousel_4.png" alt="Carousel_4"
									width="120" height="120"></a>
								<div class="desc">Shared Album5</div>
							</div>

							<div style="clear: both; padding-top: 5px;">
								<ul class="pager">
									<li style="margin-right: 10%"><a href="#">&larr;
											Previous</a></li>
									<li class="page-number">Page 1 of 3</li>
									<li style="margin-left: 10%"><a href="#">Next &rarr;</a></li>
								</ul>
							</div>
						</div>

						<div class="tab-pane fade" id="create_album">
							<div class="row">

								<div class="col-sm-5">
									<div class="form-group">
										<label for="firstname"
											class="col-sm-4 control-label small-grid">Album Name</label>
										<div class="col-sm-8 small-grid">
											<input type="text" class="form-control" id="firstname"
												placeholder="Input album name">
										</div>
									</div>
								</div>

								<div class="col-sm-4">
									<div class="form-group">
										<label for="firstname"
											class="col-sm-3 control-label small-grid">Cover</label>
										<div class="col-sm-9 small-grid">
											<input type="text" class="form-control" id="firstname"
												placeholder="Choose its cover from file system">
										</div>
									</div>
								</div>

								<div class="col-sm-3 small-grid" style="text-align:left">
									<button class="btn btn-default btn-sm" type="submit">Choose
										Cover</button>
									<button class="btn btn-default btn-sm" type="submit">Upload</button>	
								</div>

							</div>

							<br />
							<div class="row">
								<div class="col-sm-5">
									<div class="form-group">
										<label for="firstname"
											class="col-sm-4 control-label small-grid">Description</label>
										<div class="col-sm-8 small-grid">
											<textarea class="form-control" rows="3"
												placeholder="Enter description, no more than 200 letters"></textarea>
										</div>
									</div>
								</div>

								<div class="col-sm-4">
									<div class="form-group">
										<label for="firstname"
											class="col-sm-3 control-label small-grid">Cover
											Preview</label>
										<div class="col-sm-9 small-grid">
											<img class="img-rounded manage-content"
												src="/Souvenirs/image" width="120" height="120">
										</div>
									</div>
								</div>
							</div>

							<br />
							<div class="row">
								<div class="col-sm-2">
									<button type="submit" class="btn btn-primary">Create</button>
								</div>
							</div>

						</div>
						<script>
							$(function() {
								$('#myTab li:eq(0) a').tab('show');
							});
						</script>

					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="footer">Copyright &copy; 2016 Souvenirs, All Rights
		Reserved.</div>
</body>
</html>