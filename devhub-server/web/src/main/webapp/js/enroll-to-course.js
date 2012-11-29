$(document).ready(function() {

	var enrollModal = $('#enroll-to-course-modal');
	var courseNameField = $('#course-search');
	var courseResults = $('#course-results');
	var enrollButton = $('#enroll-btn');
	
	enrollModal.on("hidden", function() {
		courseNameField.val("");
		courseResults.empty();
	});
	
	var lastValue;
	
	setInterval(function() {
		setTimeout(function() {
			var courseName = courseNameField.val();
			if (courseName != lastValue) {
				lastValue = courseName;
				startCourseSearch(courseName);
			}
		}, 250);
	}, 100);
	
	function startCourseSearch(query) {
		var currentQuery = courseNameField.val();
		if (query != currentQuery) {
			return;
		}
		
		queryCourses(courseNameField.val(), function(results) {
			var currentQuery = courseNameField.val();
			if (query == currentQuery) {
				courseResults.empty();
				courseResults.find(".btn").unbind();
				for (var i = 0; i < results.length; i++) {
					courseResults.append(createResultDiv(results[i]));
				}
				courseResults.find(".btn").click(function() {
					var parent = $(this).parent();
					var id = parent.attr("course-id");
				});
			}
			
			function createResultDiv(result) {
				var div = "<div>";
				div += "<label class='checkbox'>";
				div += "<input type='checkbox' class='course-result' value='" + result.id + "'> " + result.name;
				div += "</label>";
				div += "</div>";
				
				return div;
			}
		});
	}

	function queryCourses(query, callback) {
		if (query == undefined || query.length == 0) {
			courseResults.empty();
			return;
		}
		
		$.ajax({
				type: "get",
				contentType: "application/json",
				url: "/api/courses?enrolled=false&substring=" + query,
				success: function(data) {
					callback.call(this, data);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					callback.call(this, jqXHR.responseText);
				}
		});
	}

	$("#enroll-to-course").click(function(e) {
		e.preventDefault();
		enrollModal.modal('show');
	});
	
	$("#enroll-btn").click(function(e) {
		var checkBoxes = $(".course-result");
		var errors = [];
		var success = 0;
		var total = 0;
		
		checkBoxes.each(function(index, element) {
			var jElement = $(element);
			var isChecked = jElement.is(":checked");
			if (isChecked) {
				total++;
			}
		});
		
		checkBoxes.each(function(index, element) {
			var jElement = $(element);
			var courseId = jElement.attr("value");
			var isChecked = jElement.is(":checked");
			
			if (isChecked) {
				$.ajax({
					type: "get",
					contentType: "application/json",
					url: "/api/course/" + courseId + "/enroll",
					success: function(data) {
						success++;
						if (success + errors.length == total) {
							closeOrShowErrors(errors);
						}
					},
					error: function(jqXHR, textStatus, errorThrown) {
						errors.push(jqXHR.responseText);
						if (success + errors.length == total) {
							closeOrShowErrors(errors);
						}
					}
				});
			}
		});
	});
	
	function closeOrShowErrors(errors) {
		if (errors.length == 0) {
			enrollModal.modal('hide');
			window.location.replace("/dashboard");
		}
		else {
			var errorMessages = "Could not complete your request: <ul>";
			for (var i = 0; i < errors.length; i++) {
				errorMessages += "<li>" + errors[i] + "</li>";
			}
			errorMessages += "</ul>";
			
			courseResults.empty().append(errorMessages);
		}
	}
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});