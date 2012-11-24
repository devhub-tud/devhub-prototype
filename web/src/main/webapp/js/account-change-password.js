$(document).ready(function() {

	var password = $('input[name="password"]');
	var confirmPassword = $('input[name="confirmPassword"]');

	confirmPassword.blur(function(e) {
		if (password.val() !== confirmPassword.val()) {
			console.log("no match");
			$('#wrongPassword').show('normal');
			$('#weakPassword').hide('normal');
		} else if (!isPasswordOk(password.val())) {
			$('#weakPassword').show('normal');
			$('#wrongPassword').hide('normal');
		} else {
			$('#wrongPassword').hide('normal');
			$('#weakPassword').hide('normal');
		}
	});
	
	$('#changePassword').click(function (e) {
		var passw = password.val();
		if (passw !== confirmPassword.val()) {
			window.alert("Passwords don't match!");
		} else if (!isPasswordOk(passw)) {
			window.alert("Password too weak");
		} else {
			$.ajax({
				url: window.location.origin + "/api/account/reset-password",
				type: "post",
				contentType: "application/json",
				data: JSON.stringify( { "password": passw }), 
				success: function(data) {
					$("#passwordUpdated").show('normal');
					$("#unkownError").hide('normal');
					password.val("");
					confirmPassword.val("");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#passwordUpdated").hide('normal');
					$("#unkownError").show('normal');
					password.val("");
					confirmPassword.val("");
				}
		});
		}
	});

});
