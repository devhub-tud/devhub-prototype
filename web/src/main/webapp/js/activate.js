
$(document).ready(function() {

	var emailField = $('input[name="email"]');
	var netIdField = $('input[name="net-id"]');
	var studentNumberField = $('input[name="student-number"]');
	var nameField = $('input[name="name"]');
	var password1Field = $('input[name="password1"]');
	var password2Field = $('input[name="password2"]');
	var completeBtn = $('input[type="submit"]');
	
	setInterval(function() {
		checkFormValidity();
	}, 100);
	
	$('#activate').submit(function(e) {
		e.preventDefault();
		
		var email = emailField.val();
		var netId = netIdField.val();
		var studentNumber = studentNumberField.val();
		var name = nameField.val();
		var password = password1Field.val();
		var token = getTokenFromUrl();
		
		setButtonState(completeBtn, false);
		
		$.ajax({
			type: "post",
			url: "/api/accounts/activate/" + token,
			contentType: "application/json",
			data: JSON.stringify({ "email": email, "password": password, "displayName": name, "netId": netId, "studentNumber": studentNumber }),
			success: function(data) {
				window.location.replace("/account/");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-error", jqXHR.responseText);
				setButtonState(completeBtn, true);
			}
		});
	});
	
	function getTokenFromUrl() {
		var href = window.location.href;
		var index = href.lastIndexOf("/") + 1;
		return href.substring(index);
	}
	
	function checkFormValidity() {
		var netIdValid = checkNetIdField();
		var studentNumberValid = checkStudentNumberField();
		var nameValid = checkNameField();
		var password1Valid = checkPassword1Field();
		var password2Valid = checkPassword2Field();
		setButtonState(completeBtn, netIdValid && studentNumberValid && nameValid && password1Valid && password2Valid);
	}
	
	function checkNetIdField() {
		var netId = netIdField.val();
		var isValid = netId != undefined && netId.match("^[a-z0-9]{2,20}$");
		setInputState(netIdField, isValid);
		return isValid;
	}
	
	function checkStudentNumberField() {
		var studentNumber = studentNumberField.val();
		var isValid = studentNumber != undefined && studentNumber.match("^[0-9]{7}$");
		setInputState(studentNumberField, isValid);
		return isValid;
	}
	
	function checkNameField() {
		var name = nameField.val();
		var isValid = name != undefined && name != "";
		setInputState(nameField, isValid);
		return isValid;
	}
	
	function checkPassword1Field() {
		var password1 = password1Field.val();
		var isValid = isPasswordOk(password1);
		setInputState(password1Field, isValid);
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
