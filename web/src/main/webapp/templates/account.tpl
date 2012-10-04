#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		<div style="height: 96px;">
			<img style="float: left;" src="${account.getGravatarUrl(96)}" />
			<div style="float: left; margin-left: 24px;">
				<h1>$account.getDisplayName()</h1>
				<h5>$account.getRole().getDisplayName()</h5>
			</div>
		</div>
		<table class="table table-condensed" style="margin-top: 12px;">
			<tbody>
				<tr>
					<td>Email</td>
					<td>${account.getEmail()}</td>
				</tr>
				<tr>
					<td>Net ID</td>
					<td>${account.getNetId()}</td>
				</tr>
				<tr>
					<td>Student number</td>
					<td>${account.getStudentNumber()}</td>
				</tr>
			</tbody>
		</table>
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
