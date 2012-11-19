			<section id="key-management">
				<div class="page-header">
					<h1>SSH key management <a class="btn right" id="add-key">Add new SSH key</a></h1>
				</div>
#if ($user.getSshKeys().isEmpty())
				<div class="alert alert-danger"><strong>You haven't added any SSH keys yet!</strong></div>
#else
				<table class="values table table-striped table-bordered">
					<tbody>
	#foreach($key in $user.getSshKeys())
						<tr><td><label class="checkbox"><input type="checkbox"> $key.getKeyName()</label></td></tr>
	#end
					</tbody>
				</table>
				<div class="button-bar">
					<button class="btn btn-danger right">Delete key(s)</button>
				</div>
#end
			</section>