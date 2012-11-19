$(document).ready(function() {
	
	$('#recover').submit(function(e) {
		e.preventDefault();
		
		var email = $('#recover').find('input[name="email"]').val();
		
		if (email) {
			var button =$('#recover').find('input[type="submit"]');
			button.attr("disabled", "disabled");

			$.ajax({
				type: "post",
				url: "/api/reset-password/" + email,
				success: function(data) {
					showAlert("alert-success", "An e-mail has been sent to <strong>" + email 
							+ "</strong>.<br/>This e-mail contains futher instructions on how to recover access to your account.");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.status == 404) {
						showAlert("alert-info", "This email address is not associated with an account.");
					} else {
						showAlert("alert-info", jqXHR.responseText);
					}
					button.removeAttr("disabled");
				}
			});

		} else {
			showAlert("alert-info", "Email must be set");
		}
	});

	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});
