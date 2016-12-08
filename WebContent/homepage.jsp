<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
	height: 180px; padding : 5px;
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
	margin:0 auto;
}

.album_manage {
	margin-top: 50px;
}
</style>
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
					<li class="active"><a href="homepage.jsp">HomePage</a></li>
					<li><a href="#">Group</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form"
						src="/Souvenirs/res/image/default_avatar.png" alt="avatar"
						width="32" height="32"></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">${sessionScope.username }example <b
							class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li><a href="#">Account</a></li>
							<li class="divider"></li>
							<li><a href="index.jsp">Logout</a></li>
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
						<div class="step-content">Choose Style<br/> You Like</div>
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
						<div class="step-content">Make Your <br/>Souvenirs</div>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
		</div>

		<div class="row album_manage">
			<div class="col-lg-3 col-md-3 col-sm-3 col-xs-2">

				<ul id="myTab" class="nav nav-tabs nav-stacked">
					<li><a href="#my_albums" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.2em">My Albums</a></li>
					<li><a href="#shared_albums" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.2em">Shared Albums</a></li>
					<li><a href="#create_album" data-toggle="tab"
						style="padding-left: 15%; font-size: 1.2em">Create Album</a></li>
				</ul>
			</div>
			<div class=" col-lg-9 col-md-9 col-sm-9 col-xs-10 " style="">
				<div id="myTabContent" class="tab-content">
					<div class="tab-pane fade" id="my_albums">
						<p>W3Cschoool菜鸟教程是一个提供最新的web技术站点，本站免费提供了建站相关的技术文档，帮助广大web技术爱好者快速入门并建立自己的网站。菜鸟先飞早入行——学的不仅是技术，更是梦想。</p>
					</div>
					<div class="tab-pane fade" id="shared_albums">
						<p>iOS 是一个由苹果公司开发和发布的手机操作系统。最初是于 2007 年首次发布 iPhone、iPod Touch
							和 Apple TV。iOS 派生自 OS X，它们共享 Darwin 基础。OS X 操作系统是用在苹果电脑上，iOS
							是苹果的移动版本。</p>
					</div>
					<div class="tab-pane fade" id="create_album">
						<p>jMeter 是一款开源的测试软件。它是 100% 纯 Java 应用程序，用于负载和性能测试。</p>
					</div>
				</div>
				<script>
					$(function() {
						$('#myTab li:eq(0) a').tab('show');
					});
				</script>
			</div>
		</div>
	</div>
	<div class="footer">Copyright &copy; 2016 Souvenirs, All Rights
		Reserved.</div>
</body>
</html>