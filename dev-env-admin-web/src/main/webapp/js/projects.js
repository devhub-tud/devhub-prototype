$(document).ready(function() {

	var newProjectModal = $('#create-new-project-modal');
	
	var projectNameField = $('#project-name');
	var newProjectButton = $('#create-new-project');
	var closeButton = $('#close-create-new-project-modal');
	var cancelButton = $('#cancel-create-new-project-modal');
	var provisionNewProjectButton = $('#provision-new-project');
	var newProjectForm = $('#create-new-project-form');
	var newProjectProgress = $('#create-new-project-progress');
	var newProjectMessage = $('#create-new-project-message');
	
	var step1 = newProjectModal.find('.step-1');
	var step2 = newProjectModal.find('.step-2');
	var step3 = newProjectModal.find('.step-3');
	
	var lastValue;
	
	setInterval(function() {
		checkProjectName(projectNameField.val(), function(ok) {
			var controlGroup = projectNameField.parentsUntil('.control-group').parent();
			if (ok) {
				controlGroup.removeClass("error");
				provisionNewProjectButton.removeAttr("disabled");
			}
			else {
				controlGroup.addClass("error");
				provisionNewProjectButton.attr("disabled", "disabled");
			}
		});
	}, 100);
	
	function checkProjectName(projectName, callback) {
		if (projectName != lastValue) {
			lastValue = projectName;
			$.get("/projects/checkName", { "name": projectName }, function(data) {
				callback.call(this, data.ok);
			});
		}
	}
	
	newProjectButton.click(function(e) {
		e.preventDefault();
		newProjectModal.modal('show');
	});
	
	closeButton.click(function(e) {
		e.preventDefault();
		close();
	});
	
	cancelButton.click(function(e) {
		e.preventDefault();
		close();
	});
	
	provisionNewProjectButton.click(function(e) {
		e.preventDefault();
		
		step1.hide();
		step2.show();
		
		var projectName = projectNameField.val();
		console.log(JSON.stringify({ "name": projectName }));
		
		$.ajax({
				type: "post",
				dataType: "json",
				contentType: "application/json",
				url: "/projects/create", 
				data: JSON.stringify({ "name": projectName }), 
				success: function(data) {
					step2.hide();
					step3.show();
					
					var message = data.ok ? "Project successfully created!" : data.message;
					var progressBarColor = data.ok ? "bar-success" : "bar-danger";
					
					newProjectMessage.find('strong').text(message);
					newProjectMessage.find('.bar').addClass(progressBarColor);
				}
		});
	});
	
	function close() {
		newProjectModal.modal('hide');

		step1.show();
		step2.hide();
		step3.hide();

		newProjectMessage.find('strong').text("");
		newProjectMessage.find('.bar').removeClass(".bar-success");
		newProjectMessage.find('.bar').removeClass(".bar-danger");
		projectNameField.val("");
	}
	
});