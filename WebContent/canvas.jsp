<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*,java.util.*,java.awt.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- canvas页面主要是完成souvenir的绘制与下载，页面的布局会随着souvenir模板的大小以及窗口的大小进行调整 -->
<!-- 绘制souvenir使用了多段函数递归调用的方法；操作面板的显示与隐藏使用了css的display属性 -->
<!-- 生成图片化的Souvenir的方法是使用canvas的API生成png格式下的base64编码过的图片，
然后将图片上传至服务器，服务器解码后转成二进制png文件发回前台，浏览器接收发回的图片并下载到本地 -->


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Making Souvenir</title>
<link href="/Souvenirs/res/image/logo.ico" rel="icon">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<!-- Text Color Picker -->
<script src="/Souvenirs/res/js/iColorPicker.js" type="text/javascript"></script>
<script src="/Souvenirs/res/js/jquery.bootstrap-growl.min.js"></script>

<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<script src="/Souvenirs/res/js/ajax.js"></script>
<script src="/Souvenirs/res/js/canvas.js"></script>
<link href="/Souvenirs/res/css/canvas.css" rel="stylesheet" type="text/css">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script>
	/*Declarion of useful global variants  */
	//display_width is the width(unit: px) of active and available for showing contnet window 
	var display_width = window.innerWidth
			|| document.documentElement.clientWidth
			|| document.body.clientWidth;
	//display_height is similar to display_width, just stands for the height of available window
	var display_height = window.innerHeight
			|| document.documentElement.clientHeight
			|| document.body.clientHeight;
	//155 stands for the verticle space(unit: px) of  header and footer together with margin and padding 
	display_height = display_height - 155;
	//selected_image is the index number of selected image that user clicks
	var selected_image = 0;
	//image_json is json formatted string storing image info (name and address) in one availble album obtained by Ajax
	//If there is some error with Ajax, it would be assign to empty json string and would not display anything
	var image_json = '${empty Image_JSON?"[]":Image_JSON}';
	//souvenir_json is the json string of souvenir template obtained from server
	//var souvenir_json = '[{"background":"bbb.jpg","originW":1960,"originH":2240},{"type":"image","url":"/Souvenirs/res/image/default_avatar.png","startX":107,"startY":252,"drawW":1092,"drawH":658,"zoom":0,"moveX":0,"moveY":0,"t11":1,"t12":0,"t13":0,"t21":0,"t22":1,"t23":0,"clipShape":"circle","clipPara":[653,581,329]},{"type":"text","text":"abcde","startX":1204,"startY":337,"maxW":531,"maxH":263,"style":"Arial","size":16,"color":"black","bold":false,"italic":false,"paddingL":96,"paddingR":87,"paddingT":51,"paddingB":44,"lineH":1.5,"clipShape":"rect"},{"type":"text","text":"text2","startX":86,"startY":1036,"maxW":387,"maxH":345,"style":"Comic Sans MS","size":16,"color":"blue","bold":true,"italic":false,"paddingL":62,"paddingR":60,"paddingT":100,"paddingB":103,"lineH":1.5,"clipShape":"rect"},{"type":"image","url":"/Souvenirs/res/image/index_bg.jpg","startX":618,"startY":951,"drawW":1144,"drawH":618,"zoom":0,"moveX":0,"moveY":0,"t11":1,"t12":0,"t13":0,"t21":0,"t22":1,"t23":0,"clipShape":"rect"},{"type":"image","url":"/Souvenirs/res/image/bg.jpg","startX":113,"startY":1593,"drawW":1152,"drawH":548,"zoom":0,"moveX":0,"moveY":0,"t11":1,"t12":0,"t13":0,"t21":0,"t22":1,"t23":0,"clipShape":"rect"},{"type":"text","text":"text3","startX":1329,"startY":1606,"maxW":498,"maxH":476,"style":"Times New Roman","size":18,"color":"Green","bold":false,"italic":true,"paddingL":20,"paddingR":18,"paddingT":17,"paddingB":17,"lineH":1.5,"clipShape":"rect"}]';
	var souvenir_json = '${Template_json}';

	//souvenir_obj is a object parsed from souvenir_json, almost every drawing would operate souvenir_obj 
	var souvenir_obj = JSON.parse(souvenir_json);
	//This is the ratio of canvas's real height to original background height. 
	var ratio = 1;
	//It is the ratio when downloading canvas
	var download_ratio = 1;
	//Indicate which operation content is activated and shown.
	var onshowContentId = "hint";
	//This indicates the position where user clicked (in other words, the index of active part in templete)
	var proc_position = 0;
	//Flags
	//var isError = false;
	var isDrawing = true;
	var isFinished = false;
	//Use to set interval
	var intervalid;
	var making_time;
	//Store album identifiers which match album_name in ${Album_name_list}. Album identifier is the only way to find an album.
	//For personal album, it stands for group id while for shared album, it stands for album_name.
	var palbum_json = '${PAlbum_identifier_json}';
	var salbum_json = '${SAlbum_identifier_json}';
	var palbum_obj = JSON.parse(palbum_json);
	var salbum_obj = JSON.parse(salbum_json);
	var album_obj = palbum_obj.concat(salbum_obj);
	
	var display_timer = new Array(souvenir_obj.length);
	var load_times = new Array(souvenir_obj.length);
	var image_list = new Array(souvenir_obj.length);
	var reload_times_max = ${not empty Reload_times_max?Reload_times_max:3};
	var load_timeout = ${not empty Load_timeout?Load_timeout:5};
	var MSG_OFFSET = 50;
	
	var move_step_ratio = 0.05;
	var zoom_step_ratio = 0.05;

	//在页面加载完成后执行的脚本
	window.onload = function() {
		if (souvenir_json == "[]") {
			document.getElementById("div_canvas").style.display = "none";
			document.getElementById("hint_text").innerHTML = "Sorry, selected template seems to be missing, please check parameter. For more information, please contact administrator or go to <a href='/Souvenirs/homepage'>homepage</a> to select a template.";
			document.getElementById("hint").style.position = "static";
			document.getElementById("hint").style.paddingTop = "0px";
			document.getElementById("main_body").style.minHeight = "10em";
			document.getElementById("div_oper").style.width = (display_width * 0.8)
					+ "px";
			alert("Invalid template name!");
			//throw SyntaxError();
		}
		//query display image of selected album on the panel of selecting image
		//为照片操作面板获取要显示的album中照片
		assignImage(true);
		//set height and width of canvas that fits the displaying area
		//设置能够匹配窗口大小的canvas宽度和高度
		changeContentSize();

		//计算下载时可选择的souvenir的不同尺寸
		calcSouvenirResolution();
		//draw canvas content using souvenir_obj
		//使用souvenir_obj绘制canvas
		//drawSouvenir("myCanvas");
		//drawing outer border of each part
		//为每个区域绘制外框
		drawBorderRect();

	}


