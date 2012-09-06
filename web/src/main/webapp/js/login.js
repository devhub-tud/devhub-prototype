$(document).ready(function() {
	
	$('#signin').submit(function(e) {
		e.preventDefault();
		
		var email = $('#signin').find('input[name="email"]').val();
		var password = $('#signin').find('input[name="password"]').val();
		var remember = $('#signin').find('input[name="rememberMe"]').attr('checked') ? true: false;
		
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
					window.alert("Wrong username or password");
					$('#signin').find('input[name="password"]').val("");
				} else {
					window.alert("Uknown error");
				}
			}
		});
	});
	
	$('#signup').submit(function(e) {
		e.preventDefault();
		
		var emailField = $('#signup').find('input[name="email"]');
		var email = emailField.val();
		
		var signUpBtn = $('#signup').find('input[type="submit"]');
		setButtonState(signUpBtn, false);
		
		$.ajax({
			type: "post",
			url: "/register",
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