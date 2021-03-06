
$(document).ready(function() {
	
	const teacherMail = "@tudelft.nl";
	const studentMail = "@student.tudelft.nl";
	
	var openModalBtn = $("#open-new-project-modal-btn");
	var modal = $("#start-new-project-modal");
	var form = $("#start-new-project-form");
	var inviteOthers = $("#invite-others");
	var inviteContainer = $("#invites-container");
	
	var courseSelector = $("#course-id");
	
	var progressDescription = $("#progress-description");
	var progressBar = $("#progress-bar");
	
	var provisionBtn = $("#provision-btn");
	var closeBtn = $("#close-btn");
	
	var step1 = modal.find(".step-1");
	var step2 = modal.find(".step-2").hide();
	
	var formBusy = false;
	
	modal.on("hide", function() {
		formBusy = false;
		courseSelector.val(-1);
		inviteOthers.attr("checked", false);
		progressBar.addClass("active").removeClass("progress-danger").addClass("progress-striped");
		progressDescription.text("");
		$(".alerts").empty();
	});
	
	modal.on("show", function() {
		step1.show();
		step2.hide();
		
		closeBtn.hide();
		
		courseSelector.empty().append("<option value='-1'></option>");
		
		$.ajax({
			type: "get",
			url: "/api/courses",
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify({ "enrolled": false, "substring": "" }),
			success: function(data) {
				$.each(data, function(index, value) {
					courseSelector.append("<option value='" + value.id + "'>" + value.name + "</option>");
				});
			},
			error: function(jqXHR, textStatus, errorThrown) { }
		});
	});
	
	openModalBtn.click(function() {
		modal.modal("show");
	});
	
	setInterval(function() {
		var courseId = courseSelector.val();
		var validCourse = courseId >= 0;
		
		if (inviteOthers.is(":checked")) {
			ensureInviteContainerIsVisible();
			var formIsValid = checkEmails() && validCourse;
			updateProvisionButtonState(formIsValid);
		}
		else {
			emptyAndHideInviteContainer();
			updateProvisionButtonState(validCourse);
		}
		
	}, 250);
	
	provisionBtn.click(function(e) {
		e.preventDefault();
		
		formBusy = true;
		updateProvisionButtonState(false);
		
		var courseId = courseSelector.val(); 
		var invites = listInvites();
		
		$.ajax({
			type: "post",
			url: "/api/projects",
			contentType: "application/json",
			data: JSON.stringify({ "course": courseId, "invites": invites, "versionControlService": "Gitolite", "continuousIntegrationService": "Jenkins" }),
			success: function(projectId) {
				formBusy = false;
				startProvisioning(projectId);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-error", jqXHR.responseText);
				formBusy = false;
			}
		});
	});
	
	function startProvisioning(projectId) {
		progressDescription.text("Provisioning project...")

		step1.hide();
		step2.show();

		var timer = setInterval(function() {
			$.ajax({
				type: "get",
				dataType: "json",
				url: "/api/projects/provisioning",
				success: function(data) {
					updateDialog(data, timer);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					updateDialog(JSON.parse(jqXHR.responseText), timer);
				}
			});
		}, 500);
		
		function updateDialog(data, timer) {
			progressDescription.text(data.message);
			if (data.finished) {
				if (data.failures) {
					progressBar.removeClass("active").removeClass("progress-striped").addClass("progress-danger");
				}
				else {
					progressBar.removeClass("active").removeClass("progress-striped").addClass("progress-success");
				}
				clearInterval(timer);
				closeBtn.show();
			}
		}
	}
	
	function listInvites() {
		var invites = [];
		var inviteBoxes = $(".inviteBox");
		
		var allValid = true;
		var containsEmpty = false;
		$.each(inviteBoxes, function(index, value) {
			var inviteBox = $(value);
			if (!isEmpty(inviteBox) && checkIfValid(inviteBox)) {
				invites.push(inviteBox.val());
			}
		});
		
		return invites;
	}
	
	function updateProvisionButtonState(valid) {
		if (valid && !formBusy) {
			provisionBtn.removeClass("disabled").removeAttr("disabled", "");
		}
		else {
			provisionBtn.addClass("disabled").attr("disabled", "disabled");
		}
	}
	
	function ensureInviteContainerIsVisible() {
		inviteContainer.show();
	}
	
	function emptyAndHideInviteContainer() {
		$(".invite-group").remove();
		inviteContainer.hide();
	}
	
	function checkEmails() {
		var invites = $(".inviteBox");
		
		var allValid = true;
		var containsEmpty = false;
		$.each(invites, function(index, value) {
			var inviteBox = $(value);
			if (isEmpty(inviteBox)) {
				containsEmpty = true;
			}
			else {
				allValid = allValid && checkIfValid(inviteBox);
			}
		});
		
		if (allValid && !containsEmpty) {
			addInviteBox(invites.length + 1);
		}
		
		return allValid;
	}
	
	function addInviteBox(newIndex) {
		inviteContainer.append(
			"<div class=\"control-group invite-group condensed\">" +
				"<label class=\"control-label\" for=\"invite-id-" + newIndex + "\">E-mail</label>" +
				"<div class=\"controls\">" +
					"<input id=\"invite-id-" + newIndex + "\" type=\"text\" class=\"inviteBox input-wide\" />" +
				"</div>" +
			"</div>"
		);
	}
	
	function isEmpty(input) {
		return input.val().length == 0;
	}
	
	function checkIfValid(input) {
		var value = input.val();
		if (isTuAddress(value)) {
			input.parent().parent().removeClass("error");
			return true;
		}
		else {
			input.parent().parent().addClass("error");
			return false;
		}
	}
	
	function isTuAddress(address) {
		return address.substr(0 - teacherMail.length) === teacherMail
			|| address.substr(0 - studentMail.length) === studentMail;
	}
	
});