</script>

</head>
<body onresize="changeContentSize()">
	<div class="mainbody" id="main_body" style="padding-left: 0px;">
		<!-- Nav bar on the top of the screen -->
		<nav id="nav_bar" class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li><a href="homepage">HomePage</a></li>
					<li><a href="group">Group</a></li>
					<li><a href="upload">Upload</a></li>
					<li class="active"><a href="#">Making</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form" src="${empty Avatar?'/Souvenirs/res/image/default_avatar.png':Avatar}"
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

		<form class="from" role="form" action="formPicture" method="post" id="making_form">
			<!-- Hidden textarea for storing base64 code of downloading image -->
			<textarea id="picture" style="display: none" name="picture"></textarea>
			<!-- Hidden input form for storing souvenir name appearing at filename-->
			<input id="Text_souvenir_name_instead" name="Text_souvenir_name" style="display: none" />

			<div id="div_canvas">
				<canvas id="myCanvas" width="400" height="300" style="border:1px solid #c3c3c3;"> Sorry, your browser does
				not support HTML5 canvas tag, we suggest using Chrome or Firefox to achieve best experience. </canvas>
				<!-- Hidden canvas for drawing downloading image in assigned size -->
				<canvas id="download_canvas" width="400" height="300" style="display:none"></canvas>
				<!-- Show border of each component -->
				<div id="border_rect"></div>
				<!-- Download Button -->
				<div>
					<input type="button" class="btn btn-primary " value="Make Souvenir" id="submit_btn" data-toggle="modal"
						data-target="#myModal" style="display:block"/>
					<!-- onclick="checkSubmit()" -->
				</div>
			</div>

			<div class="oper-content" id="div_oper">
				<!-- The following content is the one of display hint page -->
				<div id="hint" style="padding-top: 40%">
					<h4 id="hint_text" style="text-align: center;">Click on the rectangle area in the preview image to modify its
						content.</h4>
				</div>

				<!-- The following content is the one of selecting a picture from album -->
				<div id="choose_album" style="display: none;">
					<h4>Select Pictures from Album</h4>

					<div class="form-group">
						<label for="name" style="display:block;">Album</label> <select class="form-control" name="Select_album_name"
							onchange="queryImageInAlbum(this.options.selectedIndex)" id="select_album_name" style="display:inline;">
							<c:forEach var="album_name" items="${Album_name_list}">
								<option>${album_name }</option>
							</c:forEach>
						</select>
						<input type="text" class="form-control" placeholder="Search your pictures" style="width:150px;display:inline;"id="search_input">
						<button type="button" class="btn btn-default " style="font-size: 1.45em; text-shadow: #aaa 1px 2px 3px;width:46px;"
							onclick="searchPictures()">
							<span class="glyphicon glyphicon-search" style="color: #999"></span>
						</button>
					</div>

					<!-- Choose a picture for the rect -->
					<div class="img-content" id="img_content" style="float:left;"></div>

					<!-- Modify position and Zoom -->
					<div id="modify_panel" style="width:200px;margin-left:10px; margin-right:10px;float:left;display:block;padding:0px 48px;
						border-right-style:solid;border-right-color:white; border-right-width:2px;">
						<h4 >Move</h4>
						<img src="/Souvenirs/res/image/arrow.png" width="100" height="100" alt="Move Panel" usemap="#MoveMap"style="margin-top:5px;">

							<map name="MoveMap">
							  <area shape="circle" coords="63,22,22" alt="up"  onclick="moveUp()">
							  <area shape="circle" coords="63,80,22" alt="down" onclick="moveDown()">
							  <area shape="circle" coords="22,56,22" alt="left"  onclick="moveLeft()">
							  <area shape="circle" coords="104,56,22" alt="right"  onclick="moveRight()">
							</map>
						<h4 style="padding-top:10px;">Zoom</h4>
						<img src="/Souvenirs/res/image/zoom_in.png" width="40" alt="Zoom In" style="margin:4px;cursor:pointer;" onclick="zoomIn()">
						<img alt="Zoom Out" src="/Souvenirs/res/image/zoom_out.png" width="40" style="margin:4px; cursor:pointer;" onclick="zoomOut()">
					</div>

					<!-- Add button -->
					<div style="margin-top: 10px;clear:both;">
						<button class="btn-sm btn-primary" id="add_pic_btn" type="button" onclick="addImage2Canvas()">Add
							Selected Image</button>
					</div>
				</div>

				<!-- The following content is the one of editing text -->
				<div id="edit_text" style="display: none">
					<h4>Edit Text</h4>
					<div class="form-group">
						<label for="name">Input your words below </label>
						<textarea id="text_content" class="form-control" rows="3" style="width: 100%"></textarea>
						<span class="help-block"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span> Notice:
							Over-long text may be displayed improperly.</span>
					</div>

					<h5 style="font-weight: bold">Set Attributes</h5>

					<!-- Choose fonts -->
					<div class="row">
						<div class="col-sm-7 col-md-4 col-lg-4 narrow-col">
							<div class="form-group">
								<select class="form-control" id="style_select">
									<option id="style_Arial">Arial</option>
									<option id="style_Comic">Comic Sans MS</option>
									<option id="style_Couri">Courier New</option>
									<option id="style_Micro">Microsoft YaHei UI</option>
									<option id="style_Times">Times New Roman</option>
									<option id="style_Verda">Verdana</option>
									<%
										Locale loc = request.getLocale();
										//out.println(loc.toString());
										/* 										String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(loc);//获得当前系统字体  
																				for (int i=0; i<fontnames.length; i++) {
																					out.println("<option id='style_'>"+fontnames[i]+"</option>");
																				} */
										if (loc.toString().compareTo("zh_CN") == 0) {
											out.println("<option id='style_Verda'>宋体</option>");
											out.println("<option id='style_Verda'>楷体</option>");
										} else if (loc.toString().compareTo("ja_JP") == 0) {
											out.println("<option id='style_Verda'>Yu Mincho</option>");
										}
									%>
								</select>
							</div>
						</div>

						<!-- Choose Size -->
						<div class="col-sm-5 col-md-4  col-lg-2 narrow-col">
							<div class="form-group">
								<select class="form-control" id="size_select">
									<option id="size_6">6</option>
									<option id="size_8">8</option>
									<option id="size_10">10</option>
									<option id="size_12">12</option>
									<option id="size_14">14</option>
									<option id="size_16">16</option>
									<option id="size_18">18</option>
									<option id="size_20">20</option>
									<option id="size_22">22</option>
									<option id="size_24">24</option>
									<option id="size_28">28</option>
									<option id="size_32">32</option>
								</select>
							</div>
						</div>

						<div class="clearfix visible-xs"></div>
						<!-- Choose text color -->
						<div class="col-sm-7 col-md-4 col-lg-3 narrow-col">
							<div class="form-group">
								<input id="color1" class="iColorPicker form-control" type="text" value="#000000"
									style="background-color: #000000; width: 70%; display: inline">
							</div>
						</div>
					</div>

					<div class="row">
						<!-- Bold Button -->
						<div class="col-xs-4 col-sm-5 col-md-4 col-lg-3 narrow-col">
							<button type="button" id="bold_btn" class="btn btn-default" aria-label="Left Align" onclick="changeBold()"
								style="margin-top: 5px;">
								<span class="glyphicon glyphicon-bold" aria-hidden="true"></span>
							</button>
							<!-- Italic Button -->
							<button type="button" id="italic_btn" class="btn btn-default" aria-label="Left Align" onclick="changeItalic()"
								style="margin-top: 5px;">
								<span class="glyphicon glyphicon-italic" aria-hidden="true"></span>
							</button>
						</div>

						<!-- Choose Line Height -->
						<div class="col-xs-8 col-sm-7 col-md-7 col-lg-6 narrow-col">
							<!-- 							<div class="row">
								<div class="col-xs-5 col-sm-5 col-md-5 col-lg-4 narrow-col">Line
									Height</div>
								<div class="col-xs-6 narrow-col"> -->

							<span style="float: left; padding: 10px">Line Height</span> <select class="form-control" id="line_height_select"
								style="float: left; width: 50%"
								onchange="souvenir_obj[proc_position].lineH=parseFloat(document.getElementById('line_height_select').value)">
								<option id="line_height_10">1</option>
								<option id="line_height_15">1.5</option>
								<option id="line_height_20">2</option>
								<option id="line_height_25">2.5</option>
								<option id="line_height_30">3</option>
							</select>
						</div>

					</div>

					<div class="row" style="margin-top: 10px;">
						<div class="col-sm-4 col-md-2 col-lg-1 narrow-col">
							<button type="button" id="save_text" class="btn btn-primary" aria-label="Left Align" onclick="saveText()">Save
								Text</button>
						</div>
					</div>
				</div>

			</div>
		</form>
	</div>

	<div id="size" style="display: none"></div>
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>

	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" id="modal_dialog">
			<div class="modal-content" style="display: block" id="select_size_content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
					<h4 class="modal-title" id="myModalLabel">Select Souvenir Size</h4>
				</div>

				<div class="modal-body">
					<div class="radio"
						style="margin-bottom: 10px; padding-bottom: 10px; border-bottom-style: solid; border-width: 1px; border-color: #999999;">
						<label> <input type="radio" name="optionsRadios" id="origin_size" value="origin_size"> Origin
							Resolution (<span id="origin_size_text"></span>)
						</label>
					</div>
					<div class="radio">
						<label> <input type="radio" name="optionsRadios" id="SHD_size" value="SHD_size"> Super High
							Resolution (<span id="SHD_size_text"></span>)
						</label>
					</div>
					<div class="radio">
						<label> <input type="radio" name="optionsRadios" id="HD_size" value="HD_size" checked> High
							Resolution(<span id="HD_size_text"></span>)
						</label>
					</div>
					<div class="radio">
						<label> <input type="radio" name="optionsRadios" id="SD_size" value="SD_size"> Standard Resolution
							(<span id="SD_size_text"></span>)
						</label>
					</div>
					<div class="radio"
						style="margin-bottom: 10px; padding-bottom: 10px; border-bottom-style: solid; border-width: 1px; border-color: #999999">
						<label> <input type="radio" name="optionsRadios" id="LD_size" value="LD_size"> Low Resolution (<span
							id="LD_size_text"></span>)
						</label>
					</div>
					<div class="radio">
						<label> <input type="radio" name="optionsRadios" id="edit_size" value="edit_size"> Resolution of
							Editing Image (<span id="edit_size_text"></span>)
						</label>
					</div>
					<div class="input-group" style="width: 60%">
						<span class="input-group-addon">Filename</span>
						<input type="text" class="form-control" value="img"
							onchange="document.getElementById('Text_souvenir_name_instead').value=this.value">
						<span class="input-group-addon">.png</span>
					</div>
					<h5>
						<strong>Notice:</strong>
					</h5>
					<ul>
						<li>Image components with low resolution may be obscure when making a high-resolution souvenir.</li>
						<li>Resolution of HD or above is recommended for printing and postcard making.</li>
					</ul>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="checkSubmit()">Make Souvenir and Download</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				</div>
			</div>

			<div class="modal-content" id="loading_content" style="display: none">
				<img src="/Souvenirs/res/image/loading.gif" width="400" height="300">
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

</body>
</html>