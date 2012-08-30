$(document).ready(function() {
	
	$('form').submit(function(e) {
		e.preventDefault();
		
		var username = $('input[name="username"]').val();
		var password = $('input[name="password"]').val();
		var remember = $('input[name="rememberMe"]').attr('checked') ? true: false;
		
		$.ajax({
			type: "post",
			data: { "username": username, "password": password, "rememberMe": remember },
			dataType: "text",
			success: function(data) {
				window.location.replace("/projects");
			},
			error: function(a, b, c) {
				// Already logged in...
				if (a.status == 405) {
					window.location.replace("/projects");
				}
			}
		});
		
	});
	
});