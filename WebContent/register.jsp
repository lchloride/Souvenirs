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

<link href="/Souvenirs/res/css/website.css" rel="stylesheet"
	type="text/css">
<link href="/Souvenirs/res/css/register.css" rel="stylesheet"
	type="text/css">
<!-- <script src="/Souvenirs/res/js/register.js"> </script> -->
<title>Register</title>
<style type="text/css"></style>
<script type="text/javascript">
	function pwdstrength() {
		var LvTxt = [
				'<span class=\"bar\" style=\"background-color:red\">Invalid Password!</span>',
				'<span class=\"bar\" style=\"background-color:red\">Strength: Low</span>',
				'<span class=\"bar\" style=\"background-color:orange\">Strength: Moderate</span>',
				'<span class=\"bar\" style=\"background-color:green\">Strength: High</span>' ];
		var lv = 0;
		var val = document.getElementById("password").value;
		var score = gCheckPassword(val);
		if (score < 4)
			lv = 0;
		else if (score <= 12)
			lv = 1;
		else if (score <= 20)
			lv = 2;
		else
			lv = 3;
		document.getElementById("pwdstrength").innerHTML = LvTxt[lv];
		return LvTxt[lv];
	}

	function checkPwdConfirm() {
		var pwd = document.getElementById("password").value;
		var pwd_confirm = document.getElementById("password_confirm").value;
		if (pwd_confirm && pwd == pwd_confirm)
			document.getElementById("pwdcheck").innerHTML = "<span class=\"bar\" style=\"background-color:green\">Confirmation Passed!</span>";
		else
			document.getElementById("pwdcheck").innerHTML = "<span class=\"bar\" style=\"background-color:red\">Confirmation Failed!</span>";
		return (pwd_confirm && pwd == pwd_confirm);
	}

	function gCheckPassword(password) {
		var _score = 0;
		if (!password) {
			return 0
		}
		if (password.length < 6) {
			return 0;
		} else {
			if (password.length >= 6 && password.length <= 15) {
				_score += 2 * (password.length - 6 + 1);
			} else {
				if (password.length > 15) {
					_score += 1 * (password.length - 15) + (2 * (15 - 6 + 1));
				}
			}
		}

		var _UpperCount = (password.match(/[A-Z]/g) || []).length;
		var _LowerCount = (password.match(/[a-z]/g) || []).length;
		var _NumberCount = (password.match(/[\d]/g, "") || []).length;
		var _CharacterCount = (password.match(/[!@#$%^&*?_\.\-~]/g) || []).length;
		if (_UpperCount)
			_score += 2;
		if (_LowerCount)
			_score += 2;
		if (_NumberCount)
			_score += 2;
		if (_CharacterCount)
			_score += 2;
		return _score;
	}

	function checkUsername() {
		document.getElementById("username_check").innerHTML = "";
		return true;
	}

	function checkSubmit() {
		if (!document.getElementById("username").value) {
			alert("Username cannot be empty!");
			return false;
		}
		if (!document.getElementById("verifycode").value) {
			alert("Verify Code cannot be empty!");
			return false;
		}
		if (!checkUsername()) {
			alert("Username exists!");
			return false;
		}
		if (gCheckPassword(document.getElementById("password").value) < 4) {
			alert("Invalid Password!");
			return false;
		}
		if (!checkPwdConfirm()) {
			alert("Comfirmation failed!");
			return false;
		}

		return true;
	}
</script>
</head>
<body>
	<div class="mainbody">
		<!-- Nav bar on the top of the screen -->
		<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Souvenirs</a>
			</div>
			<div>
				<ul class="nav navbar-nav">
					<li class="disabled"><a href="#">HomePage</a></li>
					<li class="disabled"><a href="#">Group</a></li>
				</ul>


				<ul class="nav navbar-nav navbar-right" style="padding-right: 5%">
					<li class="active"><a href="#"><span class="glyphicon glyphicon-user"></span>
							Register</a></li>
					<li ><a href="index.jsp"><span
							class="glyphicon glyphicon-log-in"></span> Login</a></li>
				</ul>


			</div>
		</div>
		</nav>

		<div class="register">
			<h2 class="title">Souvenirs Register Page</h2>
			<br />
			<form class="form-horizontal" role="form"
				onsubmit="return checkSubmit()">
				<div class="form-group">
					<label for="username" class="col-sm-2 control-label">Username</label>
					<div class="col-sm-7">
						<input type="text" class="form-control" id="username"
							placeholder="3~50 English letters/numbers ONLY">
					</div>
					<div class="col-sm-3 control-label ">
						<span class="bar" style="background-color: red; padding: 0px;"
							id="username_check"></span>
					</div>
				</div>
				<div class="form-group">
					<label for="password" class="col-sm-2 control-label">Password</label>
					<div class="col-sm-7">
						<input type="password" class="form-control" id="password"
							placeholder="6~30 letters/numbers/symbols"
							onkeyup="pwdstrength()">
					</div>
					<div class="col-sm-3 control-label" id="pwdstrength"></div>
				</div>
				<div class="form-group">
					<label for="password_confirm" class="col-sm-2 control-label">Confirm
						Password</label>
					<div class="col-sm-7">
						<input type="password" class="form-control" id="password_confirm"
							placeholder="Input the password above again"
							onchange="checkPwdConfirm()">
					</div>
					<div class="col-sm-3 control-label" id="pwdcheck"></div>
				</div>
				<div class="form-group">
					<label for="password_confirm" class="col-sm-2 control-label">Verify
						Code</label>
					<div class="col-sm-7">
						<input type="text" class="form-control" id="verifycode"
							placeholder="Case insensitive">
					</div>
					<div class="col-sm-3 control-label" id="verifycode_img">
						<img class="bar"
							src="/Souvenirs/res/image/verifycode/verifycode1.png" />
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-primary">Register</button>
					</div>
				</div>
			</form>
		</div>

	</div>

	<div class="footer">Copyright &copy; 2016 Souvenirs, All Rights
		Reserved.</div>
</body>
</html>