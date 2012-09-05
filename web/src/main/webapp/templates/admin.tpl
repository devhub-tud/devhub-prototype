#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">

				<h3>Administrator dashboard<a id="promote-user" class="btn btn-primary btn-mini">Promote user</a><a id="create-new-course" class="btn btn-primary btn-mini">Create new course</a></h3>
				<table class="table table-striped table-bordered table-hover">
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
				</form>
			</div>
			<div class="modal-footer">
				<a href="#" id="cancel-create-new-course-modal" class="btn">Cancel</a>
				<a href="#" id="provision-new-course" class="btn btn-primary">Create course</a>
			</div>
		</div>

#parse("footer.tpl")