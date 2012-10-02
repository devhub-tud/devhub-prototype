#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">

		#if($invitations && !$invitations.isEmpty())
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

		<div class="page-head">
			<h3>Your projects</h3>
			<a id="enroll-to-course" class="btn btn-primary">Enroll to course</a>
			<a id="create-new-project" class="btn btn-primary" style="margin-right: 10px;">Create personal project</a>
		</div>
		#if(!$projects.isEmpty())
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
		<div class="well">
			<h1>Welcome to DevHub!</h1>
			<p>You don't seem to be part of any projects yet. To start using DevHub, create your own project, enroll for a course, or accept a project invitation from someone in your team.</p>
		</div>
		#end
	</div>
</div>

<div id="create-new-project-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Create a new project</h3>
	</div>
	<div class="step-1">
		<div class="modal-body">
			<form id="create-new-project-form" class="form-horizontal">
				<div class="control-group">
					<label class="control-label" for="project-name">Project name</label>
					<div class="controls">
						<input type="text" id="project-name" placeholder="Project name" />
						<img src="/img/loader.gif" class="loader hide" />
						<div class="hide help-block"></div>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" id="cancel-create-new-project-modal" class="btn" data-dismiss="modal">Cancel</a>
			<a href="#" id="provision-new-project" class="btn btn-primary">Create project</a>
		</div>
	</div>
	<div class="step-2 hide">
		<div class="modal-body">
			<div id="create-new-project-progress">
				<strong>Creating project...</strong>
				<div class="progress progress-striped active">
					<div class="bar" style="width: 100%;"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="step-3 hide">
		<div class="modal-body">
			<div id="create-new-project-message">
				<strong></strong>
				<div class="progress">
					<div class="bar" style="width: 100%;"></div>
				</div>
			</div>
		</div>
		<div class="modal-footer">
			<a href="#" id="close-create-new-project-modal" class="btn btn-primary" data-dismiss="modal">Close</a>
		</div>
	</div>
</div>

<div id="enroll-to-course-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Enroll to course</h3>
	</div>
	<div class="modal-body">
		<div class="alerts"></div>
		<form id="enroll-to-course-form" class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="course-search">Search</label>
				<div class="controls">
					<input type="text" id='course-search' placeholder="Course name or code" class="input-xlarge" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="course-results">Results</label>
				<div class="controls">
					<div id="course-results" class="results-box input-xlarge"></div>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a>
		<a href="#" id="enroll-btn" class="btn btn-primary">Enroll</a>
	</div>
</div>

#parse("footer.tpl")
