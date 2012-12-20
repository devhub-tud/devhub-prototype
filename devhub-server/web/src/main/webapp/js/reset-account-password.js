
$(document).ready(function() {

	var idField = $('input[name="id"]');
	var tokenField = $('input[name="token"]');
	var emailField = $('input[name="email"]');
	var displayNameField = $('input[name="displayName"]');
	var password1Field = $('input[name="password1"]');
	var password2Field = $('input[name="password2"]');
	var completeBtn = $('input[type="submit"]');
	
	setInterval(function() {
		checkFormValidity();
	}, 100);
	
	$('#reset').submit(function(e) {
		e.preventDefault();
		
		var id = idField.val();
		var token = tokenField.val();
		var email = emailField.val();
		var displayName = displayNameField.val();
		var password = password1Field.val();
		setButtonState(completeBtn, false);
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			url: "/api/forgot-password/" + id + "/" + token,
			data: JSON.stringify({ "id": id, "token": token, "email": email, "displayName": displayName, "password": password }),
			success: function(data) {
				window.location.replace("/account");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-error", jqXHR.responseText);
				setButtonState(completeBtn, true);
			}
		});
	});
	
	function checkFormValidity() {
		var password1Valid = checkPassword1Field();
		var password2Valid = checkPassword2Field();
		setButtonState(completeBtn, password1Valid && password2Valid);
	}
	
	function checkPassword1Field() {
		var password1 = password1Field.val();
		var isValid = isPasswordOk(password1);
		setInputState(password1Field, isValid);
		if (isValid) {
			$("#pwError").hide();
		} else if (password1 !== ""){
			$("#pwError").show();
		}
		return isValid;
	}
	
	function checkPassword2Field() {
		var password1 = password1Field.val();
		var password2 = password2Field.val();
		var isValid = isPasswordOk(password2) && password2 == password1;
		setInputState(password2Field, isValid);
		return isValid;
	}
	
	function setInputState(field, valid) {
		var controlGroup = field.parentsUntil('.control-group').parent();
		if (valid) {
			controlGroup.removeClass("error");
		}
		else {
			controlGroup.addClass("error");
		}
	}
	
	function setButtonState(button, valid) {
		if (valid) {
			button.removeAttr("disabled");
		}
		else {
			button.attr("disabled", "disabled");
		}
	}
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});
