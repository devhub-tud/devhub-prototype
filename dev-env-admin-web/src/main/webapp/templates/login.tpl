<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Login</title>
<link
	href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/css/bootstrap-combined.min.css"
	rel="stylesheet">
<style>
.hero-unit {
	width: 666px;
	margin: 80px auto 25px auto;
}

.logintable .span3 {
	height: 30px;
}

footer {
	text-align: center;
	color: #ccc;
}
</style>
</head>
<body>
	<div class="hero-unit" style="height: 280px;">
		<h1>DevHub</h1>
		<div class="pull-left" style="width: 49%; border-right: 1px solid #999;">
			<p>Login with TU Delft e-mail and password</p>
			<form id="login" method='POST'>
				<table class='logintable'>
					<tr>
						<td><input class='span3' type='text' name='email'
							placeholder='E-mail address'></td>
					</tr>
					<tr>
						<td><input class='span3' type='password' name='password'
							placeholder='Password' /></td>
					</tr>
					<tr>
						<td><label class="checkbox"><input type='checkbox'
								name='rememberMe' /> Remember me on this computer. </label></td>
					</tr>
					<tr>
						<td colspan='2'><input class="btn btn-primary btn-large"
							name="submit" type="submit" value="Log me in" /></td>
					</tr>
				</table>
			</form>
		</div>
		<div class="pull-right" style="width: 49%;">
			<p>Sign-up with your TU Delft e-mail</p>
			<form id="register" method='POST'>
				<table class='logintable'>
					<tr>
						<td><input class='span3' type='text' name='email'
							placeholder='E-mail address'></td>
					</tr>
					<tr>
						<td colspan='2'><input class="btn btn-primary btn-large"
							name="submit" type="submit" value="Register" /></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div id="successful-registration" class="hide">
		Hello world...
	</div>
	<script src="http://code.jquery.com/jquery-1.8.0.min.js"></script>
	<script src="/js/login.js"></script>
</body>
</html>