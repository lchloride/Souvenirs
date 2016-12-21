<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Making Souvenirs</title>
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
<script>
	var display_width = window.innerWidth
			|| document.documentElement.clientWidth
			|| document.body.clientWidth;

	var display_height = window.innerHeight
			|| document.documentElement.clientHeight
			|| document.body.clientHeight;
	display_height = display_height - 155;
	var selected_image = 0;
	var image_json = '${Image_JSON}';

	window.onload = function() {
		//set height and width of canvas that fits the displaying area
		var c = document.getElementById("myCanvas");
		c.width = display_height / 8 * 7;
		c.height = display_height;
		document.getElementById("div_canvas").style.width = c.width.toString()
				+ "px";
		document.getElementById("div_oper").style.width = display_width * 0.8
				- (20 * 3) - c.width - 10 + "px";
		document.getElementById("div_oper").style.left = String(Math
				.floor(display_width * 0.1 + c.width + 20))
				+ 'px';
		document.getElementById("img_content").style.width = display_width
				* 0.8 - (20 * 3) - c.width - 10 + "px";
		document.getElementById("img_content").style.height = display_height
				- (41 + 74 + 39) + "px";
		assignImage();

		var ctx = c.getContext("2d");
		ctx.fillStyle = "white";
		ctx.fillRect(0, 0, 400, 300);

		bg = new Image();
		bg.src = "res/image/BBB.jpg"
		bg.onload = function() {
			ctx.drawImage(bg, 0, 0, c.width, c.height);
			image = new Image();
			image.onload = function() {
				//drawImg(ctx, image);
				var d = 30;
				ctx.drawImage(image, 0 + d, 0 + d, image.width - 2 * d,
						image.height - 2 * d, 0, 0, 240, 240);
			}
			image.src = "res/image/default_avatar.png";
		}

		/* 		var c2 = document.getElementById("myCanvas1");
		 var ctx2 = c.getContext("2d");
		 ctx2.fillStyle = "#FFFF00";
		 ctx2.fillRect(50, 25, 150, 75); */
	}

	function drawImg(context, image) {
		//圆形裁剪区
		//createCircleClip(context)
		//星形裁剪区
		create5StarClip(context);
		context.fillStyle = "black";
		context.fillRect(0, 0, 400, 300);
		context.scale(1, 2);
		context.drawImage(image, 50, 0);

	}

	function create5StarClip(context) {
		var n = 0;
		var dx = 200;
		var dy = 135;
		var s = 150;
		context.beginPath();
		var x = Math.sin(0);
		var y = Math.cos(0);
		var dig = Math.PI / 5 * 4;
		for (var i = 0; i < 5; i++) {
			var x = Math.sin(i * dig);
			var y = Math.cos(i * dig);
			context.lineTo(dx + x * s, dy + y * s);
		}
		context.closePath();
		context.clip();
	}

	function selectImage(idx) {
		document.getElementById("selected_img_" + selected_image).className = "img";
		selected_image = idx;
		document.getElementById("selected_img_" + selected_image).className = "img-clicked";
	}

	function queryImageInAlbum(str) {
		var xmlhttp;
		var album = document.getElementById("select_album_name").value;
		if (str == "") {
			image_json = "[]";
			return;
		}
		if (window.XMLHttpRequest) {
			// IE7+, Firefox, Chrome, Opera, Safari 浏览器执行代码
			xmlhttp = new XMLHttpRequest();
		} else {
			// IE6, IE5 浏览器执行代码
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				image_json = xmlhttp.responseText;
			}
			assignImage();
		}
		xmlhttp.open("GET", "AlbumAjax?album_name=" + album, true);
		xmlhttp.send();
	}

	function assignImage() {
		var obj = JSON.parse(image_json);
		var img_content = new String();
		if (obj.length == 0)
			img_content = "<h4>There is no picture in the album.</h4>";
		for (i = 0; i < obj.length; i++) {
			img_content += '<div class="img" id="selected_img_'+i+'"><a onclick="selectImage('
					+ i
					+ ')"><img src="'+
			 obj[i].Addr+'" alt="Image In Album" width="120" height="120"></a><div class="desc" ><a style="color:black">'
					+ obj[i].Filename + '</a></div></div>';
		}
		document.getElementById("img_content").innerHTML = img_content;
	}
