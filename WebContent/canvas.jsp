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
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<!-- Text Color Picker -->
<script src="/Souvenirs/res/js/iColorPicker.js" type="text/javascript"></script>

<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<script>
	/*Declarion of useful global variants  */
	//display_width is the width(px) of active and available for showing contnet window 
	var display_width = window.innerWidth
			|| document.documentElement.clientWidth
			|| document.body.clientWidth;
	//display_height is similar to display_width, just stands for the height of available window
	var display_height = window.innerHeight
			|| document.documentElement.clientHeight
			|| document.body.clientHeight;
	//155 stands for the verticle space(px) of  header and footer together with margin and padding 
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
	//index of processing templete part when drawing
	//var idx = 1;
	var isError = false;
	var isDrawing = true;
	var isFinished = false;
	//Use to set interval
	var intervalid;
	var making_time;

	//在页面加载完成后执行的脚本
	window.onload = function() {
		if (souvenir_json == "[]") {
			document.getElementById("div_canvas").style.display = "none";
			document.getElementById("hint_text").innerHTML = "Sorry, selected template seems to be missing, please check parameter. For more information, please contact administrator or go to <a href='/Souvenirs/homepage'>homepage</a> to select a template.";
			document.getElementById("hint").style.position = "static";
			document.getElementById("hint").style.paddingTop = "0px";
			document.getElementById("main_body").style.minHeight = "10em";
			alert("Invalid template name!");
			//throw SyntaxError();
		}
		//set height and width of canvas that fits the displaying area
		//设置能够匹配窗口大小的canvas宽度和高度
		changeContentSize();
		//query display image of selected album on the panel of selecting image
		//为照片操作面板获取要显示的album中照片
		assignImage();
		//计算下载时可选择的souvenir的不同尺寸
		calcSouvenirResolution();
		//draw canvas content using souvenir_obj
		//使用souvenir_obj绘制canvas
		drawSouvenir("myCanvas");
		//drawing outer border of each part
		//为每个区域绘制外框
		drawBorderRect();
	}

	//This function is the main function of drawing which will call the spcific sub-function to draw shapes
	//这个函数是绘图时调用的主函数，用来完成一些初始化和子函数的调用，同时加载背景
	function drawSouvenir(canvas_id, callback) {
		var c = document.getElementById(canvas_id);
		var ctx = c.getContext("2d");

		document.getElementById("size").innerHTML = JSON
				.stringify(souvenir_obj);
		//Processing first part of templete
		var idx = 1;
		isFinished = false;
		//Loading background image
		bg = new Image();
		bg.src = "res/image/template/" + souvenir_obj[0].background;
		bg.onload = function() {
			//Drawing bavkground image
			ctx.drawImage(bg, 0, 0, c.width, c.height);
			//Creating displaying parts
			drawClip(ctx);
			//Drawing content
			drawContent(ctx, idx);
			if (callback != null && callback != "")
				callback();
		}

	}

	//本函数用来绘制要显示的区域。只有在这个区域中的内容才会被显示，其他位置的内容会被隐藏
	//目前只支持绘制矩形和圆形区域
	function drawClip(ctx) {
		//Transparent filling color
		ctx.strokeStyle = "rgba(255,255,255,0)";
		//Clipping to obtain displaying areas based on parameters in templete 
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
	//本函数根据idx决定要调用的绘图方法
	function drawContent(ctx, idx) {
		//If idx points to an invalid item in templete, just return
		if (isError || souvenir_obj[idx] == undefined
				|| souvenir_obj[idx] == null
				|| souvenir_obj[idx].type == undefined
				|| souvenir_obj[idx].type == null) {
			isFinished = true;
			return;
		} else {
			//Call specific function to finish drawing 
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
	//绘制制定的图片
	function drawImg(ctx, idx) {
		if (isError)
			return;
		//Create image and load it
		image = new Image();
		image.src = souvenir_obj[idx].url;

		image.onload = (function(idx) {
			var d = souvenir_obj[idx].zoom;
			var moveX = souvenir_obj[idx].moveX;
			var moveY = souvenir_obj[idx].moveY;
			//Transform image based on transforming metrix
			ctx.transform(souvenir_obj[idx].t11, souvenir_obj[idx].t12,
					souvenir_obj[idx].t21, souvenir_obj[idx].t22,
					souvenir_obj[idx].t13, souvenir_obj[idx].t23);
			//Draw image under the control of zoom pixels, horizontal translating pixels and verticle translating pixels
			ctx.drawImage(image, 0 + d - moveX, 0 + d - moveY, image.width - 2
					* d - moveX, image.height - 2 * d - moveY,
					R(souvenir_obj[idx].startX), R(souvenir_obj[idx].startY),
					R(souvenir_obj[idx].drawW), R(souvenir_obj[idx].drawH));
			idx++;
			//Draw the next image/text
			drawContent(ctx, idx);
		})(idx)

		image.onerror = function() {
			isError = true;
			alert("Cannot load image!");
		}
	}

	//Draw text
	//绘制文字
	function drawText(ctx, idx) {
		if (isError)
			return;
		//Form style string
		var font_style = '';
		if (souvenir_obj[idx].italic)
			font_style += "italic ";
		if (souvenir_obj[idx].bold)
			font_style += "bold ";
		font_style += (isDrawing ? souvenir_obj[idx].size : Math
				.round(souvenir_obj[idx].size / ratio * download_ratio))
				+ 'px "' + souvenir_obj[idx].style + '" ';
		ctx.font = font_style;
		//Set color
		ctx.fillStyle = souvenir_obj[idx].color;
		/* 		ctx.fillText(souvenir_obj[idx].text, R(souvenir_obj[idx].startX+souvenir_obj[idx].paddingL),
		 R(souvenir_obj[idx].startY+souvenir_obj[idx].paddingT+souvenir_obj[idx].size), R(souvenir_obj[idx].maxW));  */
		//Call draw_long_text() to draw text at specific position considering text auto-wrap and line height
		draw_long_text(souvenir_obj[idx].text, ctx, R(souvenir_obj[idx].startX
				+ souvenir_obj[idx].paddingL),
				R(souvenir_obj[idx].startY + souvenir_obj[idx].paddingT)
						+ (isDrawing ? souvenir_obj[idx].size : Math
								.round(souvenir_obj[idx].size / ratio
										* download_ratio)),
				R(souvenir_obj[idx].maxW), (isDrawing ? souvenir_obj[idx].size
						: Math.round(souvenir_obj[idx].size / ratio
								* download_ratio)), souvenir_obj[idx].lineH);
		idx++;
		drawContent(ctx, idx);
	}

	//Auxiliary function of auto-wrap, without considering of vertical out-of-range condition
	//辅助绘图函数，可完成自动换行，并调整行距
	function draw_long_text(longtext, ctx, startX, startY, max_width, size,
			line_height) {
		var line_text = new String();
		var lineno = 1;
		for (i = 0; i < longtext.length; i++) {
			//letters in line_text doesn't take line full width  
			if (ctx.measureText(line_text).width < max_width)
				line_text += longtext[i];
			else {
				//Letters overflow one line then put previous line onto canvas and create a new line 
				ctx.fillText(line_text, startX, startY + (lineno - 1) * size
						* line_height, max_width);
				line_text = new String();
				line_text += longtext[i];
				lineno++;
			}
		}
		ctx.fillText(line_text, startX, startY + (lineno - 1) * size
				* line_height, max_width);
	}

	//裁剪五角星区域的函数，未启用
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

	//绘制原型裁剪区域函数
	function createCircleClip(ctx, stX, stY, radius) {
		ctx.beginPath();
		ctx.arc(stX, stY, radius, 0, Math.PI * 2, true);
		ctx.closePath();
		//ctx.clip();
	}

	//绘制矩形区域函数
	function createRectClip(ctx, stX, stY, width, height) {
		ctx.rect(stX, stY, width, height);
		ctx.stroke();
	}

	//绘制预览图像中可操作区域的外框，使用js写入不同的html脚本以及使用js控制显示的css类来实现
	function drawBorderRect() {
		var div_id = document.getElementById("border_rect");
		var div_str = new String();
		var margin_left = jQuery("#myCanvas").offset().left;
		var margin_top = jQuery("#myCanvas").offset().top;

		for (i = 1; i < souvenir_obj.length; i++) {
			if (souvenir_obj[i].type == "image")
				div_str += '<a><div class="border-rect" id="border_rect_' + i
						+ '" style="left:'
						+ (margin_left + R(souvenir_obj[i].startX)) + 'px;top:'
						+ (margin_top + R(souvenir_obj[i].startY))
						+ 'px;width:' + (R(souvenir_obj[i].drawW) + 2)
						+ 'px;height:' + (R(souvenir_obj[i].drawH) + 2)
						+ 'px" onclick="changeOperContent(\'choose_album\', '
						+ i + ')"></div></a>';
			else if (souvenir_obj[i].type == "text")
				div_str += '<a><div class="border-rect"  id="border_rect_'
						+ i
						+ '" style="left:'
						+ (margin_left + R(souvenir_obj[i].startX))
						+ 'px;top:'
						+ (margin_top + R(souvenir_obj[i].startY))
						+ 'px;width:'
						+ (R(souvenir_obj[i].maxW + souvenir_obj[i].paddingL
								+ souvenir_obj[i].paddingR) + 2)
						+ 'px;height:'
						+ (R(souvenir_obj[i].maxH + souvenir_obj[i].paddingT
								+ souvenir_obj[i].paddingB) + 2)
						+ 'px" onclick="changeOperContent(\'edit_text\', ' + i
						+ ')"></div></a>';
			else
				alert("Internal Error!");
		}
		div_id.innerHTML = div_str;
	}

	//选择一张图片时的响应函数，将选中的图片激活，之前选中的图片失活，改变它们的外观
	function selectImage(idx) {
		if (selected_image >= 1)
			document.getElementById("select_img_" + selected_image).className = "img";
		selected_image = idx;
		document.getElementById("select_img_" + selected_image).className = "img-clicked";
	}

	//根据ratio计算实际显示在屏幕中的像素值
	function R(val) {
		//isDrawing of true indicates the status of drawing, else it means downloading
		return (isDrawing ? ratio : download_ratio) * val;
	}

	//本函数通过Ajax从服务器获取选定的album中的照片
	function queryImageInAlbum(str) {
		var xmlhttp;
		var album = document.getElementById("select_album_name").value;
		selected_image = 0;//set selected image index to 0 when changine another album 
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
		//Having a response
		xmlhttp.onreadystatechange = function() {
			//Check response status 
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				image_json = xmlhttp.responseText;
			}
			//Assign displaying image using image_json 
			assignImage();
		}
		//From URL and create connection
		xmlhttp.open("GET", "AlbumAjax?album_name=" + album, true);
		xmlhttp.send();
	}

	//此函数通过js完成Ajax获取的album中的照片在页面中的显示
	function assignImage() {
		var obj = JSON.parse(image_json);
		var img_content = new String();
		if (obj.length == 0)
			img_content = "<h4>There is no picture in the album.</h4>";
		for (i = 0; i < obj.length; i++) {
			img_content += '<div class="img" id="select_img_'
					+ (i + 1)
					+ '"><a onclick="selectImage('
					+ (i + 1)
					+ ')"><img id="album_image_'
					+ (i + 1)
					+ '" src="'
					+ obj[i].Addr
					+ '" alt="'
					+ obj[i].Filename
					+ '" width="120" height="120"></a><div class="desc" ><a style="color:black">'
					+ obj[i].Filename + '</a></div></div>';
		}
		document.getElementById("img_content").innerHTML = img_content;
	}

	//更换操作面板时的响应函数，完成旧面板的隐藏与新面板的显示，同时进行相关变量的销毁与初始化
	function changeOperContent(new_content_id, idx) {
		selected_image = 0;
		assignImage();

		document.getElementById(onshowContentId).style.display = "none";
		if (proc_position >= 1)
			document.getElementById("border_rect_" + proc_position).className = "border-rect";
		//For editing text panel, clear all the value in forms
		if (onshowContentId == "edit_text") {
			document.getElementById("size_" + souvenir_obj[proc_position].size).selected = "";
			document.getElementById("style_"
					+ souvenir_obj[proc_position].style.substring(0, 5)).selected = "";
			document.getElementById("line_height_"
					+ souvenir_obj[proc_position].lineH * 10).selected = "";
		}

		onshowContentId = new_content_id;

		proc_position = idx;
		document.getElementById(onshowContentId).style.display = "block";
		document.getElementById("border_rect_" + idx).className = "border-rect-active";

		document.getElementById("main_body").style.minHeight = Math
				.max((jQuery("#div_oper").height() + jQuery("#myCanvas")
						.offset().top),
						document.getElementById("myCanvas").height)
				+ "px";

		//For eding text panel, set attribute values of text to forms 
		if (new_content_id == "edit_text") {
			document.getElementById("text_content").value = souvenir_obj[proc_position].text;
			document.getElementById("size_" + souvenir_obj[proc_position].size).selected = "selected";
			document.getElementById("style_"
					+ souvenir_obj[proc_position].style.substring(0, 5)).selected = "selected";
			document.getElementById("color1").value = souvenir_obj[proc_position].color;
			document.getElementById("color1").style.backgroundColor = souvenir_obj[proc_position].color;
			document.getElementById("line_height_"
					+ souvenir_obj[proc_position].lineH * 10).selected = "selected";
			//Set bold button status
			if (souvenir_obj[proc_position].bold) {
				document.getElementById("bold_btn").className = "btn btn-default active";
			} else
				document.getElementById("bold_btn").className = "btn btn-default";
			//Set italic button status
			if (souvenir_obj[proc_position].italic) {
				document.getElementById("italic_btn").className = "btn btn-default active";
			} else
				document.getElementById("italic_btn").className = "btn btn-default";
		}
	}

	//此函数是向canvas添加照片时的响应函数，即add Image按钮的响应函数
	function addImage2Canvas() {
		if (proc_position >= 1 && selected_image >= 1) {
			souvenir_obj[proc_position].url = document
					.getElementById("album_image_" + selected_image).src;
			drawSouvenir("myCanvas");
		}
	}

	//调整页面中各个组件的大小。
	//基本规则是：保证canvas的高度可以在页面中完整显示，宽度与高度保持原图比例，显示区占总体宽度的80%并居中，
	//显示区有最小宽度是768px，保证操作面板不会被挤得无法使用，中央操作区域的最小高度由操作面板和canvas的最大值决定
	//选择图片时图片的展示区域高度由操作面板的高度、以及面板中其他组件的高度共同决定
	function changeContentSize() {
		display_width = window.innerWidth
				|| document.documentElement.clientWidth
				|| document.body.clientWidth;

		display_height = window.innerHeight
				|| document.documentElement.clientHeight
				|| document.body.clientHeight;
		display_height = display_height - 190;
		//alert(jQuery("#nav_bar").outerHeight(true)+" "+jQuery("#myCanvas").outerHeight(true));

		var c = document.getElementById("myCanvas");
		c.width = display_height
				* (souvenir_obj[0].originW / souvenir_obj[0].originH);
		c.height = display_height;
		ratio = c.height / souvenir_obj[0].originH;

		document.getElementById("main_body").style.minWidth = Math.max(
				(c.width * 1.75 + (20 * 3)), 768 - 10)
				+ "px";

		document.getElementById("div_canvas").style.width = c.width.toString()
				+ "px";

		var margin_left = jQuery("#myCanvas").offset().left;
		document.getElementById("div_oper").style.left = String(Math
				.round(margin_left + c.width))
				+ 'px';
		document.getElementById("div_oper").style.top = jQuery("#myCanvas")
				.offset().top
				+ "px";
		document.getElementById("div_oper").style.width = (jQuery("#main_body")
				.innerWidth()
				- c.width - (jQuery("#div_canvas").offset().left - jQuery(
				"#main_body").offset().left) * 3)
				+ "px";

		document.getElementById("text_content").style.width = (jQuery(
				"#main_body").width()
				- c.width - (jQuery("#div_canvas").offset().left - jQuery(
				"#main_body").offset().left) * 3)
				+ "px";
		//20 is margin of each column

		document.getElementById("img_content").style.width = display_width
				* 0.8 - (20 * 3) - c.width - 10 + "px";
		document.getElementById("img_content").style.height = display_height
				- (41 + 74 + 39) + "px";//41 is the height of <h4>, 74 is the height of form and 39 is the height of button	

		document.getElementById("main_body").style.minHeight = Math
				.max((jQuery("#div_oper").height() + jQuery("#myCanvas")
						.offset().top), c.height)
				+ "px";

		drawSouvenir("myCanvas");
		drawBorderRect();
	}

	//本函数是用户单击加粗按钮时的响应函数，如果之前是加粗状态就改成不加粗，否则改成加粗，同时对显示按钮的样式进行修改
	function changeBold() {
		souvenir_obj[proc_position].bold = !souvenir_obj[proc_position].bold;
		if (souvenir_obj[proc_position].bold) {
			document.getElementById("bold_btn").className = "btn btn-default active";
		} else
			document.getElementById("bold_btn").className = "btn btn-default";
	}

	//本函数是用户单击倾斜按钮时的响应函数，逻辑与加粗按钮响应函数相似
	function changeItalic() {
		souvenir_obj[proc_position].italic = !souvenir_obj[proc_position].italic;
		if (souvenir_obj[proc_position].italic) {
			document.getElementById("italic_btn").className = "btn btn-default active";
		} else
			document.getElementById("italic_btn").className = "btn btn-default";
	}

	//用户点击save text的响应函数，将文字的绘制属性保存
	function saveText() {
		var text = document.getElementById("text_content").value;
		souvenir_obj[proc_position].text = text;
		souvenir_obj[proc_position].style = document
				.getElementById("style_select").value;
		souvenir_obj[proc_position].size = parseInt(document
				.getElementById("size_select").value);
		souvenir_obj[proc_position].color = document.getElementById("color1").value;
		drawSouvenir("myCanvas");
	}

	//用户在下载模态框中点击download时的响应函数，将canvas中的内容按照用户选定的尺寸在一个隐藏的canvas中绘制要下载的图片
	function checkSubmit() {
		isDrawing = false;
		document.getElementById("select_size_content").style.display = "none";
		document.getElementById("loading_content").style.display = "block";
		document.getElementById("modal_dialog").style.width = "400px";
		if (document.getElementById("origin_size").checked)
			download_ratio = 1;
		else if (document.getElementById("SHD_size").checked)
			download_ratio = 1080 / souvenir_obj[0].originH;
		else if (document.getElementById("HD_size").checked)
			download_ratio = 720 / souvenir_obj[0].originH;
		else if (document.getElementById("SD_size").checked)
			download_ratio = 576 / souvenir_obj[0].originH;
		else if (document.getElementById("LD_size").checked)
			download_ratio = 480 / souvenir_obj[0].originH;
		else if (document.getElementById("edit_size").checked)
			download_ratio = ratio;
		else
			download_ratio = ratio;
		document.getElementById("download_canvas").width = R(souvenir_obj[0].originW);
		document.getElementById("download_canvas").height = R(souvenir_obj[0].originH);
		intervalid = setInterval("fun()", 200);
		making_time = setInterval("checkMakingDone()", 500);
		drawSouvenir("download_canvas");

		//isDrawing = true;
	}

	//checkSubmit()中计时器的响应函数，在隐藏的canvas中绘制完成后将canvas的转码为png图片并上传
	function fun() {
		if (isFinished) {
			document.getElementById('picture').innerHTML = document
					.getElementById('download_canvas').toDataURL('image/png');
			//alert(document.getElementById('picture').innerHTML.length/1024/1024);
			if (document.getElementById('picture').innerHTML.length >= 10485760) {
				alert("Souvenir is too large, its size should be less than 7MB");
				clearInterval(intervalid);
				isDrawing = true;
				return;
			}
			//alert('making finished');
			clearInterval(intervalid);
			document.getElementById('making_form').submit();
/* 			isDrawing = true;
			$('#myModal').modal('toggle');
			document.getElementById("loading_content").style.display = "none";
			document.getElementById("select_size_content").style.display = "block";
			document.getElementById("modal_dialog").style.cssText = ""; */
		}
	}

	function checkMakingDone() {
		var xmlhttp;
		if (window.XMLHttpRequest) {
			// IE7+, Firefox, Chrome, Opera, Safari 浏览器执行代码
			xmlhttp = new XMLHttpRequest();
		} else {
			// IE6, IE5 浏览器执行代码
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		//Having a response
		xmlhttp.onreadystatechange = function() {
			//Check response status 
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				if (xmlhttp.responseText == "true") {
					clearInterval(making_time);
					isDrawing = true;
					$('#myModal').modal('toggle');
					document.getElementById("loading_content").style.display = "none";
					document.getElementById("select_size_content").style.display = "block";
					document.getElementById("modal_dialog").style.cssText = "";
				} else if (xmlhttp.responseText.indexOf("true")>=0&&xmlhttp.responseText.length>4){
					clearInterval(making_time);
					isDrawing = true;
					$('#myModal').modal('toggle');
					document.getElementById("loading_content").style.display = "none";
					document.getElementById("select_size_content").style.display = "block";
					document.getElementById("modal_dialog").style.cssText = "";
					alert("Sorry, making souvenir failed! Error: "+xmlhttp.responseText.substring(5));
				}else{
					return;
				}
			}
		}
		//From URL and create connection
		xmlhttp.open("GET", "checkMakingDone",  true);
		xmlhttp.send();
	}	
	//This function calculates several size of downloading which would be displayed on the modal
	//Calculate width of downloading image based on its height
	//There are six kinds of resolution: template-original, Super High Definition(verticle 1080px), 
	//High Definition(verticle 720px), Standard Definition(verticle 576px), Low Definition(verticle 480px),
	//editing size which is the size of canvas when editing souvenir
	function calcSouvenirResolution() {
		document.getElementById("origin_size_text").innerHTML = souvenir_obj[0].originW
				+ "px * " + souvenir_obj[0].originH + "px";
		document.getElementById("SHD_size_text").innerHTML = Math
				.round(souvenir_obj[0].originW
						* (1080 / souvenir_obj[0].originH))
				+ "px * 1080px";
		document.getElementById("HD_size_text").innerHTML = Math
				.round(souvenir_obj[0].originW
						* (720 / souvenir_obj[0].originH))
				+ "px * 720px";
		document.getElementById("SD_size_text").innerHTML = Math
				.round(souvenir_obj[0].originW
						* (576 / souvenir_obj[0].originH))
				+ "px * 576px";
		document.getElementById("LD_size_text").innerHTML = Math
				.round(souvenir_obj[0].originW
						* (480 / souvenir_obj[0].originH))
				+ "px * 480px";
		document.getElementById("edit_size_text").innerHTML = document
				.getElementById("myCanvas").width
				+ "px * " + document.getElementById("myCanvas").height + "px";
	}
</script>

<style type="text/css">
@media ( max-width : 845px) {
	body {
		min-width: 845px;
	}
}

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

div.border-rect-active {
	position: absolute;
	border-style: solid;
	border-color: #c71585;
	border-width: 2px;
}

#border_rect a :hover {
	border-color: rgba(255, 0, 0, 0.5);
	border-width: 4px;
}

.narrow-col {
	padding-left: 10px;
	padding-right: 10px;
}
</style>
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
					<li class="active"><a href="homepage">HomePage</a></li>
					<li><a href="#">Group</a></li>
					<li><a href="#">Upload</a></li>
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

		<form class="from" role="form" action="formPicture" method="post" id="making_form">
			<!-- Hidden textarea for storing base64 code of downloading image -->
			<textarea id="picture" style="display: none" name="picture"></textarea>

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
						data-target="#myModal" />
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
						<label for="name">Album</label> <select class="form-control" name="Select_album_name"
							onchange="queryImageInAlbum()" id="select_album_name">
							<c:forEach var="album_name" items="${Album_List}">
								<option>${album_name }</option>
							</c:forEach>
						</select>
					</div>


					<!-- Choose a picture for the rect -->
					<div class="img-content" id="img_content"></div>

					<!-- Modify position and Zoom -->
					<div></div>

					<!-- Add button -->
					<div style="margin-top: 10px;">
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