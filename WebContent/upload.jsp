<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en_US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Image Upload</title>
<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"
	type="text/javascript"></script>
</head>
<body>
	<div class="mainbody">
		<form method="post" action="upload" enctype="multipart/form-data">
			选择一个文件: <input type="file" name="uploadFile" /> <br />
			<br /> <input type="submit" value="上传" />
		</form>
	</div>
<label class="btn btn-primary" for="xFile">上传文件</label>
<form><input type="file" id="xFile" style="position:absolute;clip:rect(0 0 0 0);"></form>
</body>
</html>