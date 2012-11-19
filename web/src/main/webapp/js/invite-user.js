$(document).ready(function() {
	var inviteUserButton = $("#invite-user");
	var inviteUserModal = $("#invite-user-modal");
	var inviteConfirmBtn = $("#invite-btn");
	var userSearch = $("#user-search"); 
	var loader = userSearch.parentsUntil('.control-group').parent().find(".loader");
	var alerts = inviteUserModal.find('.alerts');
	
	inviteUserButton.click(function(e) {
		e.preventDefault();
		inviteUserModal.modal('show');
	});
	
	inviteUserModal.on("hide", function() {
		userSearch.val("");
		loader.hide();
	});
	
	inviteConfirmBtn.click(function(e) {
		console.log(loader);
		var mail = userSearch.val();
		if (mail == undefined || mail === "") return;
		loader.show();
		var url = "/api" + window.location.pathname + "/invite/" + mail;
		
		$.ajax({
			url: url, 
			success: function(data) {
				window.location.replace(window.location.pathname);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				loader.hide();
				if (jqXHR.responseText === "unknown-user") {
					showAlert("alert-error", "Email not registered at DevHub.<br>You can only invite people who have registered on DevHub before.");
				} else if (jqXHR.responseText === "User is already invited") {
					showAlert("alert-error", "That user is already invited.")
				} else {
					showAlert("Whoops! Something went wrong. Sorry about that.")
				}
			}
		});
		
		function showAlert(type, message) {
			var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
			alerts.empty().append(alert).show('normal');
		}
	});
});