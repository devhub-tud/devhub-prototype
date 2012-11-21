			<section id="key-management">
				<div class="page-header">
					<h1>SSH key management <a class="btn right" id="add-key">Add new SSH key</a></h1>
				</div>
#if ($ssh-keys.isEmpty())
				<div class="alert alert-danger"><strong>You haven't added any SSH keys yet!</strong></div>
#else
				<table class="values table table-striped table-bordered">
					<tbody>
	#foreach($key in $ssh-keys)
						<tr><td><label class="checkbox"><input name="keyId[]" value="$key.getId()" type="checkbox"> $key.getKeyName()</label></td></tr>
	#end
					</tbody>
				</table>
				<div class="button-bar">
					<button id="delete-ssh-keys-btn" class="btn btn-danger right">Delete key(s)</button>
				</div>
#end
				
				<div id="add-new-ssh-key" class="modal hide fade">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h3>Add a new public SSH key</h3>
					</div>
					<div class="modal-body">
						<form id="add-new-ssh-key-form" class="form-vertical">
							<div class="alert alert-info"><a href=""><strong>Need some help with SSH keys?</strong> Follow this guide...</a></div>
							<div class="alerts"></div>
							<div class="control-group condensed">
								<label class="control-label" for="ssh-key-name">Name</label>
								<div class="controls">
									<input type="text" class="input-wide" name="ssh-key-name" id="ssh-key-name" placeholder="Key name" />
								</div>
							</div>
							<div class="control-group condensed">
								<label class="control-label" for="ssh-key-content">Key</label>
								<div class="controls">
									<textarea class="input-wide" rows="9" name="ssh-key-content" id="ssh-key-content" placeholder="Key contents"></textarea>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<a href="#" class="btn" data-dismiss="modal">Cancel</a>
						<a href="#" id="add-ssh-key-btn" class="btn btn-primary">Add key</a>
					</div>
				</div>
			</section>