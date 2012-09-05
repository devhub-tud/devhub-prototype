$(document).ready(function() {

	var newCourseModal = $('#create-new-course-modal');
	
	var courseCodeField = $('#course-code');
	var courseNameField = $('#course-name');
	var newCourseButton = $('#create-new-course');
	var cancelCourseButton = $('#cancel-create-new-course-modal');
	var provisionNewCourseButton = $('#provision-new-course');

	var lastValue;

	setInterval(function() {
		setTimeout(function() {
			var courseName = courseCodeField.val() + " " + courseNameField.val();
			if (courseName != lastValue) {
				lastValue = courseName;
				startCourseNameCheck(courseName);
			}
		}, 250);
	}, 100);

	function startCourseNameCheck(courseName) {
		
		var currentCourseName = courseCodeField.val() + " " + courseNameField.val();
		if (courseName != currentCourseName) {
			return;
		}
		
		var controlGroup = courseNameField.parentsUntil('.control-group').parent();
		var helpBlock = controlGroup.find(".help-block");
		
		helpBlock.hide();
		provisionNewCourseButton.attr("disabled", "disabled");
		
		checkCourseName(currentCourseName, function(result) {
			var currentCourseName = courseCodeField.val() + " " + courseNameField.val();
			if (courseName == currentCourseName) {
				if (result == "ok") {
					controlGroup.removeClass("error warning");
					provisionNewCourseButton.removeAttr("disabled");
				}
				else if (result == "already-taken") {
					helpBlock.html("The course name <strong>" + currentCourseName + "</strong> is taken!").show();
					controlGroup.addClass("error");
					provisionNewCourseButton.attr("disabled", "disabled");
				}
				else if (result == "invalid-name") {
					helpBlock.html("Course names may only consist of letters and numbers, and must be at least 4 characters long!").show();
					controlGroup.addClass("error");
					provisionNewCourseButton.attr("disabled", "disabled");
				} else {
					helpBlock.html("Server error: " + result);
					controlGroup.addClass("error");
					provisionNewCourseButton.attr("disabled", "disabled");
				}
			}
		});
	}

	function checkCourseName(courseName, callback) {
		$.ajax({
				type: "get",
				url: "/courses/checkName?name=" + courseName,
				success: function(data) {
					callback.call(this, "ok");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					callback.call(this, jqXHR.responseText);
				}
		});
	}

	newCourseButton.click(function(e) {
		e.preventDefault();
		newCourseModal.modal('show');
	});
	
	cancelCourseButton.click(function(e) {
		e.preventDefault();
		close();
	});
	
	provisionNewCourseButton.click(function(e) {
		e.preventDefault();
		
		var courseName = courseNameField.val();
		console.log(JSON.stringify({ "name": courseName }));
		
		$.ajax({
				type: "post",
				dataType: "text",
				contentType: "application/json",
				url: "/courses/create", 
				data: JSON.stringify({ "name": courseName }), 
				success: function() {
					close();
					window.location.reload();
				},
				error: function(jqXHR, textStatus, errorThrown) {
					showAlert("alert-error", jqXHR.responseText);
				}
		});
	});
	
	function close() {
		newCourseModal.modal('hide');

		courseCodeField.val("");
		courseNameField.val("");
	}
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});