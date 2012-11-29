$(document).ready(function() {
	
	var invitationsBlock = $("#invitations");
	
	invitationsBlock.find(".join").click(function() {
		var td = $(this).parentsUntil("td").parent();
		var projectId = td.attr("projectId");
		if (projectId != undefined && projectId >= 0) {
			sendResponse(td, projectId, true);
		}
	});
	
	invitationsBlock.find(".reject").click(function() {
		var td = $(this).parentsUntil("td").parent();
		var projectId = td.attr("projectId");
		if (projectId != undefined && projectId >= 0) {
			sendResponse(td, projectId, false);
		}
	});
	
	function sendResponse(td, projectId, accept) {
		$.ajax({
			type: "post",
			url: "/api/project/" + projectId + "/invitation?accept=" + accept,
			success: function(data) {
				if (accept) {
					addProjectToOverview(td);
				}
				updateInvitationsBlock(td);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.responseText);
			}
		});
	}
	
	function addProjectToOverview(td) {
		var projectName = td.find(".project-name").html();
		var projectDescription = "Project description goes here...";
		var projectId = td.attr("projectId");
		
		$(".projects").append("<a class=\"item\" href=\"/project/" + projectId + "\"><span class=\"title\">" + projectName + "</span><span class=\"subtitle muted\">" + projectDescription + "</span></a>");
	}
	
	function updateInvitationsBlock(td) {
		var tr = td.parent();
		var buttonPanel = tr.find(".btn-panel");
		buttonPanel.remove();
		if (invitationsBlock.find(".btn-panel").length == 0) {
			invitationsBlock.delay(600).fadeOut(600, function() { invitationsBlock.remove(); });
		}
	}
	
});