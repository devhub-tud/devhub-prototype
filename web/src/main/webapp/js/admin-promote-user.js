$(document).ready(function() {

	var newPromotionModal = $('#promote-user-to-teacher-modal');
	
	var searchField = $('#search-field');
	var assistents = $('#assistents');
	var results = $('#results');
	var openModalButton = $('#promote-user-to-teacher');
	var cancelModalButton = $('#cancel-promote-user-to-teacher-modal');
	var promoteUserButton = $('#promote-user-to-teacher-btn');

	var lastValue;

	setInterval(function() {
		setTimeout(function() {
			var query = searchField.val();
			if (query != lastValue) {
				lastValue = query;
				startUserSearch(query);
			}
		}, 250);
	}, 100);

	function startUserSearch(query) {
		var currentQuery = searchField.val();
		if (query != currentQuery) {
			return;
		}
		
		promoteUserButton.attr("disabled", "disabled");
		
		checkCourseName(currentCourseName, function(result) {
			var currentQuery = searchField.val();
			if (query == currentQuery) {
				// TODO: implement this.
			}
		});
	}

	function queryUser(query, callback) {
		$.ajax({
				type: "get",
				url: "/accounts?substring=" + courseName,
				success: function(data) {
					callback.call(this, "ok");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					callback.call(this, jqXHR.responseText);
				}
		});
	}

	openModalButton.click(function(e) {
		e.preventDefault();
		newPromotionModal.modal('show');
	});
	
	cancelModalButton.click(function(e) {
		e.preventDefault();
		close();
	});
	
	promoteUserButton.click(function(e) {
		e.preventDefault();
		
		// Implement this.
	});
	
	function close() {
		newPromotionModal.modal('hide');
		searchField.val("");
		assistents.empty();
		results.empty();
	}
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});