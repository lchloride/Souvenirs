<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh_CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="/Souvenirs/res/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="/Souvenirs/res/bootstrap/js/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="/Souvenirs/res/bootstrap/js/bootstrap.min.js"></script>

<!-- <link href="/Souvenirs/res/css/register.css" rel="stylesheet"
	type="text/css"> -->
<title>Register</title>
<style type="text/css">
body {
	background: #2d3b36 url(/Souvenirs/res/image/bg.jpg) no-repeat bottom
		right fixed;
	background-size: cover
}

@media ( min-width :768px) {
	.mainbody {
		margin-top: auto;
		margin-bottom: 50px;
		margin-right: 10%;
		margin-left: 10%;
		font-family: Tahoma, "Microsoft YaHei", sans-serif;
		background: #F0FFFF;
		padding-bottom: 50px;
	}
}

@media ( max-width : 767px) {
	.mainbody {
		margin-top: auto;
		margin-bottom: auto;
		margin-right: 5px;
		margin-left: 5px;
		font-family: Tahoma, "Microsoft YaHei", sans-serif;
		background: #F0FFFF;
		padding-bottom: 50px;
	}
}

div.register {
	margin-left: 5%;
	margin-right: 5%;
	margin-top: 50px;
}

.title {
	border-bottom-style: solid;
	border-width: 2px;
	padding-bottom: 10px;
	border-bottom-color: rgba(80, 80, 80, .8)
}

.bar {
	/* padding-left:5px;
	padding-right:5px;
	 */padding:5px;
	/* border-style:solid; */
}
</style>
<script type="text/javascript">
function pwdstrength() {
	var LvTxt = ['<span class=\"bar\" style=\"background-color:red;color:white\">Invalid Password!</span>',
	              '<span class=\"bar\" style=\"background-color:red\">Strength: Low</span>',
	              '<span class=\"bar\" style=\"background-color:yellow\">Strength: Moderate</span>',
	              '<span class=\"bar\" style=\"background-color:green\">Strength: High</span>']; 
	var lv = 0; 
	var val = document.getElementById("password").value;
/* 	if(val.match(/[a-z]/g)){lv++;} 
	if(val.match(/[0-9]/g)){lv++;} 
	if(val.match(/(.[^a-z0-9])/g)){lv++;} 
	if(val.length < 6 || val.length > 30){lv=0;}
	if (lv>3) lv=3;*/
	var score=gCheckPassword(val);
	if (score <= 5)
		lv = 0;
	else if (score < 20)
		lv = 1;
	else if (score < 60)
		lv = 2;
	else
		lv = 3;
	document.getElementById("pwdstrength").innerHTML = LvTxt[lv];
	return LvTxt[lv];
}
function gCheckPassword(password) {
    var _score = 0;
    if (!password) {
        return 0
    }
    if (password.length <= 6) {
        _score += 0
    } else {
        if (password.length >= 6 && password.length <= 8) {
            _score += 10
        } else {
            if (password.length >= 8) {
                _score += 25
            }
        }
    }
    var _UpperCount = (password.match(/[A-Z]/g) || []).length;
    var _LowerCount = (password.match(/[a-z]/g) || []).length;
    var _LowerUpperCount = _UpperCount + _LowerCount;
    if (_UpperCount && _LowerCount) {
        _score += 20
    } else {
        if (_UpperCount || _LowerCount) {
            _score += 10
        }
    }
    var _NumberCount = (password.match(/[\d]/g, "") || []).length;
    if (_NumberCount > 0 && _NumberCount <= 2) {
        _score += 10
    } else {
        if (_NumberCount >= 3) {
            _score += 20
        }
    }
    var _CharacterCount = (password.match(/[!@#$%^&*?_\.\-~]/g) || []).length;
    if (_CharacterCount == 1) {
        _score += 10
    } else {
        if (_CharacterCount > 1) {
            _score += 25
        }
    }
    if (_NumberCount && (_UpperCount && _LowerCount)
            && _CharacterCount) {
        _score += 5
    } else {
        if (_NumberCount && _LowerUpperCount && _CharacterCount) {
            _score += 3
        } else {
            if (_NumberCount && _LowerUpperCount) {
                _score += 2
            }
        }
    }
    return _score
}
</script>
</head>
<body>
	<div class="mainbody">
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li class="active"><a href="index.jsp">HomePage</a></li>
					<li><a href="#">Group</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">

					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">${sessionScope.username }"example" <b
							class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li><a href="#">Account</a></li>
							<li class="divider"></li>
							<li><a href="/AnimeHelper/en_US/logout">Logout</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
		</nav>

		<div class="register">
			<h2 class="title">Souvenirs Register Page</h2>

			<form class="form-horizontal" role="form">
				<div class="form-group">
					<label for="firstname" class="col-sm-2 control-label">Username</label>
					<div class="col-sm-7">
						<input type="text" class="form-control" id="username"
							placeholder="3~50 English letters/numbers ONLY">
					</div>
					<div class="col-sm-3 control-label">${isUsernameValid}Already Exist!</div>
				</div>
				<div class="form-group">
					<label for="lastname" class="col-sm-2 control-label">Password</label>
					<div class="col-sm-7">
						<input type="password" class="form-control" id="password"
							placeholder="6~30 letters/numbers/symbols" onkeypress="pwdstrength()">
					</div>
					<div class="col-sm-3 control-label" id="pwdstrength">
						<script>
							document.getElementById("pwdstrength").innerHTML = window
									.pwdstrength();
						</script>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<div class="checkbox">
							<label> <input type="checkbox">请记住我
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-default">登录</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>