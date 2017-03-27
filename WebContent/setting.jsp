<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<script src="/Souvenirs/res/js/jquery.bootstrap-growl.min.js"></script>
<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<!-- <link href="/Souvenirs/res/css/homepage.css" rel="stylesheet"
	type="text/css"> -->

<!--[if lt IE 9]><script language="javascript" type="text/javascript" src="excanvas.js"></script><![endif]-->
<script type="text/javascript" src="/Souvenirs/res/js/homepage.js"></script>
<link rel="stylesheet" type="text/css" href="/Souvenirs/res/css/homepage.css" />
<title>Setting</title>
</head>
<script>
	function changePwd() {
		if ($('#pwd_checkbox').is(':checked')) {
			$('#old_password').removeAttr('disabled');
			$('#new_password').removeAttr('disabled');
			$('#new_pwd_confirm').removeAttr('disabled');
		} else {
			$('#old_password').attr('disabled', 'disabled');
			$('#new_password').attr('disabled', 'disabled');
			$('#new_pwd_confirm').attr('disabled', 'disabled');
		}
	} 
	function displayFilename() {
		var filepath = $('#upload_file').val();
		alert(filepath);
		$('#filename_display').html(filepath.substring(filepath.lastIndexOf('\\') + 1));
		$('#filename').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
		$('#album_name').css('display', 'inline');
		$('#album_name_label').css('display', 'inline');
		$('#avatar').css("opacity", "0.1");
	}
</script>
<body>
		<!-- mainbody is the content part except footer of website infomation -->
	<div class="mainbody" style="min-width:1024px;">
		
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation" >
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li><a href="homepage">HomePage</a></li>
					<li><a href="group">Group</a></li>
					<li><a href="upload">Upload</a></li>
					<li  class="active"><a href="#">Setting</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form" src="${Avatar}"
						alt="avatar" width="32" height="32" style="width:62px;"></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${sessionScope.username} <b
							class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li><a href="setting">Account</a></li>
							<li class="divider"></li>
							<li><a href="logout">Logout</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
		</nav>
		<!-- Navigate bar END -->
		<div id="main_content">
			<form class="form-horizontal" role="form" method="post" action="updateSettings" enctype="multipart/form-data">
				<div class="form-group">
					<label for="user_id" class="col-sm-2 control-label">UserID</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="user_id" name="user_id" value="${User_id}" disabled>
					</div>
				</div>
				<div class="form-group">
					<label for="username" class="col-sm-2 control-label">Username</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="username" name="username" value="${Username}">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-8">
						<div class="checkbox">
							<label>
								<input type="checkbox" id="pwd_checkbox" onchange="changePwd()" name="pwd_checkbox" >Change Password								
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label for="old_password" class="col-sm-2 control-label">Old Password</label>
					<div class="col-sm-8">
						<input type="password" class="form-control" id="old_password" name="old_password" placeholder="" disabled>
					</div>
				</div>
				<div class="form-group">
					<label for="new_password" class="col-sm-2 control-label">New Password</label>
					<div class="col-sm-8">
						<input type="password" class="form-control" id="new_password" name="new_password" placeholder="" disabled>
					</div>
				</div>	
				<div class="form-group">
					<label for="new_pwd_confirm" class="col-sm-2 control-label">New Password Confirmation</label>
					<div class="col-sm-8">
						<input type="password" class="form-control" id="new_pwd_confirm" name="new_pwd_confirm" placeholder="" disabled>
					</div>
				</div>	
				<div class="form-group">
					<label for="avatar" class="col-sm-2 control-label">Profile Picture</label>
					<div class="col-sm-8">
						<img src="${Avatar}" alt="Avatar"  id="avatar"width="120px" height="120px">
						<table style="display:inline;margin-left:20px;vertical-align:bottom">
							<tbody style="vertical-align:bottom">
							<tr><td>
								<label class="btn btn-sm btn-default " for="upload_file" style="vertical-align:bottom">Change Profile Picture</label> 
								<span class="" id="filename_display" style="text-overflow: ellipsis; clear: both; white-space: nowrap; overflow: hidden;"></span>
								<input type="file" name="upload_file" id="upload_file" name="upload_file" style="position: absolute; clip: rect(0, 0, 0, 0); display: inline"
									onchange="displayFilename()" accept="image/*">
								<input type="hidden" style="display:  none;" id="filename" name="filename">
							</td></tr>
							<tr><td>
								<label for="album_name" class="control-label" id="album_name_label"  style="display:none;">Album Storing New Profile Picture</label>
								<select id="album_name" name="album_name" class="form-control" style="display:none;width:auto;">
									<c:forEach var="item"  items="${PAlbum }">
										<option>${item }</option>
									</c:forEach>
								</select>
							</td><tr>
							</tbody>
						</table>
					</div>
				</div>

				<div class="form-group">
					<label for="username" class="col-sm-2 control-label">Maximum Reloading Times</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="MRT" value="${MRT}" name="MRT">
						<span class="help-block">Maximum Reloading Times(MRT) stands for reloading times of a picture when 
						creating souvenir. If a picture has been loaded MRT times, loading would be stopped. <strong>Default value is 3.</strong></span>
					</div>
				</div>	
				<div class="form-group">
					<label for="username" class="col-sm-2 control-label">Loading Timeout</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" id="LT" value="${LT}" name="LT">
						<span class="help-block">Loading Timeout(LT) stands for loading timeout of a picture when 
						creating souvenir. If loading has lasted LT seconds, loading would be stopped. <strong>Default value is 15.</strong></span>
					</div>
				</div>	
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-primary">Save</button>
						<button type="reset" class="btn btn-warning">Reset</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	<!-- mainbody END -->
</body>
</html>