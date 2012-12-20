<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Activate</title>
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
		<h1>Activate account</h1>
		<div class="alerts hide" style="margin-top: 20px;"></div>
		<p style="margin-top: 20px;">You're just one step away from completing your registration.</p>
		<div style="margin-top: 20px;">
			<form id="activate" class="form-inline">
				<div class="control-group">
					<label for="email">E-mail address</label>
					<div class="controls">
						<input type="text" name="email" value="$email" disabled/>
					</div>
				</div>
				<div class="control-group">
					<label for="net-id">Net ID</label>
					<div class="controls">
						<input type="text" name="net-id" placeholder="Net ID"/>
					</div>
				</div>
	#if ($isStudent)			
				<div class="control-group">
					<label for="student-number">Student number</label>
					<div class="controls">
						<input type="text" name="student-number" placeholder="Student number" />
					</div>
				</div>
	#end
				<div class="control-group">
					<label for="name">Your name</label>
					<div class="controls">
						<input type="text" name="name" placeholder="Your name" />
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
				<div id='pwError' class="alert alert-error" style="display: none;">Passwords don't match or aren't strong enough.</div>
				<div class="control-group" style="margin-top: 32px;">
					<input class="btn btn-primary" name="submit" type="submit" value="Complete registration" />
				</div>
			</form>
		</div>
	</div>
	<script src="http://code.jquery.com/jquery-1.8.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.1.0/js/bootstrap.min.js"></script>
	<script src="/js/common.js"></script>
	<script src="/js/activate.js"></script>
</body>
</html>