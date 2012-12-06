$(document).ready(function() {
	
	$("#feedback-form").submit(function(e) {
		e.preventDefault();
		
		var titleBox = $("#feedback-form").find('input[name="title"]');
		var contentBox = $("#feedback-form").find('textarea[name="content"]');
		
		var title = titleBox.val();
		var content = contentBox.val();
		
		$.ajax({
			type: "post",
			url: "/feedback",
			data: { "title": title, "content": content },
			success: function(data) {
				showAlert("alert-success", "<strong>Thank you for your feedback!</strong> We'll be in touch...")
				titleBox.val("");
				contentBox.val("");
			},
			error: function(jqXHR, textStatus, errorThrown) {
				showAlert("alert-error", "<strong>We're terribly sorry</strong>, but your feedback could not be submitted!");
			}
		});
	});
	
});