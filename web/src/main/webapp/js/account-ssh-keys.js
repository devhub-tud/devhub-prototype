$(document).ready(function() {
	
	var modal = $("#add-new-ssh-key");
	var nameField = $("#ssh-key-name");
	var keyField = $("#ssh-key-content");
	
	var addKeyBtn = $("#add-ssh-key-btn");
	var deleteKeyBtn = $("#delete-ssh-keys-btn");
	
	var timers = [];
	
	$("#add-key").click(function(e) {
		e.preventDefault();
		modal.modal("show");
	});
	
	modal.on("show", function() {
		nameField.val("");
		keyField.val("");
		enableVerification();
	});
	
	modal.on("hide", function() {
		stopTimers(timers);
		nameField.val("");
		keyField.val("");
	});
	
	addKeyBtn.click(function(e) {
		e.preventDefault();
		
		stopTimers(timers);
		setButtonState(addKeyBtn, false);
		
		modal.modal('hide');
		$("body").append("<div class='preloader'><div class='processing'><img src='/img/processing.gif'></div><div class='modal-backdrop fade in' style='opacity: 0.2;'></div></div>")
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			url: "/api/account/ssh-keys",
			data: JSON.stringify({ "name": nameField.val(), "key": keyField.val() }),
			success: function(data) {
				window.location.replace("/account/ssh-keys");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$(".preloader").remove();
				if (jqXHR.status == 409) {
					showAlert("alert-error", jqXHR.responseText);
				} else {
					showAlert("alert-error", "Unknown error, please try again later.");
				}
				enableVerification();
			}
		});
	});
	
	deleteKeyBtn.click(function() {
		var keys = [];
		$(":checked").each(function() {
			keys.push($(this).val());
		});
		
		var value = JSON.stringify({ "keyIds": keys });
		
		
		
		$.ajax({
			type: "delete",
			contentType: "application/json",
			url: "/api/account/ssh-keys",
			data: value,
			success: function(data) {
				window.location.replace("/account/ssh-keys");
				$(".processing").remove();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$(".processing").remove();
				if (jqXHR.status == 409) {
					showAlert("alert-error", jqXHR.responseText);
				} else {
					showAlert("alert-error", "Unknown error, please try again later.");
				}
				enableVerification();
			}
		});
	});
	
	function enableVerification() {
		timers.push(
			verify(nameField, "^[a-zA-Z0-9]+([\.-][a-zA-Z0-9]+)*$"),
			verify(keyField, "^[ssh\-](.){64,}$"),
			synchronize(addKeyBtn, [ nameField, keyField ])
		);
	}
	
});