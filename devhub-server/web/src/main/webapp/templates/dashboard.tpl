#parse("header.tpl") 
#parse("menu.tpl")

<div class="container">
	<div class="content">
		
#set ( $hasInvites = false)
#if($invitations && !$invitations.isEmpty())
	#set ( $hasInvites = true)
		<div id="invitations" class="invitations alert alert-info">

	#if($invitations.size() == 1)
			<h3>You have 1 new invitation awaiting...</h3>
	#else
			<h3>You have $invitations.size() new invitations awaiting...</h3>
	#end

			<table class="table table-condensed">
				<tbody>
	#foreach($invitation in $invitations)
					<tr>
						<td projectId="${invitation.getProject().getId()}">
							<span class="project-name">${invitation.getProject().getName()}</span>
							<div class="btn-panel">
								<button class="join btn btn-mini btn-success">Join project</button>
								<button class="reject btn btn-mini btn-danger">Ignore</button>
							</div>
						</td>
					</tr>
	#end
				</tbody>
			</table>
		</div>
#end

		<div class="row" style="margin-bottom: 48px;">
			<div class="span12">
				<img style="float: left;" src="${user.getGravatarUrl(128)}" class="img-polaroid" />
				<div class="left">
					<div class="page-header">
						<h2><span class="muted">Welcome, </span><br/>$user.getDisplayName()</h2>
					</div>
				</div>
			</div>
		</div>
		
#if(!$projects.isEmpty())
		<div>
			<h3>Your projects <button id="open-new-project-modal-btn" class="btn btn-primary right">Start a new project</button></h3>
			<div class="projects">
	#foreach($project in $projects)
				<a class="item" href="/project/${project.id}">
					<span class="title">$project.getName()</span>
					<span class="subtitle muted">Project description goes here...</span>
				</a>
	#end
			</div>
		</div>
#else
		<div class="well">
			<h1>Welcome to DevHub!</h1>
			<p>	
	#if ($hasInvites) 
				By the looks of it, you don't seem to be participating in any projects yet. 
				To get started you can either accept a pending project invitation at the top of the page, or set up 
				your own project by clicking on the blue button on the right. Before you know it you'll be 
				collaborating with your team mates.
	#else
				By the looks of it, you don't seem to be participating in any projects yet. 
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

<div id="start-new-project-modal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Start a new project</h3>
	</div>
	<div class="step-1">
		<div class="modal-body">
			<form id="start-new-project-form" class="form-horizontal">
				<div class="alerts"></div>
				<div class="control-group condensed">
					<label class="control-label" for="course-id">Course name</label>
					<div class="controls">
						<select id="course-id" class="input-wide">
							<option value="-1"></option>
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
	<div class="step-2">
		<div class="modal-body">
			<div class="alerts"></div>
			<h5 id="progress-description"></h5>
			<div id="progress-bar" class="progress progress-striped active">
				<div class="bar" style="width: 100%;"></div>
			</div>
		</div>
		<div class="modal-footer">
			<a href="#" id="close-btn" class="btn" data-dismiss="modal">Close</a>
			<a href="#" id="done-btn" class="btn btn-primary hide">See project details</a>
		</div>
	</div>
</div>

#parse("footer.tpl")
