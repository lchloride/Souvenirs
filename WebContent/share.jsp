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
<title>Share</title>
<script type="text/javascript">
	<%-- var image_list_count = <%=request.getAttribute("image_json_list")!=null?((List<String>)request.getAttribute("image_json_list")).size():0%>; --%>
	var image_obj;
	var selected_image = new Array();
	var MSG_OFFSET = 50;
	window.onload = function() {
		$(".container").css("min-width",
				($("#mainbody").width()-60) + "px");
		drawImageList('${image_list_json}');
		displaySelectedPicture();
	}
	
	function ajaxProcess(callback, URL, send)
	{
	  var xmlhttp;    
	  if (window.XMLHttpRequest)
	  {
	    // IE7+, Firefox, Chrome, Opera, Safari 浏览器执行代码
	    xmlhttp=new XMLHttpRequest();
	  }
	  else
	  {
	    // IE6, IE5 浏览器执行代码
	    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  xmlhttp.onreadystatechange=function()
	  {
	    if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
	      callback(xmlhttp.responseText);
	    }
	  }
	  if (send == undefined || send == "" ) {
	  	xmlhttp.open("GET",URL, true);
	  	xmlhttp.send();
	  } else {
	  	xmlhttp.open("POST",URL, true);
	  	xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	  	xmlhttp.send(send);
	  }
	}
	
	function queryPictureInAlbum(album_name) {
		ajaxProcess(drawImageList, "/Souvenirs/AlbumAjax?album_identifier="+album_name);
	}
	
