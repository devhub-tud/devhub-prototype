#parse("header.tpl")
#parse("menu.tpl")

<div class="container">
	<div class="content">

		<div class="page-head">
			<h3>${project.getName()}</h3>
			<a id="invite-user" class="btn btn-primary">Invite user</a>
			#if($invitations && !$invitations.isEmpty())
			<a id="provision-project" class="btn btn-primary disabled" style="margin-right: 10px;">Provision project</a>
			#else
			<a id="provision-project" class="btn btn-primary" style="margin-right: 10px;">Provision project</a>
			#end
		</div>
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Project information</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Project</td>
					<td>${project.getName()}</td>
				</tr>
				<tr>
					<td>Git URL</td>
					<td>TODO</td>
				</tr>
				<tr>
					<td>Jenkins job URL</td>
					<td>TODO</td>
				</tr>
				<tr>
					<td>Sonar URL</td>
					<td>TODO</td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Members</th>
				</tr>
			</thead>
			<tbody>
				#foreach($member in $members)
				<tr>
					<td>$member.user.displayName</td>
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
					<td>$invitation.user.displayName</td>
					<td>$invitation.user.email</td>
				</tr>
				#end
			</tbody>
		</table>
		#end
	</div>
</div>

#parse("footer.tpl")
