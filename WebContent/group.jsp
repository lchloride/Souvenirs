<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	var my_group_total_page = 1//{My_page_total_page};
	var my_group_active_page = 1//{My_group_active_group};
	window.onload= function() {
		pagination('my_group_pagination', my_group_active_page, my_group_total_page);
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
						<form>
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
											<tr>
												<td>Tanmay</td>
												<td>Bangalore</td>
												<td>560001</td>
												<td><button type="button" class="btn btn-link " style="padding: 0px" >Edit</button> | 
														<button type="button" class="btn btn-link " style="padding: 0px" >Leave</button> |
														<button type="button" class="btn btn-link " style="padding: 0px" >Show Attached Album</button>
												</td>
											</tr>
											<tr>
												<td>Sachin</td>
												<td>Mumbai</td>
												<td>400003</td>
											</tr>
											<tr>
												<td>Uma</td>
												<td>Pune</td>
												<td>411027</td>
											</tr>
										</tbody>
									</table>
									<ul class="pagination" id="my_group_pagination">
										<li ><a href="#" >1</a></li>
										<li ><a href="#">...</a></li>
										<li ><a href="#">2</a></li>
										<li><a href="#">3</a></li>
										<li><a href="#">4</a></li>
										<li ><a href="#">...</a></li>
										<li><a style="">5</a></li>
										<li class="disabled">
											<a style="margin-left:5px;border-right-color:transparent;padding-right:0px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;">
												Page <input type="text" style="width:30px;height:18px;"> 
											</a>
										</li>
										<li ><a style="border-left-color:transparent;text-decoration:underline;cursor:pointer;margin-left:0px;">Jump</a></li>
										<li><a style="height:33px;">
											<select style="height:18px;">
												<option>10</option>
												<option>20</option>
												<option>50</option>
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
											<input type="text" class="form-control" placeholder="Search group ID" style="display:inline;width:auto">
										</div>
										<button type="submit" class="btn btn-default " style="font-size: 1.45em; text-shadow: #aaa 1px 2px 3px;">
											<span class="glyphicon glyphicon-search" style="color: #999"></span>
										</button>
									</div>
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
											<tr>
												<td>Tanmay</td>
												<td>Bangalore</td>
												<td>560001</td>
												<td><button type="button" class="btn btn-link " style="padding: 0px" >Join In</button>
												</td>
											</tr>
											<tr>
												<td>Sachin</td>
												<td>Mumbai</td>
												<td>400003</td>
											</tr>
											<tr>
												<td>Uma</td>
												<td>Pune</td>
												<td>411027</td>
											</tr>
										</tbody>
									</table>
									<ul class="pagination" id="my_group_pagination">
										<li ><a href="#" >1</a></li>
										<li ><a href="#">...</a></li>
										<li ><a href="#">2</a></li>
										<li><a href="#">3</a></li>
										<li><a href="#">4</a></li>
										<li ><a href="#">...</a></li>
										<li><a style="">5</a></li>
										<li class="disabled">
											<a style="margin-left:5px;border-right-color:transparent;padding-right:0px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;">
												Page <input type="text" style="width:30px;height:18px;"> 
											</a>
										</li>
										<li ><a style="border-left-color:transparent;text-decoration:underline;cursor:point;">Jump</a></li>
										<li><a style="height:33px;">
											<select style="height:18px;">
												<option>10</option>
												<option>20</option>
												<option>50</option>
											</select>
											records per page
										</a></li>
									</ul>								
								</div>
								<!-- Join In END -->
								<!-- Create Group -->
								<div class="tab-pane fade" id="create_group">
									<form>
										<div class="form-group padding-top">
											<label for="album_name" class="col-sm-3 control-label narrow-grid">Group Name</label>
											<div class="col-sm-9 narrow-grid">
												<input type="text" class="form-control" id="album_name" name="album_name" placeholder="Input album name">
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
											<label for="album_name" class="col-sm-3 control-label narrow-grid">Attached Shared Album Name</label>
											<div class="col-sm-9 narrow-grid">
												<input type="text" class="form-control" id="album_name" name="album_name" placeholder="Input album name">
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
									</form>
								</div>
								<!-- Create Group END -->
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

	<!-- Mainbody END -->
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All Rights Reserved.</div>
</body>
</html>