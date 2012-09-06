<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Reset account password</title>
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/css/bootstrap-combined.min.css" rel="stylesheet">
<style>
.hero-unit {
	width: 480px;
	margin: 80px auto 25px auto;
}

input {
	width: 460px;
}

input[name="email"] {
	cursor: default;
}

</style>
</head>
<body>
	<div class="hero-unit">
		<h1>Reset account password</h1>
		<div class="alerts" style="margin-top: 10px;"></div>
		<div style="margin-top: 20px;">
			<form id="reset" class="form-inline">
				<div class="control-group" hidden="true">
					<label for="id">Your account ID</label>
					<div class="controls">
						<input type="text" name="id" value="$id" disabled />
					</div>
				</div>
				<div class="control-group" hidden="true">
					<label for="token">One-time password reset token</label>
					<div class="controls">
						<input type="text" name="token" value="$token" disabled />
					</div>
				</div>
				<div class="control-group">
					<label for="email">E-mail address</label>
					<div class="controls">
						<input type="text" name="email" value="$email" disabled />
					</div>
				</div>
				<div class="control-group">
					<label for="name">Your name</label>
					<div class="controls">
						<input type="text" name="name" value="$displayName" disabled />
					</div>
				</div>
				<div class="control-group">
					<label for="password1">Desired password</label>
					<div class="controls">
						<input type="password" name="password1" placeholder="Password" />
					</div>
				</div>
				<div class="control-group">
					<label for="password2">Repeat password</label>
					<div class="controls">
						<input type="password" name="password2" placeholder="Repeat password" />
					</div>
				</div>
				<div class="control-group" style="margin-top: 32px;">
					<input class="btn btn-primary" name="submit" type="submit" value="Reset password" />
				</div>
			</form>
		</div>
	</div>
	<script src="http://code.jquery.com/jquery-1.8.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/js/bootstrap.min.js"></script>
	<script src="/js/reset-account-password.js"></script>
</body>
</html>
