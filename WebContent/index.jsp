<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh_CN">
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

<link href="/Souvenirs/res/css/index.css" rel="stylesheet"
	type="text/css">
<title>Souvenirs</title>

</head>
<body>
	<div class="mainbody">
		<div class="login">
			<div class="login-form">
				<h2 style="text-align: center">Souvenirs</h2>
				<h6 style="text-align: center; color: #606060">In memory of our
					graduation season</h6>
				<br />
				<form action="homepage.jsp" method="POST" id="form_login"
					onsubmit="return checkSubmit()">
					<div class="form-group">
						<input type="text" class="form-control" id="username"
							name="Text_username" placeholder="Username or ID" value="">
					</div>
					<div class="form-group">
						<input type="password" class="form-control" id="password"
							name="Text_password" placeholder="Password" value="">
					</div>
					<div class="form-group">
						<input type="text" class="form-control" id="verifycode"
							name="Text_verifycode" placeholder="Case insensitive" value="" style="width:58%;display:inline">
							<label for="verifycode" style="width:40%"><img src="/Souvenirs/res/image/verifycode/verifycode1.png" ></label>
					</div>
					<button type="submit" class="btn btn-primary">Login</button>
					<a href="register.jsp" class="btn btn-primary" role="button">Register</a>
					<!-- <button type="button" class="btn btn-primary">Register</button> -->
				</form>
			</div>
		</div>

		<div class="user-info">
			<a href="#" target="_blank"> <img class="user-icon"
				src="res/image/index_bg_user.jpg" width="48" height="48">
			</a>
			<div class="description">
				<div class="illust-title">
					<a href="#" target="_blank"> 爱在南开 </a>
				</div>
				<div class="user-name">
					<a href="#" target="_blank"> By 2008级中文系王俊峰 </a>
				</div>
			</div>
		</div>
		<!-- 	
		<div class="row">
			<div class="col-md-9">
							<div id="myCarousel" class="carousel slide" data-ride="carousel">
					轮播（Carousel）指标
					<ol class="carousel-indicators">
						<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
						<li data-target="#myCarousel" data-slide-to="1"></li>
						<li data-target="#myCarousel" data-slide-to="2"></li>
						<li data-target="#myCarousel" data-slide-to="3"></li>
					</ol>
					轮播（Carousel）项目
					<div class="carousel-inner" style="height: 400px">
						<div class="item active">
							<img src="/Souvenirs/res/image/Carousel_1.png" alt="First slide"
								height="266" width="800" class="img-responsive">
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_2.png" alt="Second slide"
								height="266" width="800" class="img-responsive">
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_3.png" alt="Third slide"
								height="266" width="800" class="img-responsive">
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_4.png" alt="Fourth slide"
								height="266" width="800" class="img-responsive">
						</div>
					</div>
					轮播（Carousel）导航
					<a class="carousel-control left" href="#myCarousel"
						data-slide="prev">&lsaquo; </a> <a class="carousel-control right"
						href="#myCarousel" data-slide="next">&rsaquo; </a>

					<script>
						$(function() {
							// 初始化轮播
							$(".start-slide").click(function() {
								$("#myCarousel").carousel('cycle');
							});
						});
					</script>
				</div>
			</div>
			<div class="col-md-3">
				<span style="border-style: solid; border-width: 5px;"></span>
			</div>

		</div>
 -->
		<div class="bottom-pos">Copyright &copy; 2016 Souvenirs, All
			Rights Reserved.</div>
	</div>
</body>
</html>