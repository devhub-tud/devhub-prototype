<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>DevHub (BETA) - Login</title>
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/css/bootstrap-combined.min.css" rel="stylesheet">
<link rel="shortcut icon" href="/img/favicon.ico">
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
	<div class="hero-unit">
		<h1>DevHub <span class="muted" style="font-size: 18px;">BETA</span></h1>
		<div style="margin-top: 20px; height: 260px;">
			<div class="alerts" style="height: 60px;"></div>
			<div class="pull-left" style="width: 334px; border-right: 1px solid #999;">
				<p>Sign in with your TU Delft email</p>
				<form id="signin" class="form-inline">
					<div class="control-group">
						<div class="controls">
							<input class="input-xlarge" type="text" name="email" placeholder="john@tudelft.nl" />
						</div>
					</div>
					<div class="control-group">
						<div class="controls">
							<input class="input-xlarge" type="password" name="password" placeholder="Password" />
						</div>
					</div>
					<div class="control-group">
						<div class="controls">
							<label class="checkbox"><input type="checkbox" name="rememberMe" /> Remember me on this computer</label>
						</div>
					</div>
					<div class="control-group">
						<input class="btn btn-primary" name="submit" type="submit" value="Sign in" />
					</div>
				</form>
				<a href="/forgot-password">Forgot your password?</a>
			</div>
			<div class="pull-right" style="width: 284px;">
				<p>Sign up with your TU Delft email</p>
				<form id="signup" class="form-inline">
					<div class="control-group">
						<div class="controls">
							<input class="input-xlarge" type="text" name="email" placeholder="john@tudelft.nl" />
						</div>
					</div>
					<div class="control-group">
						<input class="btn btn-primary" name="submit" type="submit" value="Sign up" />
					</div>
				</form>
			</div>
		</div>
	</div>
	<footer>${buildInfo.getVersion()} @ ${buildInfo.getDate()}</footer>
	<script src="http://code.jquery.com/jquery-1.8.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/js/bootstrap.min.js"></script>
	<script src="/js/login.js"></script>
</body>
</html>