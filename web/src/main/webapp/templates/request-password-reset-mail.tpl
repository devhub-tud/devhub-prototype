<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Login</title>
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/css/bootstrap-combined.min.css" rel="stylesheet">
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
		<h1>DevHub</h1>
		<div style="margin-top: 20px; height: 200px;">
			<div class="pull-left" style="width: 334px">
				<p>Recover access to your account</p>
				<p>Enter your TU Delft email:</p>
				<form id="recover" class="form-inline">
					<div class="control-group">
						<div class="controls">
							<input class="input-xlarge" type="text" name="email" placeholder="j.doe@student.tudelft.nl" />
						</div>
					</div>
					<div class="control-group">
						<input class="btn btn-primary" name="submit" type="submit" value="Reset password" />
					</div>
				</form>
			</div>
		</div>
		<div class="alerts" style="margin-top: 10px;"></div>
	</div>
	<script src="http://code.jquery.com/jquery-1.8.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/js/bootstrap.min.js"></script>
	<script src="/js/request-password-reset-mail.js"></script>
</body>
</html>
