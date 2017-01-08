<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- index界面，用户正常第一次访问网站的页面，也是用户登录的页面 -->
<!-- 页面首次加载之后，触发onload消息，从后台获取验证码 -->

<html lang="zh_CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- icon file of title -->
<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<!-- Bootstrap core css -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- jQuery -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- Bootstrap core js -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>

<!-- index page css -->
<link href="/Souvenirs/res/css/index.css" rel="stylesheet"	type="text/css">

<title>Souvenirs</title>
<!-- Check whether this is the first time to load the page. if true, verify code would be loaded. -->
<c:if test="${empty Firsttime or ((not empty Firsttime) and Firsttime == true)}">
<script>
	window.onload = function() {
		document.getElementById("form_login").submit();
	}
</script>
</c:if>
</head>

<body>
	<div class="mainbody">
		<!-- Login form part  -->
		<div class="login">
			<div class="login-form">
				<h2 style="text-align: center">Souvenirs</h2>
				<h6 style="text-align: center; color: #606060">In memory of our
					graduation season</h6>
				<c:choose>
					<c:when test="${not empty Msg}"><h6 style="color:firebrick;text-align:center;font-weight:bold">${Msg }</h6></c:when>
					<c:otherwise>	<br /></c:otherwise>
				</c:choose>

				<form action="login" method="POST" id="form_login"
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
							<label for="verifycode" style="width:40%"><img src="${'/Souvenirs/res/image/verifycode/'}${VerifyCode }${'.png'}" ></label>
					</div>
					<button type="submit" class="btn btn-primary">Login</button>
					<a href="register.jsp" class="btn btn-primary" role="button">Register</a>
					<!-- <button type="button" class="btn btn-primary">Register</button> -->
				</form>
			</div>
		</div>

		<!-- Artist information of background picture -->
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
		
		<!-- Footer part at the bottom of page -->
		<div class="bottom-pos">Copyright &copy; 2016 Souvenirs, All
			Rights Reserved.</div>
	</div>
</body>
</html>