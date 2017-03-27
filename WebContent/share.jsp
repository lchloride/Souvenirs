<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<!-- <script src="/Souvenirs/res/js/imgpreview.min.0.22.jquery.js"></script> -->
<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<link href="/Souvenirs/res/css/share.css" rel="stylesheet" type="text/css">
<script src="/Souvenirs/res/js/ajax.js"></script>
<script src="/Souvenirs/res/js/share.js"></script>
<title>Share</title>
<script type="text/javascript">
	var image_obj;
	var selected_image = new Array();
	var MSG_OFFSET = 50;
	window.onload = function() {
		$(".container").css("min-width",
				($("#mainbody").width()-60) + "px");
		drawImageList('${image_list_json}');
		displaySelectedPicture();
	}
	
</script>
</head>
<body>
	<div class="mainbody" id="mainbody">
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li ><a href="homepage">HomePage</a></li>
					<li><a href="#">Group</a></li>
					<li><a href="upload">Upload</a></li>
					<li class="active"><a>Sharing Pictures</a></li>
				</ul>
				
				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form" src="${empty Avatar?'':Avatar}" alt="avatar" width="32" height="32" style="width:62px;"></li>
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

		<div class="container" style="min-width: 80%;width:auto;margin-left:15px;margin-right:15px;">
			<h3>Share Pictures to Album <a href="/Souvenirs/sharedAlbum?group_id=${Group_id }">${SAlbum_name }</a> <small>(Group ID: ${Group_id })</small></h3>
			<div class="row" style="margin-top:40px;">
				<div class="col-sm-6 list-title"><h4>Available</h4></div>
				<div class="col-sm-5 col-sm-offset-1 list-title">
					<h4 style="display:inline-block;">Share List </h4>
					<span class="badge" id="share_list_num" style="margin-bottom: 10px;margin-left: 5px;"></span>
					<button class="btn btn-primary btn-sm" style="float:right;margin-top: 5px;" onclick="sharePictures('${Group_id}')">Share</button>
				</div>
			</div>
			
			
			<div class="row">
				<div class="col-sm-6 list-panel">
					<div class="row">
						<div class="form-group">
    						<label for="lastname" class="col-sm-4 control-label" style="padding-top: 10px; ">Album Name</label>
    						<div class="col-sm-8">
      							<select class="form-control" onchange="queryPictureInAlbum(this.value)">
									<c:forEach var="option" items="${Album_name_list }"><option>${option }</option></c:forEach>
								</select>
							</div>
						</div>
					</div>

					<table class="table">
						<caption>Pictures in album <button type="button" class="btn btn-link " style="padding: 0px;float:right;" onclick="selectImageListAll()">Select All</button></caption>
						<tbody id="image_list_table_body">
							<!-- The content of table will be put in share.js -->
						</tbody>
					</table>

				</div>

				<div class="col-sm-1">
					<button onclick="addPictures()">Add &gt;</button><br>
					<button onclick="addAllPictures()" disabled style="display: none;">Add All&gt;&gt;</button><br>
				</div>

				<div class="col-sm-5 list-panel">
					<table class="table">
						<thead>
							<tr>
								<!-- <th>Select</th> -->
								<th style="border-bottom: 2px solid #bbb;">Album Name</th>
								<th style="border-bottom: 2px solid #bbb;">Filename</th>
								<th style="border-bottom: 2px solid #bbb;">Operation</th>
							</tr>
						</thead>
						<tbody id="selected_image_table_body">
							<!--  -->
						</tbody>
					</table>
				</div>
			</div>

		</div>
	</div>

	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>