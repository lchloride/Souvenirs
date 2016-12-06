<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh_CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>

<title>Souvenirs</title>
<style type="text/css">
body {
	background-color: #ccccff;
	background-image: url('./res/image/index_bg.jpg');
	background-size: cover;
}

.mainbody {
	margin-left: 5%;
	margin-top: 5%;
	margin-right: 5%;
	font-family: "Microsoft YaHei UI", Arial, sans-serif;
}
.
</style>
</head>
<body>
	<div class="mainbody">
		<h1 style="text-align: center">
			Souvenirs<br /> <small style="padding-left: 250px">In memory
				of our graduation season</small>
		</h1>
		<div class="row">
			<div class="col-md-9">
				<div id="myCarousel" class="carousel slide" data-ride="carousel">
					<!-- 轮播（Carousel）指标 -->
					<ol class="carousel-indicators">
						<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
						<li data-target="#myCarousel" data-slide-to="1"></li>
						<li data-target="#myCarousel" data-slide-to="2"></li>
						<li data-target="#myCarousel" data-slide-to="3"></li>
					</ol>
					<!-- 轮播（Carousel）项目 -->
					<div class="carousel-inner" style="height:400px">
						<div class="item active">
							<img src="/Souvenirs/res/image/Carousel_1.png" alt="First slide" height="266" width="800"
								>
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_2.png" alt="Second slide" height="266" width="800">
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_3.png" alt="Third slide"height="266" width="800">
						</div>
						<div class="item">
							<img src="/Souvenirs/res/image/Carousel_4.png" alt="Fourth slide" height="266" width="800">
						</div>
					</div>
					<!-- 轮播（Carousel）导航 -->
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
				
				<div class="col-md-3" >
					<span style="border-style:solid;border-width:5px;" ></span>
				</div>
			</div>
		</div>
	</div>
</body>
</html>