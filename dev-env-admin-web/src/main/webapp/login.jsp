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
	<div class="hero-unit">
		<h1>DevHub</h1>
		<p>Login with TU Delft e-mail and password</p>

		<form method='POST'>
			<table class='logintable'>
				<tr>
					<td><input class='span3' type='text' name='username'
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
						name="submit" type="submit" value="Log me in"/></td>
				</tr>
			</table>
		</form>
		<%
			String errorDescription = (String) request.getAttribute("shiroLoginFailure");
			if (errorDescription != null) {
		%>
		<div class="alert alert-error">
			<p>Username or password was incorrect</p>
		</div>
		<%
			}
		%>
	</div>
	<%
		String version = getServletContext().getInitParameter("version");
		if (version.contains("SNAPSHOT")) {
			version = version + " @ " + getServletContext().getInitParameter("build");
		}
	%>
	<footer>
		Version
		<%=version%></footer>
</body>
</html>