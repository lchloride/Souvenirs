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
<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<title>Manage Album</title>
<script type="text/javascript">
	var original_album_name = '${Album_name}';
	var original_description = '${Description}';
	var mouse_over_idx = 0;
	window.onload = function() {
		if ($("#album_show").innerHeight() < $("#album_info").innerHeight()){
			document.getElementById("album_show").className = "col-sm-9";
			document.getElementById("album_info").className = "col-sm-3 border-left";
		} else {
			document.getElementById("album_show").className = "col-sm-9 border-right";
			document.getElementById("album_info").className = "col-sm-3";			
		}
	}
	
	function activate(idx) {
		if (mouse_over_idx>0){
			<c:if test="${Is_personal}">
				document.getElementById("img_edit_btn_"+mouse_over_idx).style.display = "none";
				document.getElementById("img_delete_btn_"+mouse_over_idx).style.display = "none";
			</c:if>
			<c:if test="${not Is_personal}">
				document.getElementById("img_details_btn_"+mouse_over_idx).style.display = "none";
			</c:if>
		}
		mouse_over_idx = idx;
		<c:if test="${Is_personal}">
			document.getElementById("img_edit_btn_"+idx).style.display = "inline";
			document.getElementById("img_delete_btn_"+idx).style.display = "inline";
		</c:if>
		<c:if test="${not Is_personal}">
			document.getElementById("img_details_btn_"+mouse_over_idx).style.display = "inline";
		</c:if>
	}
	
	function normalize() {
		document.getElementById("img_edit_btn_"+mouse_over_idx).style.display = "none";
		document.getElementById("img_delete_btn_"+mouse_over_idx).style.display = "none";
	}
</script>
<style type="text/css">
.container {
	width: 100%
}

.border-right {
	border-right: solid;
	border-width: 2px;
	border-color: rgba(96, 96, 96, .8);
}

.border-left {
	border-left: solid;
	border-width: 2px;
	border-color: rgba(96, 96, 96, 0.8);
}

div.album-cover {
	margin: 0 auto;
	width: 122px;
}

div.album-cover img {
	border: solid;
	border-width: 1px;
	border-color: rgba(96, 96, 96, 0.8);
	border-radius: 5px;
}

.operation-btn {
	display: none;
	margin-top:2px;
	margin-bottom:2px;
}
</style>
</head>
<body>
	<div class="mainbody"  >
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation" >
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
					<li><img class="navbar-form" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}"
						alt="avatar" width="32" height="32"></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${sessionScope.username} <b
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

		<div class="container">
			<h3 class="">
				<c:if test="${Is_personal }">
					Personal Album Management
				</c:if>
				<c:if test="${not Is_personal }">
					Shared Album Management
				</c:if><small>(Web Designer has gone home.....)</small>
			</h3>
			<div class="row">
				<div class="col-sm-9" id="album_show">
					<div class="operations">
