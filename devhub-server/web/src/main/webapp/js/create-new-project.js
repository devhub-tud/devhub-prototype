$(document).ready(function() {

	var newProjectModal = $('#create-new-project-modal');
	var projectNameField = $('#project-name');
	var newProjectButton = $('#create-new-project');
	var provisionNewProjectButton = $('#provision-new-project');
	var newProjectForm = $('#create-new-project-form');
	var newProjectProgress = $('#create-new-project-progress');
	var newProjectMessage = $('#create-new-project-message');
	var doneBtn = $('#done-btn');
	
	var step1 = newProjectModal.find('.step-1');
	var step2 = newProjectModal.find('.step-2');
	var step3 = newProjectModal.find('.step-3');
	
	newProjectModal.on("hidden", function() {
		step1.show();
		step2.hide();
		step3.hide();

		newProjectMessage.find('strong').text("");
		newProjectMessage.find('.bar').removeClass(".bar-success");
		newProjectMessage.find('.bar').removeClass(".bar-danger");
		projectNameField.val("");
	});
	
	var lastValue;
	
	setInterval(function() {
		setTimeout(function() {
			var projectName = projectNameField.val();
			if (projectName != lastValue) {
				lastValue = projectName;
				startProjectNameCheck(projectName);
			}
		}, 250);
	}, 100);
	
	function startProjectNameCheck(projectName) {
		if (projectName != projectNameField.val()) {
			return;
		}
		
		var controlGroup = projectNameField.parentsUntil('.control-group').parent();
		var loader = controlGroup.find(".loader");
		var helpBlock = controlGroup.find(".help-block");
		
		loader.show();
		helpBlock.hide();
		provisionNewProjectButton.attr("disabled", "disabled");
		
		checkProjectName(projectNameField.val(), function(result) {
			if (projectName == projectNameField.val()) {
				loader.hide();
				if (result == "ok") {
					controlGroup.removeClass("error warning");
					provisionNewProjectButton.removeAttr("disabled");
				}
				else if (result == "already-taken") {
					helpBlock.html("The project name <strong>" + projectNameField.val() + "</strong> is taken!").show();
					controlGroup.addClass("error");
					provisionNewProjectButton.attr("disabled", "disabled");
				}
				else if (result == "invalid-name") {
					helpBlock.html("Project names may only consist of letters and numbers, and must be at least 4 characters long!").show();
					controlGroup.addClass("error");
					provisionNewProjectButton.attr("disabled", "disabled");
				} 
				else {
					console.error("Unexpected result " + result);
					//No serious error handling.
				}
			}
		});
	}
	
	function checkProjectName(projectName, callback) {
		if (projectName == "") {
			callback.call(this, "invalid-name");
			return;
		}
		
		$.ajax({
				url: "/api/projects/checkName", 
				data: { "name": projectName }, 
				success: function(data) {
					callback.call(this, "ok");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					callback.call(this, jqXHR.responseText);
				}
		});
	}
	
	newProjectButton.click(function(e) {
		e.preventDefault();
		newProjectModal.modal('show');
	});
	
	provisionNewProjectButton.click(function(e) {
		e.preventDefault();
		
		step1.hide();
		step2.show();
		
		var projectName = projectNameField.val();
		$.ajax({
				type: "post",
				dataType: "text",
				contentType: "application/json",
				url: "/api/projects/create", 
				data: JSON.stringify({ "name": projectName }), 
				success: function() {
					step2.hide();
					step3.show();
					doneBtn.show();
					
					newProjectMessage.find('strong').text("Project successfully created!");
					newProjectMessage.find('.bar').addClass("bar-success");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					step2.hide();
					step3.show();
					
					newProjectMessage.find('strong').text(jqXHR.responseText);
					newProjectMessage.find('.bar').addClass("bar-danger");
				}
		});
	});
	
});