$(document).ready(function() {
	
	$('#login').submit(function(e) {
		e.preventDefault();
		
		var email = $('#login').find('input[name="email"]').val();
		var password = $('#login').find('input[name="password"]').val();
		var remember = $('#login').find('input[name="rememberMe"]').attr('checked') ? true: false;
		
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
	
	$('#register').submit(function(e) {
		e.preventDefault();
		
		var email = $('#register').find('input[name="email"]').val();
		
		$.ajax({
			type: "post",
			url: "/register",
			data: JSON.stringify({ "email": email }),
			dataType: "text",
			contentType: "application/json",
			success: function(data) {
				$('.hero-unit').hide();
				$('#successful-registration').show();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.responseText);
			}
		});
	});
	
});