<!-- 							<button type="button" class="btn btn-default">Upload Image</button>
					<button type="button" class="btn btn-default">Manage Information</button>
						<button type="button" class="btn btn-default">Delete Image</button> -->
					</div>

					<div class="images">
						<c:forEach var="image_item" items="${image_json_list }" varStatus="idx">

							<div class="img" onmouseover="activate(${idx.count})" onmouseout="normalize(${idx.count})">
								<a id="image_item_frame_${idx.count }" target="_self" href="#"> <img id="image_item_img_${idx.count }"
									src="" alt="" width="120" height="120"></a>
								<div class="desc">
									<a id="image_item_text_${idx.count }" target="_self" href="#"></a><br>
									<c:if test="${Is_personal }">
										<button type="button" class="btn btn-default btn-xs operation-btn"  id="img_edit_btn_${idx.count }"
											onclick="window.location.href=''">
											<span class="glyphicon glyphicon-pencil"></span> Edit
										</button>
										<button type="button" class="btn btn-default btn-xs operation-btn" id="img_delete_btn_${idx.count }">
											<span class="glyphicon glyphicon-trash"></span> Delete
										</button>
									</c:if>
									<c:if test="${not Is_personal }">
										<button type="button" class="btn btn-default btn-xs operation-btn"  id="img_details_btn_${idx.count }"
											onclick="window.location.href=''" disabled>
											<span class="	glyphicon glyphicon-eye-open"></span> Details
										</button>
									</c:if>
								</div>
							</div>

							<script>
								image_item_json = '${image_item}';
								idx = ${idx.count};
								image_item_obj = JSON.parse(image_item_json);
								document
										.getElementById("image_item_img_" + idx).src = image_item_obj.Addr;
								document
										.getElementById("image_item_img_" + idx).alt = image_item_obj.Filename;
								document.getElementById("image_item_text_"
										+ idx).innerHTML = image_item_obj.Filename;
								//Basic query picture address, which is for ipictures in personal album 
								document.getElementById("image_item_frame_" + idx).href = 
									"/Souvenirs/picture?album_name="+encodeURIComponent(image_item_obj.AlbumName)+"&picture_name="+encodeURIComponent(image_item_obj.Filename);
								//Extended attribute user_id for pictures in shared album
								<c:if test="${not Is_personal}">
									document.getElementById("image_item_frame_" + idx).href += "&user_id="+encodeURIComponent(image_item_obj.UserID)+"&group_id="+encodeURIComponent("${Group_id}");
								</c:if>
								//Set URL to related element
								document.getElementById("image_item_text_" + idx).href = document.getElementById("image_item_frame_" + idx).href;
								<c:if test="${Is_personal}">
									document.getElementById("img_edit_btn_" + idx).setAttribute("onclick","window.location.href='"+document.getElementById("image_item_frame_" + idx).href+"'");
								</c:if>
								<c:if test="${not Is_personal}">
									document.getElementById("img_details_btn_" + idx).setAttribute("onclick","window.location.href='"+document.getElementById("image_item_frame_" + idx).href+"'");
								</c:if>
								
							</script>
						</c:forEach>

					</div>
				</div>
				<div class="col-sm-3" id="album_info">
					<div class="album-cover">
						<img id="image_item_img_${idx.count }" src="${Album_cover }" alt="" width="120" height="120">
						<button type="button" class="btn btn-default btn-sm" style="margin: 5px auto; width: 120px">Change Cover</button>
					</div>
	
					<!-- Display album name -->
					<div class="form-group">
						<label for="name" style="display:block">Album Name
						<div class="btn-group btn-group-xs" style="float:right">
   							 <button type="button" class="btn btn-default" disabled>Save</button>
  							  <button type="button" class="btn btn-default" onclick="document.getElementById('album_name').value=original_album_name">Reset</button>
						</div></label>
						<input type="text" class="form-control" id="album_name" placeholder="" value="${Album_name }">
					</div>
	
					<!-- Display owner -->
					<div class="form-group">
						<label for="name">Owner</label>
						<input type="text" class="form-control" id="owner" placeholder="" value="${Owner_name }" disabled>
					</div>
					
					<!-- Display description(field name "intro" in DB) -->
					<div class="form-group">
						<label for="name" style="display:block">Description
						<div class="btn-group btn-group-xs" style="float:right">
   							 <button type="button" class="btn btn-default" disabled>Save</button>
  							  <button type="button" class="btn btn-default" onclick="document.getElementById('description').value=original_description">Reset</button>
						</div></label>
						<textarea id="description" class="form-control" rows="3" >${Description }</textarea>
					</div>		
					<c:if test="${Is_personal }">
						<a href="/Souvenirs/upload?album_name=${Album_name }"><button type="button" class="btn btn-default">Upload Image</button></a>
					</c:if>	
					<c:if test="${not Is_personal }">
						<button type="button" class="btn btn-default">Share My Picture</button>		
					</c:if>	
				</div>
			</div>
			<div class="row">...</div>
		</div>

	</div>
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>