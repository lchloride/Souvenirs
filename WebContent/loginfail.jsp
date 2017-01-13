<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Failed</title>
<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<style type="text/css">
html, body {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
	font-family:Tahoma, "Microsoft YaHei", sans-serif;
}

.content {
	width: 500px;
	height: 150px;
	border-style: solid;
	border-width: 1px; margin : 0 auto; /*水平居中*/
	position: relative; /*脱离文档流*/
	top: 50%; /*偏移*/
	transform: translateY(-50%);
	text-align: center;
	margin: 0 auto;
}

h3.text {
	font-size:20px;
}

span.text {
	font-size: 16px;
}
</style>
<script language="javascript" type="text/javascript">
	var i = 5;
	var intervalid;
	intervalid = setInterval("fun()", 1000);
	function fun() {
		i--;
		document.getElementById("mes").innerHTML = i;
		if (i == 0) {
			window.location.href = "index.jsp";
			clearInterval(intervalid);
		}
		
		
	}
</script>
</head>
<body>
	<div class="content" >
		<h3 class="text">This Login has expired, please login again.</h3>
		<span class="text">The browser will redirect to login page in <span id="mes">5</span> seconds.<br /> If
		browser does not redirect automatically, please click on <a href="index.jsp">this link.</a>
		</span>
	</div>
</body>
</html>