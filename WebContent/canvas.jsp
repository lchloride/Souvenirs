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
	var image_json = '${empty Image_JSON?"[]":Image_JSON}';
	var souvenir_json = '[{"background": "BBB.jpg","originW": 1960,"originH": 2240},'
			+ '{"type": "image","url": "/Souvenirs/res/image/default_avatar.png","startX": 107,"startY": 252,"drawW": 1092,"drawH": 658,"zoom": 0,'
			+ '"moveX": 0,"moveY": 0,"t11": 1,"t12": 0,"t13": 0,"t21": 0,"t22": 1,"t23": 0, "clipShape":"rect", "clipPara": [653,581,329]},'
			+ '{"type": "text","text": "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ你我他她它あいうえお1234567890`-=[];\'", "startX": 1204,"startY": 337,"maxW": 531,"maxH":263, "style": "arial","size": 16,'
			+ '"color": "black","bold": false,"italic": false,"paddingL": 96, "paddingR":87, "paddingT":51, "paddingB":44, "lineH":1.5, "clipShape":"rect"}]';
	var souvenir_obj = JSON.parse(souvenir_json);
	var ratio = 1;//This is the ratio of canvas's real height to original background height. 
	var onshowContentId = "hint";//Indicate which operation content is activated and shown.

	window.onload = function() {
		//set height and width of canvas that fits the displaying area
		var c = document.getElementById("myCanvas");
		c.width = display_height
				* (souvenir_obj[0].originW / souvenir_obj[0].originH);
		c.height = display_height;
		ratio = c.height / souvenir_obj[0].originH;

		document.getElementById("div_canvas").style.width = c.width.toString()
				+ "px";
		document.getElementById("div_oper").style.width = display_width * 0.8
				- (20 * 3) - c.width - 10 + "px";//0.8 is the width of mainbody,20 is margin of rach column, 10 is the modification of style
		document.getElementById("div_oper").style.left = String(Math
				.floor(display_width * 0.1 + c.width + 20))
				+ 'px';
		document.getElementById("img_content").style.width = display_width
				* 0.8 - (20 * 3) - c.width - 10 + "px";
		document.getElementById("img_content").style.height = display_height
				- (41 + 74 + 39) + "px";//41 is the height of <h4>, 74 is the height of form and 39 is the height of button
				
		assignImage();
		drawSouvenir();
		drawBorderRect();
	}

	//This function is the main function of drawing which will call the spcific sub-function to draw shapes
	function drawSouvenir() {
		var c = document.getElementById("myCanvas");
		var ctx = c.getContext("2d");

		bg = new Image();
		bg.src = "res/image/" + souvenir_obj[0].background;
		bg.onload = function() {
			ctx.drawImage(bg, 0, 0, c.width, c.height);
			drawClip(ctx);
			drawContent(ctx, 1);
		}
	}

	function drawClip(ctx) {
		ctx.strokeStyle = "rgba(255,255,255,0)";
		for (i = 1; i < souvenir_obj.length; i++) {
			if (souvenir_obj[i].type == "image") {
				if (souvenir_obj[i].clipShape == "rect")
					createRectClip(ctx, R(souvenir_obj[i].startX),
							R(souvenir_obj[i].startY),
							R(souvenir_obj[i].drawW), R(souvenir_obj[i].drawH));
				else if (souvenir_obj[i].clipShape == "circle")
					createCircleClip(ctx, R(souvenir_obj[i].clipPara[0]),
							R(souvenir_obj[i].clipPara[1]),
							R(souvenir_obj[i].clipPara[2]));
				else
					;
			} else if (souvenir_obj[i].type == "text") {
				if (souvenir_obj[i].clipShape == "rect")
					createRectClip(
							ctx,
							R(souvenir_obj[i].startX + souvenir_obj[i].paddingL),
							R(souvenir_obj[i].startY + souvenir_obj[i].paddingT)
									- souvenir_obj[i].size / 2,
							R(souvenir_obj[i].maxW), R(souvenir_obj[i].maxH)
									+ souvenir_obj[i].size);
				else
					;
			} else {
				alert("There are something wrong with this templete!\n"
						+ "Sorry for the imconvience, please give us a feedback about this issue and try another templete.\n"
						+ "Thank you!");
				return;
			}
		}
		ctx.clip();
	}
	//This function is to select proper sub-function of drawing
	function drawContent(ctx, idx) {
		if (souvenir_obj[idx] == undefined || souvenir_obj[idx] == null
				|| souvenir_obj[idx].type == undefined
				|| souvenir_obj[idx].type == null)
			return;
		else {
			if (souvenir_obj[idx].type == "image")
				drawImg(ctx, idx);
			else if (souvenir_obj[idx].type == "text")
				drawText(ctx, idx);
			else {
				alert("There are something wrong with this templete!\n"
						+ "Sorry for the imconvience, please give us a feedback about this issue and try another templete.\n"
						+ "Thank you!");
				return;
			}
		}
	}

	//Draw an assigned image
	function drawImg(ctx, idx) {
		//圆形裁剪区
		//createCircleClip(ctx)
		//星形裁剪区
		//create5StarClip(ctx);
		image = new Image();
		image.onload = function() {
			//drawImg(ctx, image);
			var d = souvenir_obj[idx].zoom;
			var moveX = souvenir_obj[idx].moveX;
			var moveY = souvenir_obj[idx].moveY;
			ctx.transform(souvenir_obj[idx].t11, souvenir_obj[idx].t12,
					souvenir_obj[idx].t21, souvenir_obj[idx].t22,
					souvenir_obj[idx].t13, souvenir_obj[idx].t23);

			ctx.drawImage(image, 0 + d - moveX, 0 + d - moveY, image.width - 2
					* d - moveX, image.height - 2 * d - moveY,
					R(souvenir_obj[idx].startX), R(souvenir_obj[idx].startY),
					R(souvenir_obj[idx].drawW), R(souvenir_obj[idx].drawH));
			drawContent(ctx, idx + 1);
		}
		image.src = souvenir_obj[idx].url;
	}

	//Draw text
	function drawText(ctx, idx) {
		var font_style = '';
		if (souvenir_obj[idx].italic)
			font_style += "italic ";
		if (souvenir_obj[idx].bold)
			font_style += "bold ";
		font_style += souvenir_obj[idx].size + 'px "' + souvenir_obj[idx].style
				+ '" ';
		ctx.font = font_style;
		ctx.fillStyle = souvenir_obj[idx].color;
		/* ctx.fillText(souvenir_obj[idx].text, R(souvenir_obj[idx].startX+souvenir_obj[idx].paddingL),
				R(souvenir_obj[idx].startY+souvenir_obj[idx].paddingT+souvenir_obj[idx].size), R(souvenir_obj[idx].maxW)); */
		draw_long_text(souvenir_obj[idx].text, ctx, R(souvenir_obj[idx].startX
				+ souvenir_obj[idx].paddingL), R(souvenir_obj[idx].startY
				+ souvenir_obj[idx].paddingT + souvenir_obj[idx].size),
				R(souvenir_obj[idx].maxW), souvenir_obj[idx].size,
				souvenir_obj[idx].lineH);
		drawContent(ctx, idx + 1);
	}

	//Auxiliary function of auto-wrap, without considering of vertical out-of-range condition
	function draw_long_text(longtext, ctx, startX, startY, max_width, size,
			line_height) {
		var line_text = new String();
		var lineno = 1;
		for (i = 0; i < longtext.length; i++) {
			if (ctx.measureText(line_text).width <= max_width)
				line_text += longtext[i];
			else {
				ctx.fillText(line_text, startX, startY + (lineno - 1) * size
						* line_height, max_width);
				line_text = new String();
				lineno++;
			}
		}
		ctx.fillText(line_text, startX, startY + (lineno - 1) * size
				* line_height, max_width);
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
		//context.clip();
	}

	function createCircleClip(ctx, stX, stY, radius) {
		ctx.beginPath();
		ctx.arc(stX, stY, radius, 0, Math.PI * 2, true);
		ctx.closePath();
		//ctx.clip();
	}

	function createRectClip(ctx, stX, stY, width, height) {
		ctx.rect(stX, stY, width, height);
		ctx.stroke();
	}

	function drawBorderRect() {
		var div_id = document.getElementById("border_rect");
		var div_str = new String();
		for (i = 1; i < souvenir_obj.length; i++) {
			if (souvenir_obj[i].type == "image")
				div_str += '<a><div class="border-rect" style="left:'
						+ (display_width * 0.1 + 20 + R(souvenir_obj[i].startX) + 1)
						+ 'px;top:' + (70 + R(souvenir_obj[i].startY) + 2)
						+ 'px;width:' + R(souvenir_obj[i].drawW) + 'px;height:'
						+ (R(souvenir_obj[i].drawH) + 2) + 'px" onclick="changeOperContent(\'choose_album\')"></div></a>';
			else if (souvenir_obj[i].type == "text")
				div_str += '<a><div class="border-rect" style="left:'
						+ (display_width * 0.1 + 20 + R(souvenir_obj[i].startX) + 1)
						+ 'px;top:'
						+ (70 + R(souvenir_obj[i].startY) + 2)
						+ 'px;width:'
						+ R(souvenir_obj[i].maxW + souvenir_obj[i].paddingL
								+ souvenir_obj[i].paddingR)
						+ 'px;height:'
						+ (R(souvenir_obj[i].maxH + souvenir_obj[i].paddingT
								+ souvenir_obj[i].paddingB) + 2) + 'px" onclick="changeOperContent(\'hint\')"></div></a>';
		}
		div_id.innerHTML = div_str;
	}
	function selectImage(idx) {
		document.getElementById("selected_img_" + selected_image).className = "img";
		selected_image = idx;
		document.getElementById("selected_img_" + selected_image).className = "img-clicked";
	}

	function R(val) {
		return ratio * val;
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
	
	function changeOperContent(new_content_id) {
		document.getElementById(onshowContentId).style.display = "none";
		onshowContentId = new_content_id;
		document.getElementById(onshowContentId).style.display = "block";
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

div.border-rect {
	position: absolute;
	border-style: solid;
	border-color: #0000ff;
	border-width: 2px;
	/*following 4 items will be overwriten in js script*/
	left: 0;
	top: 0;
	width: 0;
	height: 0;
}

#border_rect a :hover {
	border-color:rgba(255,0,0,0.5);
	border-width:4px;
	
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
			<div id="border_rect"></div>
		</div>

		<form class="from" role="form" action="making">
			<input type="hidden" value="" id="query_type" />
			<div class="oper-content" id="div_oper">
				<!-- The following content is the one of display hint page -->
				<div id="hint">
					<h4 style="text-align: center; margin-top: 45%;">Click on the
						rectangle area in the preview image to modify its content.</h4>
				</div>

				<!-- The following content is the one of selecting a picture from album -->
				<div id="choose_album" style="display: none;">
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