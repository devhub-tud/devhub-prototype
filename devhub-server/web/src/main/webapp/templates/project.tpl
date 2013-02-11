#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		<div style="margin-bottom: 48px;">
			<div class="page-header">
				<h2>${project.getName()}<a id="leave-project" class="btn btn-danger right suppressed">Leave project</a></h2>
			</div>
			<span class="muted">Project description goes here...</span>
		</div>
		<table class="values table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Project information</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="attribute">Git URL</td>
					<td>${project.getSourceCodeUrl()}</td>
				</tr>
				<tr>
					<td class="attribute">Continuous Integration</td>
					<td><a href="${project.getContinuousIntegrationUrl()}">${project.getContinuousIntegrationUrl()}</a></td>
				</tr>
			</tbody>
		</table>

		<div class="button-bar">
			<a id="invite-user" class="btn btn-primary right">Invite user</a>
		</div>
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Members</th>
				</tr>
			</thead>
			<tbody>
				#foreach($member in $members)
				<tr>
					<td class="attribute">$member.user.displayName</td>
					<td>$member.user.email</td>
				</tr>
				#end
			</tbody>
		</table>

		#if($invitations && !$invitations.isEmpty())
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Pending invitations</th>
				</tr>
			</thead>
			<tbody>
				#foreach($invitation in $invitations)
				<tr>
					#if(! $invitation.user )
					<td class="attribute">Unknown user</td> #else
					<td class="attribute">$invitation.user.displayName</td> #end
					<td>$invitation.email</td>
				</tr>
				#end
			</tbody>
		</table>
		#end
	</div>
</div>

<div id="invite-user-modal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Invate a user</h3>
	</div>
	<div class="modal-body">
		<div class="alerts"></div>
		<form id="enroll-to-course-form" class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="user-search">Invite user</label>
				<div class="controls">
					<input type="text" id='user-search' placeholder="User email" class="input-xlarge" /> <img src="/img/loader.gif" class="loader hide" />
					<div class="hide help-block"></div>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a> <a id="invite-btn" class="btn btn-primary">Invite</a>
	</div>
</div>

<div id="leave-project-modal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Leave project</h3>
	</div>
	<div class="modal-body">
		<form id="leave-project-form" class="form-horizontal">
			<div class="control-group">
				<img src="/img/loader.gif" class="loader hide" />
				<div class="alert alert-danger">You're about to leave this project. Are you sure you want to do this? You cannot undo this action.</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a> <a id="leave-project-btn" class="btn btn-primary">Leave</a>
	</div>
</div>

#parse("footer.tpl")
