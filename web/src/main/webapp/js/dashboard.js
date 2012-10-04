
$(document).ready(function() {
	
	var openModalBtn = $("#open-new-project-modal-btn");
	var modal = $("#start-new-project-modal");
	var form = $("#start-new-project-form");
	var inviteOthers = $("#invite-others");
	var inviteContainer = $("#invites-container");
	
	var courseSelector = $("#course-id");
	var provisionBtn = $("#provision-btn");
	
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
	
	function updateProvisionButtonState(valid) {
		if (valid) {
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
	
	const teacherMail = "@tudelft.nl";
	const studentMail = "@student.tudelft.nl";
	function isTuAddress(address) {
		return address.substr(0 - teacherMail.length) === teacherMail
			|| address.substr(0 - studentMail.length) === studentMail;
	}
	
});
