#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">

#if($invitations && !$invitations.isEmtpy())
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
								<td>
									${invitation.name}
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

				<h3>Your projects <a id="create-new-project" class="btn btn-primary btn-mini">Sign up for a new project</a></h3>
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th>Project name</th>
						</tr>
					</thead>
					<tbody>
#foreach($project in $projects)
						<tr>
							<td>
								<a href="#">$project.getName()</a> <!-- TODO: link to /project/${project.id} -->
							</td>
						</tr>
#end
					</tbody>
				</table>	
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
					<a href="#" id="cancel-create-new-project-modal" class="btn">Cancel</a>
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
					<a href="#" id="close-create-new-project-modal" class="btn btn-primary">Close</a>
				</div>
			</div>
		</div>
		
#parse("footer.tpl")