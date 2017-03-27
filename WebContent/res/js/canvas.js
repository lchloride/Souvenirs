/**
 * 
 */
//This function is the main function of drawing which will call the spcific sub-function to draw shapes
//这个函数是绘图时调用的主函数，用来完成一些初始化和子函数的调用，同时加载背景
function drawSouvenir(canvas_id, callback) {
	var c = document.getElementById(canvas_id);
	var ctx = c.getContext("2d");
	
	for (var i=0; i<souvenir_json.length; i++)
		clearInterval(display_timer[i]);
	
	//Processing first part of templete
	var idx = 1;
	isFinished = false;
	//Loading background image
	bg = new Image();
	
	bg.onload = function() {
		//Drawing background image
		ctx.drawImage(bg, 0, 0, c.width, c.height);
		//Creating displaying parts
		drawClip(ctx);
		for (var idx=1; idx<souvenir_obj.length; idx++) {
			load_times[idx] = 1;
			display_timer[idx] = setInterval("checkDisplay("+idx+")",Math.round(load_timeout*1000));
			drawContent(ctx, idx);
		}
		isFinished = true;
		//clearInterval(display_timer);
	}
	bg.src = "res/image/template/" + souvenir_obj[0].background;
}

//本函数用来绘制要显示的区域。只有在这个区域中的内容才会被显示，其他位置的内容会被隐藏
//目前只支持绘制矩形和圆形区域
function drawClip(ctx) {
	//Transparent filling color
	ctx.strokeStyle = "rgba(255,255,255,0)";
	//Clipping to obtain displaying areas based on parameters in templete 
	for (var i = 1; i < souvenir_obj.length; i++) {
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
	
	if ( souvenir_obj[idx] == null
			|| souvenir_obj[idx].type == undefined
			|| souvenir_obj[idx].type == null) {
		return;
	} else {
		//display_idx = idx;
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
	//if (isError)
		//return;
	//Create image and load it
	var image = new Image();
	image_list[idx] = image;
	//alert("before onload"+idx);
	image.onload = (function () {
		var image = this;
		var dx = souvenir_obj[idx].zoom*image.width;
		var dy = souvenir_obj[idx].zoom*image.height;
		if (2*dx >= image.width*0.99 || 2*dy >= image.height*0.99) {
			souvenir_obj[idx].zoom -= zoom_step_ratio;
			dx = souvenir_obj[idx].zoom*image.width;
			dy = souvenir_obj[idx].zoom*image.height;
			$.bootstrapGrowl("Already maximum size.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		}
		
		var moveX = souvenir_obj[idx].moveX*image.width;
		var moveY = souvenir_obj[idx].moveY*image.height;
		if (moveX >= image.width-dx) {
			souvenir_obj[idx].moveX -= move_step_ratio;
			moveX = souvenir_obj[idx].moveX*image.width;
			$.bootstrapGrowl("Out of right range.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		}
		if (moveX <= 2*dx-image.width) {
			souvenir_obj[idx].moveX += move_step_ratio;
			moveX = souvenir_obj[idx].moveX*image.width;
			$.bootstrapGrowl("Out of left range.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		}
		if (moveY >= image.height-dy) {
			souvenir_obj[idx].moveY -= move_step_ratio;
			moveY = souvenir_obj[idx].moveY*image.height;
			$.bootstrapGrowl("Out of bottom range.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		}
		if (moveY <= 2*dy-image.height) {
			souvenir_obj[idx].moveY += move_step_ratio;
			moveY = souvenir_obj[idx].moveY*image.height;
			$.bootstrapGrowl("Out of top range.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		}
		//Transform image based on transforming metrix
		ctx.transform(souvenir_obj[idx].t11, souvenir_obj[idx].t12,
				souvenir_obj[idx].t21, souvenir_obj[idx].t22,
				souvenir_obj[idx].t13, souvenir_obj[idx].t23);
		//Draw image under the control of zoom pixels, horizontal translating pixels and verticle translating pixels
		ctx.drawImage(image, 0 + dx - moveX, 0 + dy - moveY, image.width - 2
				* dx, image.height - 2 * dy,
				R(souvenir_obj[idx].startX), R(souvenir_obj[idx].startY),
				R(souvenir_obj[idx].drawW), R(souvenir_obj[idx].drawH));
	})
	image.src = souvenir_obj[idx].url;//+souvenir_obj[idx].url.indexOf("default")>=0?"":"&random="+encodeURIComponent(Math.random());
	
	image.onerror = function() {
		//isError = true;
		$.bootstrapGrowl("Displaying "+idx+" failed. ", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	}
}


//Draw text
//绘制文字
function drawText(ctx, idx) {
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
	//Call draw_long_text() to draw text at specific position considering text auto-wrap and line height
	draw_long_text(souvenir_obj[idx].text, ctx, R(souvenir_obj[idx].startX
			+ souvenir_obj[idx].paddingL),
			R(souvenir_obj[idx].startY + souvenir_obj[idx].paddingT)
					+ (isDrawing ? souvenir_obj[idx].size : Math
							.round(souvenir_obj[idx].size / ratio
									* download_ratio)),
			R(souvenir_obj[idx].maxW), (isDrawing ? souvenir_obj[idx].size
					: Math.round(souvenir_obj[idx].size / ratio
							* download_ratio)), souvenir_obj[idx].lineH, R(souvenir_obj[idx].maxH));
}

//Auxiliary function of auto-wrap, without considering of vertical out-of-range condition
//辅助绘图函数，可完成自动换行，并调整行距
function draw_long_text(longtext, ctx, startX, startY, max_width, size,
		line_height, max_height) {
	var line_text = new String();
	var lineno = 1;
	// max_line_number指定了画布区域中可以绘制的最大文字行数
	var max_line_number = Math.floor(max_height / (size * line_height));
	// for循环结束条件：文字已经画完 或 行数已达max_line_number
	for (var i = 0; i < longtext.length && lineno <= max_line_number; i++) {
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
	// 如果行数比最大行数小(文字正常画完而退出)的时候，把剩余行的内容画上；否则说明已绘制的文字已经填满整个区域，无法再画文字
	if (lineno <= max_line_number)
		ctx.fillText(line_text, startX, startY + (lineno - 1) * size * line_height, max_width);
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

function checkDisplay(idx) {
	document.getElementById("size").innerHTML += idx + ", "
	if (image_list[idx] != undefined && !image_list[idx].complete) {
		if (load_times[idx] <= reload_times_max) {
			load_times[idx]++;
			$.bootstrapGrowl("Loading picture "+idx+" failed. Trying to load it with longer time.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
		} else {
			$.bootstrapGrowl("Displaying "+idx+" failed. ", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
			clearInterval(display_timer[idx]);
		}
	} else {
		clearInterval(display_timer[idx]);

	}
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

	for (var i = 1; i < souvenir_obj.length; i++) {
		if (souvenir_obj[i].type == "image")
			div_str += '<a><div class="border-rect" id="border_rect_' + i
					+ '" style="left:'
					+ (margin_left + R(souvenir_obj[i].startX)) + 'px;top:'
					+ (margin_top + R(souvenir_obj[i].startY))
					+ 'px;width:' + (R(souvenir_obj[i].drawW) + 1)
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
							+ souvenir_obj[i].paddingR) + 1)
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

//本函数通过Ajax从服务器获取选定的album中的照片，idx指的是选中的option的序号
function queryImageInAlbum(idx) {
	selected_image = 0;//set selected image index to 0 when changing another album 
	ajaxProcess(queryImageInAlbumCallback, "AlbumAjax?album_identifier=" + album_obj[idx]+(idx<palbum_obj.length?"&range=personal":"&range=shared"));
}

function queryImageInAlbumCallback(result) {
	image_json = result;
	//Assign displaying image using image_json 
	assignImage(true);
}

//此函数通过js完成Ajax获取的album中的照片在页面中的显示
function assignImage(album_flag) {
	
	var obj = JSON.parse(image_json);
	var img_content = new String();
	if (obj.length == 0)
		if (album_flag)
			img_content = "<h4>There is no picture in the album.</h4>";
		else
			img_content = "<h4>Cannot find any proper picture.</h4>";
	for (var i = 0; i < obj.length; i++) {
		img_content += '<div class="img" id="select_img_'
				+ (i + 1)
				+ '"><a onclick="selectImage('
				+ (i + 1)
				+ ')"><img id="album_image_'
				+ (i + 1)
				+ '" src="'
				+ obj[i].Addr
				+ '" alt="'
				+ obj[i].Filename //onmouseout="$(\'.img\').css(\'height\', \'auto\')"
				+ '" width="120" height="120"></a><div class="desc"  >'
				+ '<a style="color:black" href="#" onmouseover="activate('+(i+1)+')"'   
				+ 'data-placement="top" data-toggle="tooltip"'
				+ ' title="'+'User: '+obj[i].Username+' | Album: '+obj[i].AlbumName
				+ (obj[i].Description==""?"":" | Description: "+obj[i].Description)+'">'
				+ obj[i].Filename + '</a></div></div>';
	}
	document.getElementById("img_content").innerHTML = img_content;
	document.getElementById("img_content").style.width = $('#choose_album').width()-$('#modify_panel').outerWidth(true)+"px";
	$("[data-toggle='tooltip']").tooltip();
	$(".tooltip").css("background-color", "gray");
}

//更换操作面板时的响应函数，完成旧面板的隐藏与新面板的显示，同时进行相关变量的销毁与初始化
function changeOperContent(new_content_id, idx) {
	selected_image = 0;
	assignImage(true);

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
	} else if (new_content_id == "choose_album") {
		document.getElementById("img_content").style.width = $('#choose_album').width()-$('#modify_panel').outerWidth(true)+"px";
		document.getElementById("select_album_name").style.width = document.getElementById("img_content").style.width;
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

	document.getElementById("img_content").style.width = $('#choose_album').width()-$('#modify_panel').outerWidth(true)+"px";
	//display_width* 0.8 - (20 * 3) - $('#div_canvas').outerWidth(true) - 10 - $('#modify_panel').outerWidth(true) + "px";
	document.getElementById("img_content").style.height = display_height
			- (41 + 74 + 39) + "px";//41 is the height of <h4>, 74 is the height of form and 39 is the height of button	

	document.getElementById("main_body").style.minHeight = Math
			.max((jQuery("#div_oper").height() + jQuery("#myCanvas")
					.offset().top), c.height)
			+ "px";

	drawSouvenir("myCanvas");
	setTimeout(drawBorderRect(), 200);
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
	//休眠1秒，防止页面瞬间卡死
	var t1 = setTimeout(
			function() {
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
			}, 1000);

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
	}
}

function checkMakingDone() {
	ajaxProcess(checkMakingDoneCallback, "checkMakingDone");
}

function checkMakingDoneCallback(result) {
	if (result == "true") {
		clearInterval(making_time);
		isDrawing = true;
		$('#myModal').modal('toggle');
		document.getElementById("loading_content").style.display = "none";
		document.getElementById("select_size_content").style.display = "block";
		document.getElementById("modal_dialog").style.cssText = "";
	} else if (result.indexOf("true") >= 0
			&& xmlhttp.responseText.length > 4) {
		clearInterval(making_time);
		isDrawing = true;
		$('#myModal').modal('toggle');
		document.getElementById("loading_content").style.display = "none";
		document.getElementById("select_size_content").style.display = "block";
		document.getElementById("modal_dialog").style.cssText = "";
		alert("Sorry, making souvenir failed! Error: "
				+ result.substring(5));
	} else {
		return;
	}
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

function moveUp() {
	if (souvenir_obj[proc_position].type == "image") {
		souvenir_obj[proc_position].moveY -= move_step_ratio;
		drawSouvenir("myCanvas");
	}else {
		alert("Wrong parameter");
		return;
	}
}

function moveDown() {
	if (souvenir_obj[proc_position].type == "image") {
		souvenir_obj[proc_position].moveY += move_step_ratio;
		drawSouvenir("myCanvas");
	}else {
		alert("Wrong parameter");
		return;
	}
}

function moveLeft() {
	if (souvenir_obj[proc_position].type == "image") {
		souvenir_obj[proc_position].moveX -= move_step_ratio;
		drawSouvenir("myCanvas");
	}else {
		alert("Wrong parameter");
		return;
	}
}

function moveRight() {
	if (souvenir_obj[proc_position].type == "image") {
		souvenir_obj[proc_position].moveX += move_step_ratio;
		drawSouvenir("myCanvas");
	}else {
		alert("Wrong parameter");
		return;
	}
}

function zoomIn() {
	if (souvenir_obj[proc_position].type == "image") {
			souvenir_obj[proc_position].zoom += zoom_step_ratio;
			drawSouvenir("myCanvas");
	}else {
		$.bootstrapGrowl("Cannot move text.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
		return;
	}
}

function zoomOut() {
	if (souvenir_obj[proc_position].type == "image") {
		souvenir_obj[proc_position].zoom -= zoom_step_ratio;
		drawSouvenir("myCanvas");
	}else {
		$.bootstrapGrowl("Cannot move text.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
		return;
	}
}

function searchPictures() {
	var keyword = $('#search_input').val();
	ajaxProcess(searchPicturesCallback, "/Souvenirs/searchPictures?keyword="+encodeURIComponent(keyword));
}

function searchPicturesCallback(result) {
	if (result.indexOf('[') == 0) {
		image_json = result;
		assignImage(false);
	} else {
		$.bootstrapGrowl("An error occurred. "+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
	}
}

function activate(idx) {
	$('.img').css('height', 'auto');
	var height = $('#select_img_'+idx).outerHeight();
	//alert(idx+", "+height);

	var i=1;
	while (document.getElementById("select_img_"+i)!=null) {
		document.getElementById("select_img_"+i).style.height = height+"px";
		i++;
	}
}

function normalize(idx) {
	var i=1;
	alert(idx);
	while (document.getElementById("select_img_"+i)!=null) {
		document.getElementById("select_img_"+i).style.height = "auto";
		i++;
	}
}