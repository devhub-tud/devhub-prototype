$(document).ready(function() {

	var downloadModal = $('#download-modal');
	var prepareDownloadDiv = $('#prepare-download');
	var downloadReadyDiv = $('#download-ready');
	var downloadHash = $('#download-hash');
	var downloadLink = $('#download-link');
	
	if (hasNoProjects) {
		$("#download-btn").attr("disabled", "disabled");
	}
	
	downloadReadyDiv.hide();
	$("#download-btn").click(function() {
		downloadModal.modal('show');
		$.ajax({
			url: "/api/courses/" + courseId + "/download", 
			success: function(data) {
				download(data);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("error " + textStatus);
				console.log(errorThrown);
			}
		});
	})
	
	function download(hash) {
		downloadHash.text(hash);
		downloadLink.attr('href','/api/courses/download/' + hash)
		prepareDownloadDiv.hide();
		downloadReadyDiv.show();
	}
});