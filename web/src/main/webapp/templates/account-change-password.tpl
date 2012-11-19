			<section id="account-change-password">
				<div class="page-header">
					<h1>Change password</h1>
				</div>
				
				<div class="well">
					<input type="password" name='password' placeholder="Enter your new password" />
					<input type="password" name='confirmPassword' placeholder="Confirm your new password" />
					<a class="btn btn-warning" style="margin-bottom: 9px;" id='changePassword'>Change password</a>
					<div class="alert alert-error" id='wrongPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">Passwords don't match!</div>
					<div class="alert alert-error" id='weakPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">Passwords is too weak! It should be at least 8 characters long, containing a number, a symbol and both capital and normal letters.</div>
					<div class="alert alert-error" id='unkownError' style="margin-top: 10px; margin-bottom: 5px; display: none;">Whoops! Something went wrong</div>
					<div class="alert alert-success" id='passwordUpdated' style="margin-top: 10px; margin-bottom: 5px; display: none;">Password updated!</div>
				</div>
			</section>