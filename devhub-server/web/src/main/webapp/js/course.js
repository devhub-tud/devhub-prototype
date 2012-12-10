$(document).ready(function() {

	var downloadModal = $('#download-modal');
	var prepareDownloadDiv = $('#prepare-download');
	var downloadReadyDiv = $('#download-ready');
	var downloadHash = $('#download-hash');
	var downloadLink = $('#download-link');
	
	downloadReadyDiv.hide();
	$("#download-btn").click(function() {
		downloadModal.modal('show');
		$.ajax({
			url: "/api/course/" + courseId + "/download", 
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
		downloadLink.attr('href','/api/course/download/' + hash)
		prepareDownloadDiv.hide();
		downloadReadyDiv.show();
	}
});