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
											<c:foreach var="groups_row" items="${_json_list }" varStatus="idx">
												<tr>
													<c:foreach>
														<td></td>
													</c:foreach>
												</tr>
											</c:foreach>
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
									<ul class="pagination">
										<li class="active"><a href="#">1</a></li>
										<li class="disabled"><a href="#">...</a></li>
										<li ><a href="#">2</a></li>
										<li><a href="#">3</a></li>
										<li><a href="#">4</a></li>
										<li  class="disabled"><a href="#">...</a></li>
										<li><a style="border-top-right-radius: 4px;border-bottom-right-radius: 4px;margin-right:5px;">5</a></li>
										<li class="disabled">
											<a style="border-right-color:transparent;padding-right:0px;border-top-left-radius: 4px;border-bottom-left-radius: 4px;">
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

								<div class="tab-pane fade" id="joinin_group"></div>

								<div class="tab-pane fade" id="create_group"></div>
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