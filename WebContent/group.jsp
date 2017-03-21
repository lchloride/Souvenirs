<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Group</title>
<link href="/Souvenirs/res/image/logo.ico" rel="icon">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>
<!-- Text Color Picker -->
<script src="/Souvenirs/res/js/iColorPicker.js" type="text/javascript"></script>
<script src="/Souvenirs/res/js/jquery.bootstrap-growl.min.js"></script>

<link href="/Souvenirs/res/css/website.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	var my_group_total_items = ${not empty My_group_total_items?My_group_total_items:0};
	var my_group_total_page = Math.ceil(my_group_total_items/10);
	var my_group_active_page = ${not empty My_group_page_number?My_group_page_number:0};
	var previous_length = 10; // Default value, updated after each change
	var my_group_list_obj; // JSON object storing group information
	var page = '${Page}';
	
	var search_result_obj;
	window.onload= function() {
		pagination('my_group_pagination', my_group_active_page, my_group_total_page);
		displayMyGroupTable('${not empty My_group_list_json?My_group_list_json:"[]"}');
		<c:if test="${not empty Error}">
			$.bootstrapGrowl("Error: "+'${Error}', { type: 'danger' , offset: {from: 'top', amount: 50}});
		</c:if>
		
		<c:if test="${not empty Success}">
			$.bootstrapGrowl("Success", { type: 'success' , offset: {from: 'top', amount: 50}});
			alert("New Group ID: ${Group_id}. \nPlease remeber it for join in.");
		</c:if>
		
		<c:if test="${not empty Group_name}">
			$('#group_name').val('${Group_name}');
		</c:if>
		
		<c:if test="${not empty Description}">
			$('#description').val('${Description}');
		</c:if>	
		
		<c:if test="${not empty SAlbum_name}">
			$('#salbum_name').val('${SAlbum_name}');
		</c:if>
	}
	
	function pagination(id, active_idx, total_page) {
		for (var i=0; i<7; i++) {
			$('#'+id+' li:eq('+i+') a').text(i+1);
			$('#'+id+' li:eq('+i+')').removeClass('active');
			$('#'+id+' li:eq('+i+')').removeClass('disabled');
			$('#'+id+' li:eq('+i+')').removeClass('active');
			$('#'+id+' li:eq('+i+')').css('display', 'inline');
			$('#'+id+' li:eq('+i+') a').css('border-top-right-radius','0px');
			$('#'+id+' li:eq('+i+') a').css('border-bottom-right-radius','0px');
		}
		if (total_page <= 7) {
			for (var i=0; i<total_page; i++) {
				$('#'+id+' li:eq('+i+') a').text(i+1);
			}
			for (var i=total_page; i<7; i++) {
				$('#'+id+' li:eq('+i+')').css('display', 'none');
			}
			$('#'+id+' li:eq('+(total_page-1)+') a').css('border-top-right-radius','4px');
			$('#'+id+' li:eq('+(total_page-1)+') a').css('border-bottom-right-radius','4px');
			$('#'+id+' li:eq('+(active_idx-1)+')').addClass('active');
		} else {
			if (active_idx >=1 && active_idx <= 4) {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('2');
				$('#'+id+' li:eq(2) a').text('3');
				$('#'+id+' li:eq(3) a').text('4');
				$('#'+id+' li:eq(4) a').text('5');
				$('#'+id+' li:eq(5) a').text('...');
				$('#'+id+' li:eq(5) a').addClass('disabled');
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq('+(active_idx-1)+')').addClass('active');
			} else if (active_idx >=total_page-3 && active_idx <= total_page) {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('...');
				$('#'+id+' li:eq(1) a').addClass('disabled');
				$('#'+id+' li:eq(2) a').text(total_page-4);
				$('#'+id+' li:eq(3) a').text(total_page-3);
				$('#'+id+' li:eq(4) a').text(total_page-2);
				$('#'+id+' li:eq(5) a').text(total_page-1);
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq('+(6-total_page+active_idx)+')').addClass('active');
			} else {
				$('#'+id+' li:eq(0) a').text('1');
				$('#'+id+' li:eq(1) a').text('...');
				$('#'+id+' li:eq(1) a').addClass('disabled');
				$('#'+id+' li:eq(2) a').text(active_idx-1);
				$('#'+id+' li:eq(3) a').text(active_idx);
				$('#'+id+' li:eq(4) a').text(active_idx+1);
				$('#'+id+' li:eq(5) a').text('...');
				$('#'+id+' li:eq(5) a').addClass('disabled');
				$('#'+id+' li:eq(6) a').text(total_page);
				$('#'+id+' li:eq(3)').addClass('active');
			}
			
			$('#'+id+' li:eq(6) a').css('border-top-right-radius','4px');
			$('#'+id+' li:eq(6) a').css('border-bottom-right-radius','4px');
		}
	}
	
	function displayMyGroupTable(json) {
		var str="";
		var my_group_list_json = json;
		my_group_list_obj = JSON.parse(my_group_list_json);
		var line = "";
		for (var i=0; i<my_group_list_obj.length; i++) {
			line = '<tr><td>'+my_group_list_obj[i].group_id+'</td><td>'+my_group_list_obj[i].group_name+'</td><td>'+my_group_list_obj[i].intro+'</td>'+
			'<td><button type="button" class="btn btn-link " style="padding: 0px" onclick="editGroup('+i+')">Edit</button> | '+
			'<button type="button" class="btn btn-link " style="padding: 0px" onclick="leaveGroup('+i+')">Leave</button> | '+
			'<button type="button" class="btn btn-link " style="padding: 0px" ><a href="/Souvenirs/sharedAlbum?group_id='+my_group_list_obj[i].group_id+'">Show Attached Album<a></button>'+
			'</td>	</tr>';
			str += line;
		}
		$('#my_group_table_body').html(str);
		
	}
	function ajaxProcess(callback, URL)
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
	  xmlhttp.open("GET",URL, true);
	  xmlhttp.send();
	}
	
	function changeMGContentLength(length) {
		ajaxProcess(changeMGContentLengthCallback, "/Souvenirs/showMyGroup?page_number="+my_group_active_page+"&content_length="+length);
	}
	
	function changeMGContentLengthCallback(result) {
		displayMyGroupTable(result);
		var current_length = document.getElementById("MG_content_length").value;
		my_group_active_page = Math.round((my_group_active_page-1) * previous_length / current_length)+1; 
		my_group_total_page = Math.ceil(my_group_total_items / current_length);
		previous_length = current_length;
		pagination('my_group_pagination', my_group_active_page, my_group_total_page);
	}
	
	function changeMGPage(page) {
		my_group_active_page = page;
		ajaxProcess(changeMGPageCallback, "/Souvenirs/showMyGroup?page_number="+page+"&content_length="+document.getElementById("MG_content_length").value);
	}
	
	function changeMGPageCallback(result) {
		displayMyGroupTable(result);
		var current_length = document.getElementById("MG_content_length").value;
		my_group_total_page = Math.ceil(my_group_total_items / current_length);
		pagination('my_group_pagination', my_group_active_page, my_group_total_page);
	}
	
	function editGroup(idx) {
		$('#edit_group_id').val(my_group_list_obj[idx].group_id );
		$('#edit_group_name').val(my_group_list_obj[idx].group_name.replace(/&apos;/g, "'"));
		$('#edit_intro').html(my_group_list_obj[idx].intro);
		$('#editModal').modal('show');
	}
	
	function submitEdit() {
		var group_id = encodeURIComponent($("#edit_group_id").val());
		var group_name = encodeURIComponent($("#edit_group_name").val());
		var intro = encodeURIComponent($("#edit_intro").val());
		ajaxProcess(editCallback, "/Souvenirs/updateGroup?group_id="+group_id+"&group_name="+group_name+"&intro="+intro)		
	}
	
	function editCallback(result) {
		$('#editModal').modal('toggle');
		var result_obj = JSON.parse(result);
		if (result_obj.length == 1) {
			if (result_obj[0].result == "no item changed") 
				$.bootstrapGrowl("No item changed.", { type: 'info' , offset: {from: 'top', amount: 50}});
			else
				$.bootstrapGrowl("Error: "+result_obj[i].item+", "+result_obj[i].result, { type: 'danger' , offset: {from: 'top', amount: 50}});
		}else {
			for (var i=0; i<result_obj.length-1; i++) {
				if (result_obj[i].result == "true") {
					$.bootstrapGrowl("Update "+result_obj[i].item+" succeeded.", { type: 'success' , offset: {from: 'top', amount: 50}});
				} else {
					$.bootstrapGrowl("Error: "+result_obj[i].item+", "+result_obj[i].result, { type: 'danger' , offset: {from: 'top', amount: 50}});
				}
			}
			displayMyGroupTable(result_obj[result_obj.length-1]);
		}
	}
	
	function leaveGroup(idx) {
		var r=confirm("Are you sure to leave this group? You will not be able to see any shared pictures after leaving.");
		if (r==true)
		{
			var group_id = encodeURIComponent(my_group_list_obj[idx].group_id);
			ajaxProcess(leaveGroupCallback, "/Souvenirs/leaveGroup?group_id="+group_id);
		}else
		{
			$.bootstrapGrowl("Operation canceled.", { type: 'info' , offset: {from: 'top', amount: 50}});
		}
	}
	
	function leaveGroupCallback(result) {
		if (result.trim() == "true") {
			ajaxProcess(changeMGPageCallback, "/Souvenirs/showMyGroup");
			$.bootstrapGrowl("Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
		}else
			$.bootstrapGrowl("Error: "+result, { type: 'danger' , offset: {from: 'top', amount: 50}});	
	}
	
	function searchGroup() {
		var keyword = $('#search_id').val();
		var method = $('#search_method').val()=="Fuzzy Search"?"true":"false";
		ajaxProcess(searchGroupCallback, "/Souvenirs/searchGroup?keyword="+keyword+"&is_fuzzy="+method);
	}
	
	function searchGroupCallback(result)  {
		displaySearchResult(result);
	}
	
	function displaySearchResult(json) {
		search_result_obj = JSON.parse(json);
		if (search_result_obj.length == 0)
			$('#search_table_body').html("No matched group is found.");
		else {
			var str = "";
			var line = "";
			for (var i=0; i<search_result_obj.length; i++) {
				line = '<tr><td>'+search_result_obj[i].group_id+'</td><td>'+search_result_obj[i].group_name+'</td><td>'+search_result_obj[i].intro+'</td>'+
				'<td><button type="button" class="btn btn-link " style="padding: 0px" onclick="joininGroup('+i+')">Join in</button> '+
				'</td>	</tr>';
				str += line;
			}
			$('#search_table_body').html(str);
		}
	}
	
	function joininGroup(idx) {
		ajaxProcess(joininGroupCallback, "/Souvenirs/joininGroup?group_id="+search_result_obj[idx].group_id);
	}
	
	function joininGroupCallback(result) {
		if (result.trim() == "true") {
			$.bootstrapGrowl("Join In Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
		} else {
			$.bootstrapGrowl(result, { type: 'danger' , offset: {from: 'top', amount: 50}});
		}
			
	}
	
	function createGroup() {
		$('#create_form').submit();		
	}
	
	function useDefaultCover() {
		if ($('#default_cover').is(':checked')) {
			$('#upload_file').attr('disabled', 'disbaled');
			$('#upload_file_btn').addClass('disabled');
		} else {
			$('#upload_file').removeAttr('disabled');
			$('#upload_file_btn').removeClass('disabled');
		}
	}
	
	function displayFilename() {
		var filepath = $('#upload_file').val();
		$('#filename_display').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
		$('#filename').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
	}
</script>
<style type="text/css">
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
					<li><a href="homepage">HomePage</a></li>
					<li class="active"><a href="#">Group</a></li>
					<li><a href="upload">Upload</a></li>
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
		<!-- Nav bar END -->
		<!--  -->
		<div style="clear: both; padding-top: 20px;">
			<div class="album-manage">
				<div class="album-manage-title">
					<h4>Available Albums</h4>
				</div>

				<div class="row" style="background-color:hsla(200, 25%, 95%, .9);margin-left:0px;margin-right:0px">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">

						<ul id="myTab" class="nav nav-tabs nav-stacked">
							<li><a href="#manage_group" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">Manage</a></li>
							<li><a href="#joinin_group" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">Join in</a></li>
							<li><a href="#create_group" data-toggle="tab" style="padding-left: 15%; font-size: 1.0em">Create Group</a></li>
						</ul>
					</div>

					<div class=" col-lg-10 col-md-10 col-sm-10 col-xs-10" >
							<div id="TabContent" class="tab-content">
								<div class="tab-pane fade" id="manage_group">
									<table class="table table-bordered table-hover">
										<caption>Available Group</caption>
										<thead>
											<tr>
												<th>Group ID</th>
												<th>Group Name</th>
												<th>Brief Description</th>
												<th>Operation</th>
											</tr>
										</thead>
										<tbody id="my_group_table_body">
										</tbody>
									</table>
									<ul class="pagination" id="my_group_pagination">
										<li ><a href="#" onclick="changeMGPage(this.innerHTML)">1</a></li>
										<li ><a href="#" onclick="changeMGPage(this.innerHTML)">...</a></li>
										<li ><a href="#" onclick="changeMGPage(this.innerHTML)">2</a></li>
										<li><a href="#" onclick="changeMGPage(this.innerHTML)">3</a></li>
										<li><a href="#" onclick="changeMGPage(this.innerHTML)">4</a></li>
										<li ><a href="#" onclick="changeMGPage(this.innerHTML)">...</a></li>
										<li><a style="" onclick="changeMGPage(this.innerHTML)">5</a></li>
										<li class="disabled">
											<a style="margin-left:5px;border-right-color:transparent;padding-right:0px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;">
												Page <input type="text" style="width:30px;height:18px;"> 
											</a>
										</li>
										<li ><a style="border-left-color:transparent;text-decoration:underline;cursor:pointer;margin-left:0px;">Jump</a></li>
										<li><a style="height:33px;">
											<select id="MG_content_length" style="height:18px;" onchange="changeMGContentLength(this.value)">
												<option>10</option>
												<option>20</option>
												<option>1</option>
											</select>
											records per page
										</a></li>
									</ul>
								</div>
								<!-- Manage Group END-->
								<!-- Join In Group -->
								<div class="tab-pane fade" id="joinin_group">
									<div >
										<div class="form-group" style="display:inline;">
											<input id="search_id" type="text" class="form-control" placeholder="Search group ID" style="display:inline;width:auto">
											<button type="button" class="btn btn-default " style="font-size: 1.45em; text-shadow: #aaa 1px 2px 3px;"onclick="searchGroup()">
												<span class="glyphicon glyphicon-search" style="color: #999"></span>
											</button>
											<select id="search_method" class="form-control" style="display:inline;width:auto;">
												<option>Fuzzy Search</option>
												<option>Exact Search</option>
											</select>
										</div>

									</div>
									<table class="table table-bordered table-hover">
										<caption>Search Result</caption>
										<thead>
											<tr>
												<th>Group ID</th>
												<th>Group Name</th>
												<th>Brief Description</th>
												<th>Operation</th>
											</tr>
										</thead>
										<tbody id="search_table_body">

										</tbody>
									</table>
							
								</div>
								<!-- Join In END -->
								<!-- Create Group -->
								
								<div class="tab-pane fade" id="create_group">
									<form id="create_form" action="createGroup" method="POST" enctype="multipart/form-data">
										<div class="form-group padding-top">
											<label for="group_name" class="col-sm-3 control-label narrow-grid">Group Name</label>
											<div class="col-sm-9 narrow-grid">
												<input type="text" class="form-control" id="group_name" name="group_name" placeholder="Input group name">
											</div>
										</div>
										<div style="clear:both;"></div>
										<div class="form-group padding-top">
											<label for="description" class="col-sm-3 control-label narrow-grid">Description</label>
											<div class="col-sm-9 narrow-grid">
												<textarea class="form-control" rows="3" id="description" name="description" placeholder="Enter description, no more than 200 letters"></textarea>
											</div>
										</div>
										<div style="clear:both;"></div>
										<div class="form-group padding-top">
											<label for="salbum_name" class="col-sm-3 control-label narrow-grid">Attached Shared Album Name</label>
											<div class="col-sm-9 narrow-grid">
												<input type="text" class="form-control" id="salbum_name" name="salbum_name" placeholder="Input shared album name">
											</div>
										</div>
										<div style="clear:both;"></div>
										<div class="form-group padding-top">
											<label for="upload_file" class="col-sm-3 control-label narrow-grid">Cover</label>
											<div class="col-sm-9 narrow-grid">
												<input type="text" class="form-control" id="filename_display" style="width:auto;display:inline;" disabled>
												<label class="btn btn-sm btn-default " for="upload_file" id="upload_file_btn" >Choose
													Local Image</label>
												<input type="file" name="upload_file" id="upload_file" style="position: absolute; clip: rect(0, 0, 0, 0); display: inline"
														onchange="displayFilename()" accept="image/jpg">
												 <input type="hidden" class="form-control" id="filename" name="filename" style="display:none;" >
											</div>
										</div>
										
										<div style="clear:both;"></div>
										<div class="form-group padding-top">
											<label for="default_cover" class="col-sm-3 control-label narrow-grid"></label>
											<div class="col-sm-9 narrow-grid">
												<input type="checkbox" id="default_cover" name="default_cover"onclick="useDefaultCover()">Use Default Cover
												 
											</div>
										</div>
										<div style="clear:both;"></div>
										<button class="btn btn-primary" onclick="createGroup()">Create</button>
									</form>
								</div>
								
								<!-- Create Group END -->
								<script>
									$(function(e) {
										if (page == "create")
											$('#myTab li:eq(2) a').tab('show');
										else if (page == "join")
											$('#myTab li:eq(1) a').tab('show');
										else
											$('#myTab li:eq(0) a').tab('show');
										
									});
									$(function(){
										$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
											// 获取已激活的标签页的名称
											var activeTab = $(e.target).text(); 
											if (activeTab == "Manage")
												ajaxProcess(changeMGPageCallback, "/Souvenirs/showMyGroup");
										});
									});
								</script>

							</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Mainbody END -->
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
	
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	                <h4 class="modal-title" id="myModalLabel">Edit Group</h4>
	            </div>
	            <div class="modal-body">
	            	<form id="editGroup" class="form-horizontal" role="form" action="updateGroup" methoid ="GET">
					  <div class="form-group">
					    <label for="edit_group_id" class="col-sm-2 control-label">ID</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control " id="edit_group_id" placeholder="Group ID"  disabled>
					      <input type="hidden" class="form-control " id="edit_group_id" placeholder=""  >
					    </div>
					  </div>
					  <div class="form-group">
					    <label for="edit_group_name" class="col-sm-2 control-label">Name</label>
					    <div class="col-sm-10">
					      <input type="text" class="form-control" id="edit_group_name" placeholder="Group Name">
					    </div>
					  </div>
					  <div class="form-group">
					  	<label for="edit_group_name" class="col-sm-2 control-label">Introduction</label>
					    <div class="col-sm-10">
							<textarea class="form-control" rows="3" id="edit_intro"></textarea>
					    </div>
					  </div>
					</form>
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                <button type="button" class="btn btn-primary" onclick="submitEdit()">提交更改</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	<script>
	$(function() {
	    $('#myModal').modal({
	        keyboard: true
	    })
	});
	</script>
</body>
</html>