/* 	function queryPictureCallback(result) {
		drawImageList(result);
	} */
	
	function drawImageList(image_json) {
		image_obj = JSON.parse(image_json);
		var image_html = "";
		if (image_obj.length == 0)
			image_html = "<tr><td><h4>No Image In this Album!</h4></td></tr>";
		for (i=0; i<image_obj.length; i++)
			image_html +=		'<tr>'+
				'<td style="border-top: 1px solid #bbb;"><input type="checkbox" value="" id="checkbox_'+i+'"></td>'+
				'<td style="border-top: 1px solid #bbb;">'+
					'<div class="media">'+
						'<a class="pull-left" href="#"> <img class="media-object" src="'+image_obj[i].Addr+'" alt="'+image_obj[i].Filename+'" width="120" height="">'+
						'</a>'+
						'<div class="media-body">'+
							'<h4 class="media-heading">'+image_obj[i].Filename+'</h4>'+
							'<p>'+
								'Album name: '+image_obj[i].AlbumName+'<br> '+
								'Description: '+image_obj[i].Description+'<br>'+
								'Uploaded Time: '+image_obj[i].UploadTime+'<br>'+
							'</p>'+
						'</div>'+
					'</div>'+
				'</td>'+
			'</tr>';
		document.getElementById("image_list_table_body").innerHTML = image_html;
	}
	
	function addPictures() {
		for (i=0; i<image_obj.length; i++) {
			if (document.getElementById("checkbox_"+i).checked) {
				var item = {album_name:image_obj[i].AlbumName, filename:image_obj[i].Filename, addr:image_obj[i].Addr};
				var compare_flag = false;
				for (j=0; j<selected_image.length; j++)
					if (cmpSelectedImageItem(selected_image[j], item)) {
						compare_flag = true;
						break;
					}
				if (!compare_flag) {
					selected_image.push(item);
					$.bootstrapGrowl("Add "+image_obj[i].Filename+" in Album "+image_obj[i].AlbumName+" successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
				}else
					$.bootstrapGrowl(image_obj[i].Filename+" in Album "+image_obj[i].AlbumName+" has already existed.", { type: 'danger' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
			}
		}
		
		displaySelectedPicture();
	}
	
	function displaySelectedPicture() {
		var image_html = "";
		document.getElementById("share_list_num").innerHTML = selected_image.length;
		for (i=0; i<selected_image.length; i++)
			image_html += 	'<tr>'+
				// '<td><input type="checkbox" value=""></td>'+
				'<td style="border-top: 1px solid #bbb;">'+selected_image[i].album_name+'</td>'+
				'<td style="border-top: 1px solid #bbb;">'+selected_image[i].filename+'</td>'+
				'<td style="border-top: 1px solid #bbb;"><div id="large"></div>'+
				'<button type="button" class="btn btn-link " style="padding: 0px" onmouseover="mouseover(\''+selected_image[i].addr+'\')" onmouseout="mouseout()">Preview</button>'+
				' | <button type="button" class="btn btn-link " style="padding: 0px" onclick="removeSelectedImage('+i+')">Remove</button></td>'+
				'</td>'	
				'</tr>';
		image_html += '<tr><td></td><td></td><td></td><td></td></tr>';
		document.getElementById("selected_image_table_body").innerHTML = image_html;
	}
	
	function cmpSelectedImageItem(item1, item2) {
		if (item1.album_name == item2.album_name && item1.filename == item2.filename)
			return true;
		else
			return false;
	}
	
	function mouseover(addr) {
		var ei = $("#large");  
		 ei.html(
			'<img style="border:1px solid gray;position:absolute;right:0px;top:0px;z-index:1;max-width:400px;" src="'+addr+'"/>').show();  
	}
	
	function mouseout(addr) {
		var ei = $("#large");  
		ei.hide();
	}
	
	function removeSelectedImage(idx) {
		var len = selected_image.length;
		for (i=0; i<len; i++) {
			var item = selected_image.shift();
			if (idx != i)
				selected_image.push(item);
		}
		displaySelectedPicture();
	}
	
	function selectImageListAll() {
		for (i=0; i<image_obj.length; i++)
			document.getElementById("checkbox_"+i).checked="checked";
	}

	function sharePictures() {
		var share_list_obj = new Array();
		for (var i = 0; i < selected_image.length; i++) {
			var item = {album_name:selected_image[i].album_name, filename:selected_image[i].filename};
			share_list_obj.push(item);
		}
		var share_list_json = JSON.stringify(share_list_obj);
		ajaxProcess(sharePicturesCallback, "sharePictures", "list_json="+share_list_json+"&group_id=${Group_id}");
		//alert(share_list_json);
	}

	function sharePicturesCallback (result) {
		if (result.indexOf('{')!=0)
			$.bootstrapGrowl("Sharing pictures failed. Error: "+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
		else {
			var result_obj = JSON.parse(result);
			var success_list_obj = result_obj.success_list;
			var failure_list_obj = result_obj.failure_list;
			var duplication_list_obj = result_obj.duplication_list;

			for (var i = 0; i < success_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl("Sharing "+success_list_obj[i]+" succeed.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
			
			for (var i = 0; i < failure_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl("Sharing "+failure_list_obj[i]+" failed.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
			for (var i = 0; i < duplication_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl(duplication_list_obj[i]+" has already existed.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
		}
	}
</script>
<style type="text/css">
.top-border{
	border-top:solid;
	border-width:1px;
	border-color:#777;
}
.list-title {
	background-color: rgba(148, 209, 179, 0.8);
	/* border-radius: 5px; */
	border: solid;
	border-bottom-style: none;
	border-width: 2px;
	border-color: #337ab7;
	border-top-left-radius:3px;
	border-top-right-radius:3px;
}
.list-panel {
	background-color: rgba(166, 216, 222, .3);
	/* border-radius: 5px; */
	border: solid;
	border-width: 2px;
	border-color: #0099cc;
	border-top-style: none;
	border-bottom-left-radius: 5px;
	border-bottom-right-radius: 5px;
}
td.top-border {
    border-top: 2px solid #ddd;
}
</style>
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
				
				<form class="navbar-form navbar-left" role="search">
		            <div class="form-group">
		                <input type="text" class="form-control" placeholder="Search your pictures">
		            </div>
		            <button type="submit" class="btn btn-default " style="font-size: 1.45em;text-shadow: #aaa 1px 2px 3px;">
		            	<span class="glyphicon glyphicon-search" style="color:#999"></span>
		            </button>
		        </form>

				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li><img class="navbar-form" src="${empty Avatar?'':Avatar}" alt="avatar" width="32" height="32"></li>
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

		<div class="container" style="min-width: 80%;width:auto;margin-left:15px;margin-right:15px;">
			<h3>Share Pictures to Album <a href="/Souvenirs/sharedAlbum?group_id=${Group_id }">${SAlbum_name }</a> <small>(Group ID: ${Group_id })</small></h3>
			<div class="row" style="margin-top:40px;">
				<div class="col-sm-6 list-title"><h4>Available</h4></div>
				<div class="col-sm-5 col-sm-offset-1 list-title">
					<h4 style="display:inline-block;">Share List </h4>
					<span class="badge" id="share_list_num" style="margin-bottom: 10px;margin-left: 5px;"></span>
					<button class="btn btn-primary btn-sm" style="float:right;margin-top: 5px;" onclick="sharePictures()">Share</button>
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
<%-- 							<c:forEach var="image_item" items="${image_json_list }" varStatus="idx">
								<tr>
									<td><input type="checkbox" value="" id="checkbox_${idx.count }"></td>+
									<td>
										<div class="media">
											<a class="pull-left" href="#"> <img class="media-object" src="" alt=""
												width="120" height="" id="thumbnail_${idx.count }">
											</a>
											<div class="media-body">
												<h4 class="media-heading" id="filename_${idx.count }"></h4>
												<p>
													Album name: <span id="album_name_${idx.count }">d</span><br> 
													Description: <span id="description_${idx.count }"></span><br>
													Uploaded Time: <span id="upload_time_${idx.count }"></span><br>
												</p>
											</div>
										</div>
									</td>
								</tr>
								
								<script>
									image_item_json = '${image_item}';
									idx = ${idx.count};
									image_item_obj = JSON.parse(image_item_json);
									document.getElementById("thumbnail_"+idx).src = image_item_obj.Addr;
									document.getElementById("thumbnail_"+idx).alt = image_item_obj.Filename;
									document.getElementById("filename_"+idx).innerHTML = image_item_obj.Filename;
									document.getElementById("album_name_"+idx).innerHTML = image_item_obj.AlbumName;
									document.getElementById("description_"+idx).innerHTML = image_item_obj.Description;
									document.getElementById("upload_time_"+idx).innerHTML = image_item_obj.UploadTime;
								</script>
							</c:forEach> --%>

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
<!--  							<tr>
								<td><input type="checkbox" value=""></td>
								<td>daily life</td>
								<td>2.jpg</td>
								<td><div id="large"></div>
									<button type="button" class="btn btn-link " style="padding: 0px" onmouseover="mouseover()" onmouseout="mouseout()">Preview</button>
								</td>

							</tr>  -->
						</tbody>
					</table>
				</div>
			</div>

		</div>
	</div>

	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>