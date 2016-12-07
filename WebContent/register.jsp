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

<link href="/Souvenirs/res/css/register.css" rel="stylesheet"
	type="text/css">
<title>Register</title>
<style type="text/css">
</style>
</head>
<body>
	<div class="mainbody">
		<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">AnimeHelper</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li class="active"><a href="index.jsp">HomePage</a></li>
					<li><a href="#">Help</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> Reference <b class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li><a href="#">WebSite1</a></li>
							<li><a href="#">WebSite2</a></li>
							<li><a href="#">WebSite3</a></li>
							<li class="divider"></li>
							<li><a href="#">WebSite4</a></li>
						</ul></li>
				</ul>
				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<c:choose>
						<c:when test="${not empty isLogin  and isLogin == true}">
							<li class="dropdown"><a href="#" class="dropdown-toggle"
								data-toggle="dropdown">${sessionScope.username } <b
									class="caret"></b>
							</a>
								<ul class="dropdown-menu">
									<li><a href="#">Profile</a></li>
									<li><a href="#">Settings</a></li>
									<li><a href="#">Hello</a></li>
									<li class="divider"></li>
									<li><a href="/AnimeHelper/en_US/logout">Logout</a></li>
								</ul></li>
						</c:when>
						<c:otherwise>
							<li><a href="#"><span class="glyphicon glyphicon-user"></span>
									Register</a></li>
							<li><a href="/AnimeHelper/en_US/login"><span
									class="glyphicon glyphicon-log-in"></span> Login</a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div>
		</nav>
	</div>
</body>
</html>