</script>

<style type="text/css">
.oper-content {
	margin-left: 20px;
	position: absolute;
	top: 70px;
	margin-right: 20px;
}

#div_canvas {
	margin-left: 20px;
}

div.img {
	margin: 5px;
	padding: 3px;
	border: 2px solid #ffffff;
	border-radius: 5px;
	height: auto;
	width: auto;
	float: left;
	text-align: center;
	height: auto;
	border: 2px solid #ffffff;
}

div.img-clicked {
	margin: 5px;
	padding: 3px;
	border: 2px solid #ffffff;
	border-radius: 5px;
	height: auto;
	width: auto;
	float: left;
	text-align: center;
	height: auto;
	border: 2px solid #0000ff;
}

div.img img {
	display: inline;
	margin: 3px;
	border: 1px solid #ffffff;
}

div.img-clicked img {
	display: inline;
	margin: 3px;
	border: 1px solid #ffffff;
}

div.img:hover {
	background-color: rgba(232, 232, 232, .8);
	border: 2px solid #cccccc;
}

div.desc {
	text-align: center;
	font-weight: normal;
	width: 120px;
	margin: 2px;
	margin-top: 5px;
	white-space: nowrap;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

div.desc:hover {
	text-overflow: inherit;
	overflow: visible;
}

div.desc a:link {
	color: #000000;
} /* 未访问链接*/
div.desc a:visited {
	color: #000000;
} /* 已访问链接 */
div.desc a:hover {
	color: #000000;
} /* 鼠标移动到链接上 */
div.desc a:active {
	color: #000000;
} /* 鼠标点击时 */
div.img-content {
	/*position: absolute;
	left: 0px;
	top: 70px; */
	overflow: hidden;
	overflow-y: scroll;
	height: 300px;
}
</style>
</head>
<body>
	<div class="mainbody" style="padding-left: 0px;">
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
					<li><a href="#">Upload</a></li>
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

		<div id="div_canvas">
			<canvas id="myCanvas" width="400" height="300"
				style="border:1px solid #c3c3c3;"> Sorry, your browser does
			not support HTML5 canvas tag, we suggest using Chrome or Firefox to
			achieve best experience. </canvas>
			<div id="drawing_content"></div>
		</div>

		<form class="from" role="form" action="making">
			<input type="hidden" value="" id="query_type" />
			<div class="oper-content" id="div_oper">
				<div id="choose_album">
					<h4>Select Pictures from Album</h4>

					<div class="form-group">
						<label for="name">Album</label> <select class="form-control"
							name="Select_album_name" onchange="queryImageInAlbum()"
							id="select_album_name">
							<c:forEach var="album_name" items="${Album_List}">
								<option>${album_name }</option>
							</c:forEach>
						</select>
					</div>


					<!-- Choose a picture for the rect -->
					<div class="img-content" id="img_content">


						<%-- 					<c:forEach var="image_addr" items="${Image_Addr }" varStatus="status">
						<div class="img" id="selected_img_${status.index}">
							<a href="#" onclick="selectImage(${status.index})"><img
								src="${image_addr}" alt="Image In Album"
								width="120" height="120"></a>
							<div class="desc">
								<a target="_blank" href="#">My Album${status.index }</a>
							</div>
						</div>
					</c:forEach> --%>
					</div>

					<!-- Modify position and Zoom -->
					<div></div>

					<!-- Add button -->
					<div style="margin-top: 10px;">
						<button class="btn-sm btn-primary" id="add_pic_btn" type="button">Add
							Selected Image</button>
					</div>
				</div>
			</div>
		</form>
	</div>

	<div class="footer">Copyright &copy; 2016 Souvenirs, All Rights
		Reserved.</div>
</body>
</html>