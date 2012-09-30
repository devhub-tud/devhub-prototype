$(document).ready(function() {

	var newPromotionModal = $('#promote-user-to-teacher-modal');
	
	var searchField = $('#user-search');
	var searchResults = $('#search-results');
	var openModalButton = $('#promote-user-to-teacher');
	var closeModalButton = $('#close-promote-user-to-teacher-modal');
	
	newPromotionModal.on("hidden", function() {
		searchField.val("");
		searchResults.empty();
	});
	
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
		
		queryUsers(searchField.val(), function(results) {
			var currentQuery = searchField.val();
			if (query == currentQuery) {
				searchResults.empty();
				searchResults.find(".btn").unbind();
				for (var i = 0; i < results.length; i++) {
					searchResults.append(createResultDiv(results[i]));
				}
				searchResults.find(".btn").click(function() {
					var parent = $(this).parent();
					var id = parent.attr("account-id");
					var admin = parent.find(".teacher").hasClass("btn-primary");
					
					$.ajax({
						type: "post",
						contentType: "application/json",
						url: "/account/" + id + "/" + (admin ? "demote" : "promote"),
						success: function(data) {
							updateButtons(parent, !admin);
						},
						error: function(jqXHR, textStatus, errorThrown) {
							showAlert("alert-error", jqXHR.responseText);
						}
					});
				});
			}
			
			function updateButtons(parent, isAdmin) {
				if (isAdmin) {
					parent.find(".user").removeClass("btn-primary");
					parent.find(".teacher").addClass("btn-primary");
				}
				else {
					parent.find(".teacher").removeClass("btn-primary");
					parent.find(".user").addClass("btn-primary");
				}
			}
			
			function createResultDiv(result) {
				var div = "<div class='result rounded'>";
				div += result.name;
				div += "<div account-id='" + result.id + "' style='float: right;' class='btn-group'>";
				
				if (result.admin) {
					div += "<button type='button' class='user btn btn-mini'>User</button>";
					div += "<button type='button' class='teacher btn btn-mini btn-primary'>Teacher</button>";
				}
				else {
					div += "<button type='button' class='user btn btn-mini btn-primary'>User</button>";
					div += "<button type='button' class='teacher btn btn-mini'>Teacher</button>";
				}
				
				div += "</div>";
				div += "</div>";
				
				return div;
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
	
	closeModalButton.click(function(e) {
		newPromotionModal.modal('hide');
	});
	
	function showAlert(type, message) {
		var alerts = $('.alerts');
		var alert = "<div class=\"alert " + type + "\"><a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" + message + "</div>";
		alerts.empty().append(alert);
	}
	
});