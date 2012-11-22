#parse("header.tpl")
#parse("menu.tpl")

<div class="container">
	<div class="content">
		<h3>${course.name}</h3>
		<p>Managed by: ${course.owner.displayName} - ${course.owner.email}</p>
		#if(${user.isAdmin()})
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Student projects</th>
				</tr>
			</thead>
			<tbody>
				#foreach($project in $projects)
				<tr>
					<td><a href="/project/${project.id}">${project.name}</a></td>
				</tr>
				#end
			</tbody>
		</table>
		#end
	</div>
</div>

#parse("footer.tpl")
