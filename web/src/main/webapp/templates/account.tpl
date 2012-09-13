#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		<h1>$user.getDisplayName()</h1>
		<!-- (GR)Avatar hidden as long as hashing does not work. -->
		<!-- 		<div style="float: left; width: 100px;"> -->
		<!-- 			<a href="http://gravatar.com"> -->
		<!-- 		<img src="http://www.gravatar.com/avatar/e73e47d53aa7d653abba096280c1f610"> -->
		<!-- 			</a> -->
		<!-- 		</div> -->
		<div style="overflow: hidden;">
			<table class="table table-condensed">
				<tbody>
					<tr>
						<td>Mail</td>
						<td>${user.email}</td>
					</tr>
					<tr>
						<td>NetId</td>
						<td>${user.netid}</td>
					</tr>
					<tr>
						<td>Student number</td>
						<td>${user.studentNumber}</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="well">
			<h2>Change password</h2>
			<input type="password" name='password' placeholder="Enter your new password" />
			<input type="password" name='confirmPassword' placeholder="Confirm your new password" />
			<a class="btn btn-warning" style="margin-bottom: 9px;" id='changePassword'>Change password</a>
			<div class="alert alert-error" id='wrongPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">
				Passwords don't match!</div>
			<div class="alert alert-error" id='weakPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">
				Passwords is too weak! It should be at least 8 characters long, containing a number, a symbol and both
				capital and normal letters.</div>
			<div class="alert alert-error" id='unkownError' style="margin-top: 10px; margin-bottom: 5px; display: none;">
				Whoops! Something went wrong</div>
			<div class="alert alert-success" id='passwordUpdated' style="margin-top: 10px; margin-bottom: 5px; display: none;">
				Password updated!</div>
		</div>
	</div>
</div>
#parse("footer.tpl")
