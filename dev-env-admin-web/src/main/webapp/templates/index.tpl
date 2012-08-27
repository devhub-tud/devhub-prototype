#parse("header.tpl")
#parse("menu.tpl")

		<div class="container">
			<div class="content">

#if(!$invitations.isEmtpy())
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

				<h3>Your projects <a class="btn btn-primary btn-mini">Sign-up for a new project</a></h3>
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
								<a href="">$project.getName()</a>
							</td>
						</tr>
#end
					</tbody>
				</table>	
			</div>
		</div>
		
#parse("footer.tpl")