#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">

				<div class="page-head">
					<h3>Administrator dashboard</h3>
					<div class="btn-group">
						<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"> Actions
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li><a href="#" id="create-new-course">Create new course</a></li>
							<li class="divider"></li>
							<li><a href="#" id="promote-user-to-teacher">Promote or demote users</a></li>
						</ul>
					</div>
				</div>
				<table class="table table-striped table-bordered table-hover" style="margin-top: 15px;">
					<thead>
						<tr>
							<th>Course</th>
						</tr>
					</thead>
					<tbody>
#foreach($course in $courses)
						<tr>
							<td>
								<a href="/course/$course.getId()">$course.getName()</a>
							</td>
						</tr>
#end
					</tbody>
				</table>

			</div>
		</div>

		<div id="create-new-course-modal" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>Create new course</h3>
			</div>
			<div class="modal-body">
			<div class="alerts"></div>
				<form id="create-new-course-form" class="form-horizontal">
					<div class="control-group">
						<!-- TODO: Fix alignment of input boxes: pop-up should have some free space on the right of the course name box. -->
						<label class="control-label" for="course-code">Course</label>
						<div class="controls">
							<input type="text" id="course-code" placeholder="IN1234" class="input-mini" />
							<input type="text" id="course-name" placeholder="Software Quality and Test Engineering" class="input-xlarge" />
							<div class="hide help-block"></div>
						</div>						
					</div>
					<div class="control-group">
						<label class="control-label" for="template-url">Template URL</label>
						<div class="controls">
							<input type="text" id="template-url" placeholder="git://github.com/octocat/Spoon-Knife.git" class="input-xlarge" />
							<span class="help-block">You can specify a Git repository that will be used as a template for this project. Note that the repository must be readable by DevHub. If you don't want to use a template leave this box empty.</span>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<a href="#" id="cancel-create-new-course-modal" class="btn">Cancel</a>
				<a href="#" id="provision-new-course" class="btn btn-primary">Create course</a>
			</div>
		</div>

		<div id="promote-user-to-teacher-modal" class="modal hide fade">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>Promote or demote users</h3>
			</div>
			<div class="modal-body">
			<div class="alerts"></div>
				<form id="promote-user-to-teacher-form" class="form-horizontal">
					<div class="control-group">
						<label class="control-label" for="user-search">Search</label>
						<div class="controls">
							<input type="text" id="user-search" placeholder="Name or e-mail" class="input-wide" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="user-search">Results</label>
						<div class="controls">
							<div id="search-results" class="results-box input-wide"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<a href="#" id="close-promote-user-to-teacher-modal" class="btn">Close</a>
			</div>
		</div>

#parse("footer.tpl")