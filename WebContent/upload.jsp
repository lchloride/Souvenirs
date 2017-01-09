<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<link href="/Souvenirs/res/css/website.css" rel="stylesheet"
	type="text/css">
<script type="text/javascript">
	/* if flag of Upload_result is true, it means that this time of loading is after one uploading process, which should display its result.*/
	window.onload = function() {
		if (${not empty Upload_result})
			//Display modal dialog of uploading result, using Bootstrap
		$(function () { $('#myModal').modal({
			keyboard: true
		})});
	}
	
	/* This fucntion is used to display filename to input form of pic_name after choosing a picture. */
	function displayFilename() {
		var filepath = document.getElementById('upload_file').value;
		document.getElementById('filename_display').innerHTML = filepath
				.substring(filepath.lastIndexOf('\\') + 1);
		document.getElementById('pic_name').value = filepath.substring(filepath.lastIndexOf('\\') + 1, filepath.lastIndexOf('.'));
	}

	/* Check whether all parameters assigned by user is valid or not. */
	function checkSubmit() {
		if (document.getElementById("img_description").value.length > 200) {
			alert("Too long desctription!");
			return false;
		}
		if (document.getElementById("upload_file").value == undefined
				|| document.getElementById("upload_file").value == ""
				|| document.getElementById("upload_file").value == null) {
			alert("Please choose a file!");
			return false;
		}
		var filepath = document.getElementById('upload_file').value;
		if (filepath.substring(filepath.lastIndexOf('.') + 1)!="jpg" 
				&& filepath.substring(filepath.lastIndexOf('.') + 1)!="png" 
				&& filepath.substring(filepath.lastIndexOf('.') + 1)!="gif")	{
			alert("Only allow jpg, png and gif image!");
			return false;
		}
		return true;
	}
</script>
<style type="text/css">
.mainbody-content {
	margin-left: 50px;
	margin-right: 50px;
}

.heading {
	border-bottom-style: solid;
	border-width: 1px;
	border-color: black;
	padding-bottom: 20px;
	color: #30353D;
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
					<li class="active"><a href="homepage">HomePage</a></li>
					<li><a href="#">Group</a></li>
					<li><a href="upload">Upload</a></li>
					<li><a href="making">Making</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form"
						src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}"
						alt="avatar" width="32" height="32"></li>
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

		<!-- Main part of uploading operation panel  -->
		<div class="mainbody-content">
			<h3 class="heading">Image Upload</h3>

			<form class="form-horizontal" method="post" action="upload"
				enctype="multipart/form-data" onsubmit="return checkSubmit()">
				<div class="row" style="margin-bottom: 15px;">
					<h5 class="col-sm-2 text-right " style="font-weight: bold;">Filename</h5>
					<div class="col-sm-10">
						<label class="btn btn-sm btn-default " for="upload_file">Choose
							Local Image</label> <span class="" id="filename_display"
							style="text-overflow: ellipsis; clear: both; white-space: nowrap; overflow: hidden;"></span>
					</div>

					<input type="file" name="upload_file" id="upload_file"
						style="position: absolute; clip: rect(0, 0, 0, 0); display: inline"
						onchange="displayFilename()" accept="image/jpg">

				</div>

				<div class="form-group">
					<label for="pic_name" class="col-sm-2 control-label">Picture
						Name</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="pic_name"
							name="pic_name" placeholder="">
					</div>
				</div>

				<div class="form-group">
					<label for="lastname" class="col-sm-2 control-label">Album</label>
					<div class="col-sm-10">
						<select class="form-control" name="select_album_name"
							id="select_album_name">
							<c:forEach var="Album_item" items="${Album_list}">
								<option>${Album_item }</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="form-group">
					<label for="img_description" class="col-sm-2 control-label">Description</label>
					<div class="col-sm-9">
						<textarea id="img_description" class="form-control" rows="3"
							name="img_description"
							onkeydown="document.getElementById('word_count').innerHTML=this.value.length.toString()+'/200'"
							onkeyup="document.getElementById('word_count').innerHTML=this.value.length.toString()+'/200'"></textarea>
					</div>
					<div class="col-sm-1" id="word_count">200/200</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<input type="submit" class="btn btn-primary" value="Upload & Save">
						<input type="button" class="btn btn-default" value="Cancel"
							onclick="confirmCancel()">
					</div>
				</div>


			</form>
		</div>

	</div>

	<div class="footer">Copyright &copy; 2016 Souvenirs, All Rights
		Reserved.</div>

	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">Message</h4>
				</div>
				<div class="modal-body">
					<c:if test="${Upload_result }">Uploading Picture Succeeded!<br>
						<small style="color: #999999"><strong>Album:</strong>
							${Album_name }, <strong>Filename:</strong> ${Filename }</small>
					</c:if>
					<c:if test="${not Upload_result }">Uploading Picture Failed!<br>
						<small style="color: #999999">Error Message: ${Error_msg }</small>
					</c:if>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Upload
						Again</button>
					<button type="button" class="btn btn-primary"
						onclick="location.href='/Souvenirs/homepage'">Go to
						Homepage</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>
</body>
</html>