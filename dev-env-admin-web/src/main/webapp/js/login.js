$(document).ready(function() {
	
	$('#signin').submit(function(e) {
		e.preventDefault();
		
		var email = $('#signin').find('input[name="email"]').val();
		var password = $('#signin').find('input[name="password"]').val();
		var remember = $('#signin').find('input[name="rememberMe"]').attr('checked') ? true: false;
		
		$.ajax({
			type: "post",
			data: { "username": email, "password": password, "rememberMe": remember },
			dataType: "text",
			success: function(data) {
				window.location.replace("/dashboard");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// Already logged in...
				if (jqXHR.status == 405) {
					window.location.replace("/dashboard");
				}
			}
		});
	});
	
	$('#signup').submit(function(e) {
		e.preventDefault();
		
		var emailField = $('#signup').find('input[name="email"]');
		var email = emailField.val();
		
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
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-info", jqXHR.responseText);
			}
		});
	});
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});