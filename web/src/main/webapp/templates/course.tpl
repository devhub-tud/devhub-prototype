#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">
				<h3>${course.name}</h3>
				<p>Managed by: ${course.owner.displayName} - ${course.owner.email}</p>
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th>Student projects</th>
						</tr>
					</thead>
					<tbody>
#foreach($project in $projects)
						<tr>
							<td>
								<a href="#">${project.name}</a> <!-- TODO: link to: /project/${project.id} -->
							</td>
						</tr>
#end
					</tbody>
				</table>
			</div>
		</div>

#parse("footer.tpl")