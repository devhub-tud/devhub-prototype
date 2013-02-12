$(document).ready(function() {
	var leaveProjectButton = $("#leave-project");
	var leaveProjectModal = $("#leave-project-modal");
	var leaveProjectConfirmBtn = $("#leave-project-btn");
	
	leaveProjectButton.click(function(e) {
		e.preventDefault();
		leaveProjectModal.modal('show');
	});
	
	leaveProjectConfirmBtn.click(function(e) {
		var url = "/api" + window.location.pathname + "/unenroll";

		$.ajax({
			url: url, 
			type: "post",
			dataType: "json",
			contentType: "application/json",
			success: function(data) {
				alert("You will be de-registered from this project's services. This might take a few moments...");
				window.location.replace("/dashboard");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("We were unable to de-register you from this project. Please contact a student assistent.");
				window.location.replace(window.location.pathname);
			}
		});
	});
});