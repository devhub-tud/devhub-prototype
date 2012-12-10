#parse("header.tpl") #parse("menu.tpl")

<div class="container">
	<div class="content">
		<div>
			<button id="download-btn" class="btn btn-primary right" style="margin-top: 5px;">Download
				repositories</button>
			<h3>${course.name}</h3>
			<p>Managed by: ${course.owner.displayName} -
				${course.owner.email}</p>
		</div>
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

<div id="download-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3>Downloading repositories</h3>
	</div>
	<div class="modal-body">
		<div id='prepare-download'>
			<img src="/img/loader.gif" class="loader" />
			<p style="display: inline; margin-left: 5px;">Preparing the zip...</p>
		</div>
		<div id='download-ready'>
			<h2>Download ready!</h2>
			<p>Once downloaded, you can verify the file using this MD5 checksum:</p>
			<p id="download-hash" class='alert alert-warning'></p>
			<a id='download-link' class="btn btn-primary">Download</a>
		</div>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Done</a>
	</div>
</div>
<script>
	var courseId = ${course.id};
</script>
#parse("footer.tpl")
