<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- homepage页面，是用户的个人主页，这个页面上有少许的展示和较多的指向基本操作的链接 -->
<!-- 在页面上完成的操作：显示自己的相册；显示别人分享的相册；创建一个新相册；用户注销 -->
<!-- 可以跳转的链接：用户主页；上传照片；制作Souvenir；显示帮助；个人相册管理；共享相册管理；个人信息维护； 进入小组操作面板-->

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
<!-- <link href="/Souvenirs/res/css/homepage.css" rel="stylesheet"
	type="text/css"> -->
<title>Homepage</title>
<script type="text/javascript">
	window.onload = function() {
		//display_width is the width(px) of active and available for showing contnet window 
		var display_width = window.innerWidth
				|| document.documentElement.clientWidth
				|| document.body.clientWidth;
		var mainbody_width = display_width * 0.8 - 50 * 2;
		var template_width = Math.round(mainbody_width / 5 - 28) - 2;
		var template_height = template_width * 4 / 3;
		for (i = 1; i <= 5; i++) {
			document.getElementById("template" + i).width = template_width;
			document.getElementById("template" + i).height = template_height;
		}

	}
</script>
<style type="text/css">
.album-manage {
	margin-right: 0px;
	padding-left: 10px;
	padding-right: 0%;
	padding-bottom:20px;
	width: 100%;
	border: solid;
	border-width: 2px;
	border-radius: 10px;
	border-color: #99ccff;
}

div.album-manage-title {
	background-color: #99ccff;
	margin-left: -10px;
	border: solid;
	border-width: 2px;
	border: solid;
	border-width: 2px;
	border-color: #E8FFFF;
}

.album-manage h4 {
	padding-left: 10px;
}

.manage-content {
	border-left-style: solid;
	border-width: 1px;
	text-align: center;
	padding-top:10px;
}

.small-grid {
	padding-left: 0px;
	padding-right: 5px;
}

.page-number {
	background-color: rgba(30, 144, 255, 0.8);
	color: white;
	padding: 5px;
	padding-left: 10px;
	padding-right: 10px;
	border-radius: 5px;
}

.select-template {
	text-align: center;
}

.select-template .templates {
	margin-left: 50px;
	margin-right: 50px;
	margin-top: 20px;
}
</style>
</head>

