$(document).ready(function() {
	
	$('#signin').submit(function(e) {
		e.preventDefault();
		
		var email = $('#signin').find('input[name="email"]').val();
		var password = $('#signin').find('input[name="password"]').val();
		var remember = $('#signin').find('input[name="rememberMe"]').attr('checked') ? true: false;
		
		if (!isTuAddress(email, false)) {
			showNotTudelftAddress();
			return;
		}
		
		$.ajax({
			type: "post",
			url: "/login",
			data: { "username": email, "password": password, "rememberMe": remember },
			dataType: "text",
			success: function(data) {
				window.location.replace("/dashboard");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status === 405) {
					showAlert("alert-error", "Wrong username or password");
					$('#signin').find('input[name="password"]').val("");
				} else if (jqXHR.status === 302) {
					window.location.replace("/dashboard");
				}  else {
					showAlert("alert-error", "<strong>Unknown error</strong>");
				}
			}
		});
	});
	
	$('#signup').submit(function(e) {
		e.preventDefault();
		
		var emailField = $('#signup').find('input[name="email"]');
		var email = emailField.val();
		
		if (!isTuAddress(email, false)) {
			showNotTudelftAddress();
			return;
		}
		
		var signUpBtn = $('#signup').find('input[type="submit"]');
		setButtonState(signUpBtn, false);
		
		$.ajax({
			type: "post",
			url: "/api/register",
			data: JSON.stringify({ "email": email }),
			dataType: "text",
			contentType: "application/json",
			success: function(data) {
				emailField.val("");
				showAlert("alert-success", "An e-mail has been sent to <strong>" + email 
						+ "</strong>.<br/>This e-mail contains futher instructions on how to complete your registration.");
				setButtonState(signUpBtn, true);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-info", jqXHR.responseText);
				setButtonState(signUpBtn, true);
			}
		});
	});
	
	
	$('#signup').find('input[name="email"]').blur(onAddressChange);
	
	$('#signin').find('input[name="email"]').blur(onAddressChange);
	
	function onAddressChange(event) {
		if (!isTuAddress(event.srcElement.value, true)) {
			showNotTudelftAddress();
		} else {
			$('.alerts').empty();
		}
	}
	
	const teacherMail = "@tudelft.nl";
	const studentMail = "@student.tudelft.nl";
	function isTuAddress(address, emptyAllowed) {
		return (emptyAllowed && address === "" )
			|| address.substr(0 - teacherMail.length) === teacherMail
			|| address.substr(0 - studentMail.length) === studentMail;
	}
	
	function setButtonState(button, valid) {
		if (valid) {
			button.removeAttr("disabled");
		}
		else {
			button.attr("disabled", "disabled");
		}
	}
	
	function showNotTudelftAddress() {
		showAlert("alert-error", "<strong>That's not a TU-Delft address.</strong>");
	}
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert).show('normal');
	}
	
});