
$(document).ready(function() {
	
	if (window.location.hash == undefined || window.location.hash == "") {
		switchOS(getOS());
	}
	else {
		var os = window.location.hash.substring(1, 4);
		var hash = window.location.hash.substring(1);
		switchOS(os, false);
		if (hash != undefined && hash.length > 3) {
			highlight(hash);
		}
	}
	
	$(".page-header .win").click(function(e) {
		e.preventDefault();
		switchOS("win");
	});
	
	$(".page-header .mac").click(function(e) {
		e.preventDefault();
		switchOS("mac");
	});
	
	$(".page-header .linux").click(function(e) {
		e.preventDefault();
		switchOS("linux");
	});
	
	function switchOS(os, overrideHash) {
		selectOS(os);
		highlightOSbuttons(os);
		if (overrideHash == undefined || overrideHash) {
			window.location.hash = os;
		}
	}

	function selectOS(os) {
		if (os == "win") {
			$(".contents .mac").hide();
			$(".contents .linux").hide();
			$(".contents .win").show();
		}
		else if (os == "mac") {
			$(".contents .win").hide();
			$(".contents .linux").hide();
			$(".contents .mac").show();
		}
		else if (os == "linux") {
			$(".contents .win").hide();
			$(".contents .mac").hide();
			$(".contents .linux").show();
		}
	}
	
	function highlightOSbuttons(os) {
		if (os == "win") {
			$(".page-header .mac").fadeTo(500, 0.3);
			$(".page-header .linux").fadeTo(500, 0.3);
			$(".page-header .win").fadeTo(500, 1.0);
		}
		else if (os == "mac") {
			$(".page-header .win").fadeTo(500, 0.3);
			$(".page-header .linux").fadeTo(500, 0.3);
			$(".page-header .mac").fadeTo(500, 1.0);
		}
		else if (os == "linux") {
			$(".page-header .win").fadeTo(500, 0.3);
			$(".page-header .mac").fadeTo(500, 0.3);
			$(".page-header .linux").fadeTo(500, 1.0);
		}
	}
	
	function getOS() {
		if (navigator.appVersion.indexOf("Win")!=-1) return "win";
		if (navigator.appVersion.indexOf("Mac")!=-1) return "mac";
		return "linux";
	}
	
	$("a.slide").click(function(e) {
		e.preventDefault();
		slideAndHighlight($(this).attr("href").substring(1));
	});
	
	function slideAndHighlight(anchor) {
		slide(anchor);
		highlight(anchor);
	}
	
	function slide(anchor) {
		var href = $("a[id=" + anchor + "]");
		var step = $(href).parent();

		$('html, body').animate({ scrollTop: $(step).offset().top - 100 }, 500);
		window.location.hash = anchor;
	}
	
	function highlight(anchor) {
		var href = $("a[id=" + anchor + "]");
		var step = $(href).parent();
		
		step.delay(400).animate({ 'background-color': '#BDD19C' }, 700, function() {
			step.animate({ 'background-color': '#ffffff' }, 700);
		});
	}
	
});
