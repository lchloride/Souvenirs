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
		var r = (display_width >= 1367 ? display_width * 0.8
				: (display_width >= 768 ? display_width * 0.9
						: display_width - 10));
		var mainbody_width = r - 50 * 2;
		var template_width = Math.round(mainbody_width / 5 - 28) - 2;
		var template_height = template_width * 4 / 3;
		for (i = 1; i <= 5; i++) {
			document.getElementById("template" + i).width = template_width;
			document.getElementById("template" + i).height = template_height;
		}

	}
</script>
<style type="text/css">
div.operation-content {
	margin-left:20px;
	margin-right:20px;
}

div.info-part {
	border-style:solid;
	border-width:1px;
	border-color: #ccc;
	border-radius:5px;
	margin-top:10px;
	background-color: hsla(200, 10%, 93%, 1)
}

div.info-part h4.title {
	background-color: hsla(215, 30%, 60%, 1);/*rgba(148, 209, 179, 0.8)*/;
	margin-top: 0px;
    padding-top: 10px;
	border-top: solid;
	border-left: solid;
	border-right: solid;
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
	border-color: #faf0e6;
    border-bottom-style: solid;
    border-bottom-width: 1px;
    border-bottom-color: #ccc;
    padding-left: 10px;
    padding-right: 10px;
    padding-bottom: 2px;
	color: hsla(95, 50%, 95%, 1);
}
div.info-part div.body{
	
}
div.info-part div.body img {
	width:120px;
	height:120px;
	margin-left:5px;
	border-radius:2px;
	border:solid transparent 1px;
}
.divider {
    margin-top: 5px;
    margin-bottom: 5px;
    border-color: black;
    /* margin-right: 50px; */
    width: 70%;
    margin-left: 0px;
    border-top:solid 1px;
}

</style>
</head>
<body>
	<!-- mainbody is the content part except footer of website infomation -->
	<div class="mainbody" style="min-width:1024px;">
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
					<li><a href="/homepage">Previous Version</a></li>
				</ul>
				<form class="navbar-form navbar-left" role="search">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Search your pictures">
					</div>
					<button type="submit" class="btn btn-default " style="font-size: 1.45em; text-shadow: #aaa 1px 2px 3px;">
						<span class="glyphicon glyphicon-search" style="color: #999"></span>
					</button>
				</form>
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
		<!-- Navigate bar END -->
		<div class="operation-content"> 
			<div class="row" >
				<!-- Left column, display personal information -->
				<div class="col-sm-4 narrow-grid" >
					<!-- Personal information block -->
					<div class="info-part" id="personal_info">
						<h4 class="title">Personal Information</h4>
						<div class="body">
							<!-- Bootstrap media template -->
							<div class="media">
							    <a class="pull-left" href="#">
							        <img class="media-object" src="${Avatar }" alt="Avatar" width=120 height=120>
							    </a>
							    <div class="media-body">
							        <h4 class="media-heading">${sessionScope.username}</h4>
							        UID: ${sessionScope.user_id}
							        <div >
							        	<button class="btn btn-default btn-sm"><span class="glyphicon glyphicon-cog"></span> Setting</button>
							        	<button class="btn btn-default btn-sm"><span class="glyphicon glyphicon-log-out"></span> Logout</button>
							        </div>
							    </div>
							</div>
							<!-- Bootstrap media template END -->
						</div>
					</div>
					<!-- Personal information block END -->
					<!-- Latest pictures part -->
					<div class="info-part"  id="lastest_picture">
						<h4 class="title">Lastest Pictures</h4>
						<div class="body">
							<c:forEach var="picture_item" items="${picture_json_list }" varStatus="idx">
								<div class="img">
									<a id="picture_item_frame_${idx.count }" target="_self" href="#"> <img id="picture_item_img_${idx.count }" src=""
										alt="" width="120" height="120"></a>
									<div class="desc">
										<a id="picture_item_text_${idx.count }" target="_self" href="#"></a>
									</div>
								</div>

								<script>
									pAlbum_item_json = '${picture_item}';
									idx = ${	idx.count};
									pAlbum_item_obj = JSON.parse(pAlbum_item_json);
									document.getElementById("picture_item_img_" + idx).src = pAlbum_item_obj.Addr;
									document.getElementById("picture_item_img_" + idx).alt = pAlbum_item_obj.Filename;
									document.getElementById("picture_item_text_" + idx).innerHTML = pAlbum_item_obj.AlbumName+"/"+pAlbum_item_obj.Filename;
									document.getElementById("picture_item_frame_" + idx).href = "/Souvenirs/picture?album_name="+encodeURIComponent(pAlbum_item_obj.AlbumName)+"&picture_name="+encodeURIComponent(pAlbum_item_obj.Filename);
									document.getElementById("picture_item_text_" + idx).href = document.getElementById("picture_item_frame_" + idx).href;
								</script>
							</c:forEach>
						</div>
						<div style="clear:both"></div>
					</div>
					<!-- Latest pictures part END -->
					<!-- Dashboard part -->
					<div class="info-part" id="dashboard">
						<h4 class="title">Dashboard</h4>
						<div class="body">
							<ul>
								<li>Personal Albums: <span id="palbum_count">${PAlbum_count }</span></li>
								<li>Shared Albums: <span id="salbum_count">${SAlbum_count }</span></li>
								<div class="divider"></div>
								<li>Pictures: <span id="picture_count">${Picture_count }</span></li>
							</ul>
						</div>
					</div>
					<!-- Dashboard part END -->
				</div>
				<!-- Center column, display souvenir templates -->
				<div class="col-sm-6 narrow-grid" >
					<!-- Bootstrap panel -->
					<div class="panel panel-default info-part" style="background-color:hsla(200, 10%, 93%, 1);">
					    <div class="panel-heading" style="background-color:hsla(210, 29%, 92%, 1);">
					        <h3 class="panel-title">Souvenir Templates</h3>
					    </div>
					    <!-- Bootstrap panel body -->
					    <div class="panel-body">
					    	<!-- Souvenirs templates -->
					        <div class="templates">
					        	<!-- A template -->
								<div class="img">
									<a target="_self" href="/Souvenirs/making?template=template1"><img id="template1"
										src="/Souvenirs/res/image/template/aaa.jpg" alt="template1" width="120" height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template1</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="/Souvenirs/making?template=template2"><img id="template2"
										src="/Souvenirs/res/image/template/bbb.jpg" alt="template2" width="120" height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template2</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template3" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template3</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template4" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template4</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template5" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template5</a>
									</div>
								</div>
							</div>
							<!-- Souvenirs templates END -->
					    </div>
					    <!-- Bootstrap panel body END -->
					</div>
				</div>
				<!-- Right column, display available albums -->
				<div class="col-sm-2 narrow-grid">
					<div class="info-part" id="albums_info">
						<h4 class="title" style="background-color:hsla(200, 10%, 93%, 1);color:black;">Albums</h4>
						<div class="body">
							<!--  -->
							<ul id="myTab" class="nav nav-tabs">
								<li class="active">
									<a href="#personal" data-toggle="tab" style="padding:6px 10px">
										 Personal
									</a>
								</li>
								<li><a href="#shared" data-toggle="tab" style="padding:6px 10px">Shared</a></li>
							</ul>
							<!--  -->
							<div id="myTabContent" class="tab-content">
								<div class="tab-pane fade in active" id="personal">
									
								</div>
								<div class="tab-pane fade" id="shared">
									
								</div>
							</div>
							
						</div>
					</div>
				</div>
				<!-- Right column, display available albums END-->
			</div>
		</div>
	</div>


	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>