#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		
#set ( $hasInvites = false)
#if($invitations && !$invitations.isEmpty())
	#set ( $hasInvites = true)
		<div id="invitations" class="alert alert-info">

	#if($invitations.size() == 1)
			<h3>You have 1 new invitation awaiting...</h3>
	#else
			<h3>You have $invitations.size() new invitations awaiting...</h3>
	#end

			<table class="table table-condensed">
				<tbody>
	#foreach($invitation in $invitations)
					<tr>
						<td>${invitation.getProject().getName()}
							<div class="btn-panel">
								<a class="btn btn-mini btn-success">Join project</a>
								<a class="btn btn-mini btn-danger">Ignore</a>
							</div>
						</td>
					</tr>
	#end
				</tbody>
			</table>
		</div>
#end

#if(!$projects.isEmpty())
		<div class="page-head">
			<h3>Your projects</h3>
			<a id="open-new-project-modal-btn" class="btn btn-primary">Start a new project</a>
		</div>
		<table class="table table-striped table-bordered table-hover" id="$projects.size()">
			<thead>
				<tr>
					<th>Project name</th>
				</tr>
			</thead>
			<tbody>
	#foreach($project in $projects)
				<tr>
					<td><a href="/project/${project.id}">$project.getName()</a></td>
				</tr>
	#end
			</tbody>
		</table>
#else
		<div class="well" style="margin-top: 96px;">
			<h1>Welcome to DevHub!</h1>
			<p>	
	#if ($hasInvites) 
				<strong>Hey there stranger!</strong> By the looks of it, you don't seem to be participating in any projects yet. 
				To get started you can either accept a pending project invitation at the top of the page, or set up 
				your own project by clicking on the blue button on the right. Before you know it you'll be 
				collaborating with your team mates.
	#else
				<strong>Hey there stranger!</strong> By the looks of it, you don't seem to be participating in any projects yet. 
				To get started you must set up your own project by clicking on the blue button on the right. 
				Before you know it you'll be collaborating with your team mates.
	#end	
			</p>
			<div style="height: 38px; margin-top: 32px;">
				<a id="open-new-project-modal-btn" class="pull-right btn btn-primary btn-large">Let's set up a project!</a>
			</div>
		</div>
#end
	</div>
</div>

<div id="start-new-project-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Start a new project</h3>
	</div>
	<div class="step-1">
		<div class="modal-body">
			<form id="start-new-project-form" class="form-horizontal">
				<div class="control-group condensed">
					<label class="control-label" for="course-id">Course name</label>
					<div class="controls">
						<select id="course-id" class="input-wide">
							<option value="-1"></option>
							<option value="1">IN 4305: Advanced Algorithms</option>
						</select>
					</div>
				</div>
				<div class="control-group condensed">
					<div class="controls">
						<label class="checkbox">
							<input type="checkbox" id="invite-others" value="yes" /> Invite others to join
						</label>
					</div>
				</div>
				<div class="hide" id="invites-container">
					<hr />
					<div class="controls">
						<h5>Invite other users</h5>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Cancel</a>
			<a href="#" id="provision-btn" class="btn btn-primary">Let's do this!</a>
		</div>
	</div>
</div>

#parse("footer.tpl")
