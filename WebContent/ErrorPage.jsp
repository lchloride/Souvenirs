<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error Page</title>
<style type="text/css">
body {
	background-color:	#AFEEEE;
}
.mainbody {
	position:absolute;
	left:50%;
	top:50%;
	margin-left:-325px;
	margin-top:-185px;
	width:650px;
	height:350px;
	font-family: "Microsoft YaHei UI", Arial, sans-serif;
	background-color:rgba(250,250,250,.7);
	padding:10px;
}

.left-side {
	position:absolute;
	left:50%;
	top:50%;
	margin-left:-325px;
	margin-top:-175px;
	width: 300px;
	height:350px;
	
}

.right-side {
	position:absolute;
	left:50%;
	top:50%;
	margin-left:-15px;
	margin-top:-175px;
	width:340px;
	height:350px;
}

.right-side img {
	border-radius: 10px;
	width:100%;
	height: 100%;
}

.error-content {
	position:absolute;
	left:0%;
	top:50%;
	margin-left:-0px;
	margin-top:-25px;
	width:290px;
	height:125px;
	border-style:solid;
	border-width:1px;
	border-radius:5px;
	background-color:#F0E68C;
}

.link-content {
	position:absolute;
	left:0%;
	top:50%;
	margin-left:-0px;
	margin-top:125px;
	width:300px;
	height:50px;	
}

.error-code {
	position:absolute;
	left:0%;
/* 	top:50%; */
	margin-left: 10px;
/* 	margin-top:-25px; */
	width: 70px;
	height:125px;
	
	text-align:center;
	font-weight:bold;
	font-style:italic;
}

.error-code span {
	position:absolute;
	top:50%;
	left:0%;
	margin-top:-30px;
	font-size:1.2em;
}
.error-des {
	position:absolute;
	left:0%;
/* 	top:50%; */
	margin-left: 90px;
/* 	margin-top:-25px; */
	width: 200px;
	height:125px;
	font-weight:bold;
}

.error-des span {
	position:absolute;
	top:50%;
	left:0%;
	margin-top:-30px;
}

.footer {
	position: fixed;
	bottom: 0px;
	right: 0px;
	width: 100%;
	height: 20px;
	background-color: rgba(69, 69, 69, .5);
	color: #fff;
	text-align: center;
}
</style>
</head>
<body>
	<div class="mainbody">
		<div class="left-side">
			<div class="sorry">
				<img src="/Souvenirs/res/image/go_wrong.png" alt="Something went wrong">
			</div>
			<div class="error-content">
				<span class="error-code"><span>Error (${ErrorCode })</span></span>
				<span class="error-des"><span>${Description }</span></span>
			</div>
			<div class="link-content">
				<div class="link"><a href="index.jsp">Go to homepage</a></div>
				<div class="link"><a id="lastpage" href="${LastPage }">Back to previous page</a></div>
			</div>
		</div>
		<div class="right-side">
			<img src="${DesImage }" alt="Error Description Image">
		</div>
	</div>
	
	<div class="footer">Copyright &copy; 2016-2017 Souvenirs, All
			Rights Reserved.
	</div>
	
</body>
</html>