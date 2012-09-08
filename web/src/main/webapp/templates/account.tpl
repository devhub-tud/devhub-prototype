#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		<h1>$user.getDisplayName()</h1>
		<div style="float: left; width: 100px;">
			<a href="http://gravatar.com"> <img
				src="http://www.gravatar.com/avatar/e73e47d53aa7d653abba096280c1f610">
			</a>
		</div>
		<div style="overflow: hidden;">
			<table class="table table-condensed">
				<tbody>
					<tr>
						<td>Mail</td>
						<td>test@tudelft.nl</td>
					</tr>
					<tr>
						<td>NetId</td>
						<td>test-admin</td>
					</tr>
					<tr>
						<td>Student number</td>
						<td>0</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="well">
			<h2>Change password</h2>
			<input type="password" placeholder="Enter your new password">
			<input type="password" placeholder="Confirm your new password">
			<a class="btn btn-warning" style="margin-bottom: 9px;">Change
				password</a>
			<div class="alert alert-error"
				style="margin-top: 10px; margin-bottom: 5px; display: none;">Passwords
				don't match!</div>
		</div>
	</div>
</div>

#parse("footer.tpl")
