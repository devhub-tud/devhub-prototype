$(document).ready(function() {

	var newPromotionModal = $('#promote-user-to-teacher-modal');
	
	var searchField = $('#user-search');
	var assistents = $('#assistents');
	var searchResults = $('#results');
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
		
		queryUsers(searchField.val(), function(result) {
			var currentQuery = searchField.val();
			if (query == currentQuery) {
				searchResults.empty();
				for (var i = 0; i < result.length; i++) {
					searchResults.append("<div class='result rounded'>" 
							+ result[i].name
							+ "<i class='icon-plus' style='float: right;'></i>"
							+ "</div>");
				}
			}
		});
	}

	function queryUsers(query, callback) {
		if (query == undefined || query.length == 0) {
			return;
		}
		
		$.ajax({
				type: "get",
				contentType: "application/json",
				url: "/accounts?substring=" + query,
				success: function(data) {
					callback.call(this, data);
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