<body>
	<!-- mainbody is the content part except footer of website infomation -->
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

		<div class="select-template">
			<h3>Select a template to start making souvenir</h3>
			<div class="templates">
				<div class="img">
					<a target="_self" href="/Souvenirs/making?template=template1"><img id="template1"
						src="/Souvenirs/res/image/template/aaa.jpg" alt="template1" width="120" height="160"></a>
					<div class="desc">
						<a target="_blank" href="#">Template1</a>
					</div>
				</div>

				<div class="img">
					<a target="_self" href="/Souvenirs/making?template=template2"><img id="template2"
						src="/Souvenirs/res/image/template/bbb.jpg" alt="template2" width="120" height="160"></a>
					<div class="desc">
						<a target="_blank" href="#">Template2</a>
					</div>
				</div>

				<div class="img">
					<a target="_self" href="#"><img id="template3" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
						height="160"></a>
					<div class="desc">
						<a target="_blank" href="#">Template3</a>
					</div>
				</div>

				<div class="img">
					<a target="_self" href="#"><img id="template4" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
						height="160"></a>
					<div class="desc">
						<a target="_blank" href="#">Template4</a>
					</div>
				</div>

				<div class="img">
					<a target="_self" href="#"><img id="template5" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
						height="160"></a>
					<div class="desc">
						<a target="_blank" href="#">Template5</a>
					</div>
				</div>
			</div>
		</div>

		<div style="clear: both; padding-top: 20px;">
			<div class="album-manage">
				<div class="album-manage-title">
					<h4>Available Albums</h4>
				</div>

				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">

						<ul id="myTab" class="nav nav-tabs nav-stacked">
							<li><a href="#my_albums" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">My Albums</a></li>
							<li><a href="#shared_albums" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">Shared Albums</a></li>
							<li><a href="#create_album" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">Create Album</a></li>
						</ul>
					</div>

					<div class=" col-lg-10 col-md-10 col-sm-10 col-xs-10 manage-content">
						<form>
							<div id="TabContent" class="tab-content">
								<div class="tab-pane fade" id="my_albums">

									<c:forEach var="pAlbum_item" items="${PAlbum_json_list }" varStatus="idx">

										<div class="img">
											<a id="pAlbum_item_frame_${idx.count }" target="_self" href="#"> <img id="pAlbum_item_img_${idx.count }" src=""
												alt="" width="120" height="120"></a>
											<div class="desc">
												<a id="pAlbum_item_text_${idx.count }" target="_self" href="#"></a>
											</div>
										</div>

										<script>
											pAlbum_item_json = '${pAlbum_item}';
											idx = ${	idx.count};
											pAlbum_item_obj = JSON.parse(pAlbum_item_json);
											document.getElementById("pAlbum_item_img_" + idx).src = pAlbum_item_obj.cover_addr;
											document.getElementById("pAlbum_item_img_" + idx).alt = pAlbum_item_obj.album_name;
											document.getElementById("pAlbum_item_text_" + idx).innerHTML = pAlbum_item_obj.album_name;
											document.getElementById("pAlbum_item_frame_" + idx).href = "/Souvenirs/album?album_name="+pAlbum_item_obj.album_name;
											document.getElementById("pAlbum_item_text_" + idx).href = "/Souvenirs/album?album_name="+pAlbum_item_obj.album_name;
										</script>
									</c:forEach>

								</div>

								<div class="tab-pane fade" id="shared_albums">
								
									<c:forEach var="sAlbum_item" items="${SAlbum_json_list }" varStatus="idx">

										<div class="img">
											<a id="sAlbum_item_frame_${idx.count }" target="_blank" href="#"> <img id="sAlbum_item_img_${idx.count }" src=""
												alt="" width="120" height="120"></a>
											<div class="desc">
												<a id="sAlbum_item_text_${idx.count }" target="_blank" href="#" style="width:120px;"></a>
											</div>
										</div>

										<script>
											sAlbum_item_json = '${sAlbum_item}';
											idx = ${	idx.count};
											sAlbum_item_obj = JSON.parse(sAlbum_item_json);
											document.getElementById("sAlbum_item_img_" + idx).src = sAlbum_item_obj.cover_addr;
											document.getElementById("sAlbum_item_img_" + idx).alt = sAlbum_item_obj.album_name;
											document.getElementById("sAlbum_item_text_" + idx).innerHTML = sAlbum_item_obj.album_name;
										</script>
									</c:forEach>
								
								</div>

								<div class="tab-pane fade" id="create_album">
									<div class="row">

										<div class="col-sm-5">
											<div class="form-group">
												<label for="firstname" class="col-sm-4 control-label small-grid">Album Name</label>
												<div class="col-sm-8 small-grid">
													<input type="text" class="form-control" id="firstname" placeholder="Input album name">
												</div>
											</div>
										</div>

										<div class="col-sm-4">
											<div class="form-group">
												<label for="firstname" class="col-sm-3 control-label small-grid">Cover</label>
												<div class="col-sm-9 small-grid">
													<input type="text" class="form-control" id="firstname" placeholder="Choose cover from local files">
												</div>
											</div>
										</div>

										<div class="col-sm-3 small-grid" style="text-align: left">
											<button class="btn btn-default btn-sm" type="submit">Choose Cover</button>
											<button class="btn btn-default btn-sm" type="submit">Upload</button>
										</div>

									</div>

									<br />
									<div class="row">
										<div class="col-sm-5">
											<div class="form-group">
												<label for="firstname" class="col-sm-4 control-label small-grid">Description</label>
												<div class="col-sm-8 small-grid">
													<textarea class="form-control" rows="3" placeholder="Enter description, no more than 200 letters"></textarea>
												</div>
											</div>
										</div>

										<div class="col-sm-4">
											<div class="form-group">
												<label for="firstname" class="col-sm-3 control-label small-grid">Cover Preview</label>
												<div class="col-sm-9 small-grid">
													<img class="img-rounded manage-content" src="/Souvenirs/image" width="120" height="120">
												</div>
											</div>
										</div>
									</div>

									<br />
									<div class="row">
										<div class="col-sm-2">
											<button type="submit" class="btn btn-primary">Create</button>
										</div>
									</div>

								</div>
								<script>
									$(function() {
										$('#myTab li:eq(0) a').tab('show');
									});
								</script>

							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>