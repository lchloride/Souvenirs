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
    <!-- load jquery ui css theme -->
    <link type="text/css" href="/Souvenirs/res/css/sDashboard/jquery-ui.css" rel="stylesheet" />

    <!-- load the sDashboard css -->
    <link href="/Souvenirs/res/css/sDashboard/sDashboard.css" rel="stylesheet">

    <!-- load jquery ui library -->
    <script src="/Souvenirs/res/js/sDashboard/jquery-ui.js" type="text/javascript"></script>

    <!-- load datatables library -->
    <script src="/Souvenirs/res/js/sDashboard/jquery.dataTables.js"></script>

    <!-- load flot charting library -->
    <script src="/Souvenirs/res/js/sDashboard/flotr2.js" type="text/javascript"></script>

    <!-- load sDashboard library -->
    <script src="/Souvenirs/res/js/sDashboard/jquery-sDashboard.js" type="text/javascript"></script>
<title>Homepage</title>
<script type="text/javascript">
	var palbum_count = 0;
	var salbum_count = 0;
	
	window.onload = function() {
		calcSize();
	}
	
	function calcSize() {
		//display_width is the width(px) of active and available for showing contnet window 
		var display_width = window.innerWidth
				|| document.documentElement.clientWidth
				|| document.body.clientWidth;
		var r = (display_width >= 1367 ? display_width * 0.8
				: (display_width >= 768 ? display_width * 0.9
						: display_width - 10));
		var mainbody_width = r - 50 * 2;
		// Calculate size of latest pictures
		var latest_panel_width = $('#latest_picture').width() - 20;
		var image_width_margin = $('div.img').outerWidth(true)-$('div.img a img').width();
		var picture_width = Math.round(latest_panel_width / 2 - image_width_margin)-1;
		var picture_height = picture_width;
		for (i = 1; i<=4; i++) {
			$('#picture_item_img_'+i).css('width', picture_width);
			$('#picture_item_img_'+i).css('height', picture_height);
			$('#picture_item_text_'+i).css('width', picture_width);
		}
		
		// Calculate size of templates
		var templates_width = $('.templates').width();
		var template_width = Math.round(templates_width / 3 - image_width_margin);
		var template_height = template_width * 4 / 3;
		for (i = 1; i <= 8; i++) {
			document.getElementById("template" + i).width = template_width;
			document.getElementById("template" + i).height = template_height;
		}	
		
		// Calculate size of albums list
		var albums_info = Math.max($('#left_col').outerHeight(true), $('#center_col').outerHeight(true));
		var col_margin_top = $('#albums_info').outerHeight(true) - $('#albums_info').height() + 20;
		$('#albums_info').css('height', albums_info-col_margin_top);
		$('#personal_albums_list').css('height', (albums_info - $('h4.title').outerHeight(true) - $('#myTab').outerHeight(true)-$('#create_btn').outerHeight(true)-col_margin_top));
		$('#shared').css('height', (albums_info - $('h4.title').outerHeight(true) - $('#myTab').outerHeight(true)));
		var album_cover_width = $('#personal_albums_list').width() - 15 - image_width_margin;
		$('#personal_albums_list img').css('width', album_cover_width);
		$('#personal_albums_list img').css('height', album_cover_width);

	}
	
	function displayFilename() {
		var filepath = $('#upload_file').val();
		$('#filename_display').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
		
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
	background-color: hsla(210, 32%, 92%, 1);/*rgba(148, 209, 179, 0.8)*/;
	margin-top: 0px;
    padding-top: 10px;
	border-top: solid 1px;
	border-left: solid 2px;
	border-right: solid 2px;
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
	border-color: #faf0e6;
    border-bottom-style: solid;
    border-bottom-width: 1px;
    border-bottom-color: #ccc;
    padding-left: 10px;
    padding-right: 10px;
    padding-bottom: 10px;
	color: black;
	font-size: 16px;
}
div.info-part div.body{
	padding:5px 10px;
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
.padding-top {
	padding-top:10px;
}

div.album-list {
	overflow-y:scroll;
	border-top: solid 1px #999;
	border-bottom: solid 1px #999;
}
</style>
</head>
<body onresize="calcSize()" >
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
			<!-- Image -->
			<div class="row">
				<img alt="123" src="/Souvenirs/res/image/impression.png" style="width:100%;margin-top:-100px;display:block;">
			</div>
			<!-- Information and operation content -->
			<div class="row" >
				<!-- Left column, display personal information -->
				<div class="col-sm-4 col-lg-4"  id="left_col">
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
					<div class="info-part"  id="latest_picture" >
						<h4 class="title">Lastest Pictures</h4>
						<div class="body" >
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
				<!-- Left column END -->
				<!-- Center column, display souvenir templates -->
				<div class="col-sm-6 col-lg-6 narrow-grid" id="center_col">
					<!-- Bootstrap panel -->
					<div class="panel panel-default info-part" style="background-color:hsla(200, 10%, 93%, 1);">
					    <div class="panel-heading" style="background-color:hsla(215, 30%, 60%, 1);color:hsla(95, 50%, 95%, 1);">
					        <h3 class="panel-title" style="font-size:18px;">Souvenir Templates</h3>
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
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template6" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template6</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template7" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template7</a>
									</div>
								</div>
								<!-- A template -->
								<div class="img">
									<a target="_self" href="#"><img id="template8" src="/Souvenirs/res/image/prepare.png" alt="prepare" width="120"
										height="160"></a>
									<div class="desc">
										<a target="_blank" href="#">Template8</a>
									</div>
								</div>
							</div>
							<!-- Souvenirs templates END -->
					    </div>
					    <!-- Bootstrap panel body END -->
					</div>
				</div>
				<!-- Right column, display available albums -->
				<div class="col-sm-2 narrow-grid" id="right_col">
					<div class="info-part" id="albums_info">
						<h4 class="title" style="background-color:hsla(215, 30%, 60%, 1);color:hsla(95, 50%, 95%, 1);">Albums</h4>
						<div class="body" style="padding:0px;">
							<!-- Display album panel -->
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
								<!-- Personal albums panel -->
								<div class="tab-pane fade in active" id="personal" >
									<div class="album-list" id="personal_albums_list" style="overflow-y:scroll">
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
												palbum_count = Math.max(idx, palbum_count);
												pAlbum_item_obj = JSON.parse(pAlbum_item_json);
												document.getElementById("pAlbum_item_img_" + idx).src = pAlbum_item_obj.cover_addr;
												document.getElementById("pAlbum_item_img_" + idx).alt = pAlbum_item_obj.album_name;
												document.getElementById("pAlbum_item_text_" + idx).innerHTML = pAlbum_item_obj.album_name;
												document.getElementById("pAlbum_item_frame_" + idx).href = "/Souvenirs/album?album_name="+pAlbum_item_obj.album_name;
												document.getElementById("pAlbum_item_text_" + idx).href = "/Souvenirs/album?album_name="+pAlbum_item_obj.album_name;
											</script>
										</c:forEach>
									</div>
									<button class="btn btn-primary btn-block" id="create_btn" data-toggle="modal" data-target="#myModal" 
											style="margin-top:5px;border-top-left-radius:0px;border-top-right-radius:0px;">Create Album</button>
								</div>
								<!-- Personal albums panel END -->
								<!-- Shared albums panel  -->
								<div class="tab-pane fade" id="shared" style="overflow-y:scroll">
							
									<c:forEach var="sAlbum_item" items="${SAlbum_json_list }" varStatus="idx">

										<div class="img">
											<a id="sAlbum_item_frame_${idx.count }" target="_self" href="#"> <img id="sAlbum_item_img_${idx.count }" src=""
												alt="" width="120" height="120"></a>
											<div class="desc">
												<a id="sAlbum_item_text_${idx.count }" target="_self" href="#" style="width:120px;"></a>
											</div>
										</div>

										<script>
											sAlbum_item_json = '${sAlbum_item}';
											idx = ${	idx.count};
											salbum_count = Math.max(salbum_count, idx);
											sAlbum_item_obj = JSON.parse(sAlbum_item_json);
											document.getElementById("sAlbum_item_img_" + idx).src = sAlbum_item_obj.cover_addr;
											document.getElementById("sAlbum_item_img_" + idx).alt = sAlbum_item_obj.album_name;
											document.getElementById("sAlbum_item_text_" + idx).innerHTML = sAlbum_item_obj.album_name;
											document.getElementById("sAlbum_item_frame_" + idx).href = "/Souvenirs/sharedAlbum?group_id="+sAlbum_item_obj.group_id;
											document.getElementById("sAlbum_item_text_" + idx).href = document.getElementById("sAlbum_item_frame_" + idx).href;
										</script>
									</c:forEach>
												
								</div>
								<!-- Shared albums panel END -->
							</div>
							<!-- Display album panel END -->
							
						</div>
					</div>
				</div>
				<!-- Right column, display available albums END-->
			</div>
		</div>
	</div>

	<!-- Footer copyright information -->
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
	<!-- Footer copyright information END -->
	
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                <h4 class="modal-title" id="myModalLabel">Create Album</h4>
	            </div>
	            <!--  -->
	            <div class="modal-body">
					
					<div class="form-group padding-top">
						<label for="firstname" class="col-sm-3 control-label narrow-grid">Album Name</label>
						<div class="col-sm-9 narrow-grid">
							<input type="text" class="form-control" id="firstname" placeholder="Input album name">
						</div>
					</div>
					<div style="clear:both;"></div>
					<div class="form-group padding-top">
						<label for="firstname" class="col-sm-3 control-label narrow-grid">Description</label>
						<div class="col-sm-9 narrow-grid">
							<textarea class="form-control" rows="3" placeholder="Enter description, no more than 200 letters"></textarea>
						</div>
					</div>
					<div style="clear:both;"></div>
					<div class="form-group padding-top">
						<label for="firstname" class="col-sm-3 control-label narrow-grid">Cover</label>
						<div class="col-sm-9 narrow-grid">
							<input type="text" class="form-control" id="filename_display" style="width:auto;display:inline;" disabled>
							<label class="btn btn-sm btn-default " for="upload_file">Choose
								Local Image</label>
							<input type="file" name="upload_file" id="upload_file" style="position: absolute; clip: rect(0, 0, 0, 0); display: inline"
									onchange="displayFilename()" accept="image/jpg">
							 
						</div>
					</div>

					<div style="clear:both;"></div>
				</div>
				<!--  -->
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                <button type="button" class="btn btn-primary">提交更改</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>
</body>
</html>