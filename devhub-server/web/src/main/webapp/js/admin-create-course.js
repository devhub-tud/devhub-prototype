$(document).ready(function() {

	var newCourseModal = $('#create-new-course-modal');
	
	var codeField = $('#course-code');
	var nameField = $('#course-name');
	var templateField = $('#template-url');
	var provisionNewCourseButton = $('#provision-new-course');
	var templateBox = $('#base-on-template');
	var templatePanel = $('#template-url-panel');

	var timers = [];
	
	$("#create-new-course").click(function(e) {
		e.preventDefault();
		newCourseModal.modal("show");
	});
	
	function getFullCourseName() {
		return codeField.val() + " " + nameField.val();
	}
	
	setInterval(function() {
		if (templateBox.is(":checked")) {
			templatePanel.show();
		}
		else {
			templatePanel.hide();
		}
	}, 100);

	newCourseModal.on("show", function() {
		nameField.val("");
		codeField.val("");
		templateBox.attr("checked", false);
		templateField.val("");
		enableVerification();
	});
	
	newCourseModal.on("hide", function() {
		stopTimers(timers);
		nameField.val("");
		codeField.val("");
		templateBox.attr("checked", false);
		templateField.val("");
	});
	
	provisionNewCourseButton.click(function(e) {
		e.preventDefault();
		var name = nameField.val();
		var code = codeField.val();
		var courseName = code.trim() + " " + name.trim();
		var template = templateField.val();
		
		stopTimers(timers);
		setButtonState(provisionNewCourseButton, false);
		
		displayProcessor();
		
		$.ajax({
			type: "post",
			dataType: "text",
			contentType: "application/json",
			url: "/api/courses/create", 
			data: JSON.stringify({ "name": courseName , "templateUrl": template }), 
			success: function() {
				window.location.replace("/admin");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				removeProcessor();
				var result = jqXHR.responseText;
				if (result == "already-taken") {
					showAlert("alert-error", "There's already a course with this code and name!");
				}
				else if (result == "invalid-name") {
					showAlert("alert-error", "This is not a valid course code and/or name!");
				}
				else if (result == "could-not-clone-repo") {
					showAlert("alert-error", "Could not clone from the template Git repository!", "Either the Git service hosting the repository is inaccessible or the repository is not public");
				}
				else {
					showAlert("alert-error", result);
				}
				
				provisionNewCourseButton.attr("disabled", "disabled");
				enableVerification();
			}
		});
	});
	
	function enableVerification() {
		timers.push(
			verify(codeField, "^[a-zA-Z0-9]{6,}$", function(value, callback) { 
				var value = nameField.val();
				if (value != undefined && value.match("^.{6,}$")) {
					callback.call(this, true);
				}
				else {
					callback.call(this, false);
				}
			}),
			verifyCheckbox(templateBox, templateField, "^.{1,}$"),
			synchronize(provisionNewCourseButton, [ codeField, nameField, templateField ])
		);
	}
	
});