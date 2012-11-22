			<section id="account-change-password">
				<div class="page-header">
					<h1>Change password</h1>
				</div>
				
				<div class="alert alert-error" id='wrongPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">Passwords don't match!</div>
				<div class="alert alert-error" id='weakPassword' style="margin-top: 10px; margin-bottom: 5px; display: none;">Passwords is too weak! It should be at least 8 characters long, containing a number, a symbol and both capital and normal letters.</div>
				<div class="alert alert-error" id='unkownError' style="margin-top: 10px; margin-bottom: 5px; display: none;">Whoops! Something went wrong</div>
				<div class="alert alert-success" id='passwordUpdated' style="margin-top: 10px; margin-bottom: 5px; display: none;">Password updated!</div>
				
				<div class="well">
					<form class="form-horizontal condensed">
						<div class="control-group condensed">
							<label class="control-label" for="password">New password</label>
							<div class="controls">
								<input class="condensed input-wide" type="password" name='password' placeholder="Enter your new password" />
							</div>
						</div>
						<div class="control-group condensed">
							<label class="control-label" for="confirmPassword">Repeat password</label>
							<div class="controls">
								<input class="condensed input-wide" type="password" name='confirmPassword' placeholder="Confirm your new password" />
							</div>
						</div>
						<div class="controls">
							<a class="btn btn-warning" style="margin-bottom: 9px;" id='changePassword'>Change password</a>
						</div>
					</form>
				</div>
			</section>