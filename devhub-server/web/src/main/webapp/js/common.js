function isPasswordOk(password) {
	return password != undefined
		&& password.length >= 8
		&& /[a-z]/.test(password)
		&& /[A-Z]/.test(password)
		&& /\d/.test(password);
}

function verify(field, regex, callback) {
	var checker = function() {
		var value = $(field).val();
		if (value != undefined && value.match(regex)) {
			if (callback != undefined) {
				callback.call(this, value, function(valid) {
					setInputState(field, valid);
				});
			}
			else {
				setInputState(field, true);
			}
		}
		else {
			setInputState(field, false);
		}
	};
	
	return setInterval(checker, 100);
}

function verifyCheckbox(box, field, regex) {
	var checker = function() {
		var value = $(field).val();
		var isChecked = $(box).is(":checked");
		if (isChecked) {
			if (value != undefined && value.match(regex)) {
				setInputState(field, true);
			}
			else {
				setInputState(field, false);
			}
		}
		else {
			setInputState(field, true);
		}
	};
	
	return setInterval(checker, 100);
}

function synchronize(btn, inputs) {
	var checker = function() {
		var valid = true;
		for (var i = 0; i < inputs.length; i++) {
			if (!isValid(inputs[i])) {
				valid = false;
				break;
			}
		}
		setButtonState($(btn), valid);
	}
	
	return setInterval(checker, 100);
}

function stopTimers(timers) {
	for (var i = 0; i < timers.length; i++) {
		clearInterval(timers[i]);
	}
}


function setInputState(field, valid) {
	var controlGroup = field.parentsUntil('.control-group').parent();
	if (valid) {
		controlGroup.removeClass("error");
	} else {
		controlGroup.addClass("error");
	}
}

function isValid(field) {
	var controlGroup = field.parentsUntil('.control-group').parent();
	return !controlGroup.hasClass("error");
}

function setButtonState(button, valid) {
	if (valid) {
		button.removeAttr("disabled");
	} else {
		button.attr("disabled", "disabled");
	}
}

function displayProcessor() {
	$("body").append("<div class='preloader' style='z-index: 2000;'><div class='processing'><img src='/img/processing.gif'></div></div>")
}

function removeProcessor() {
	$(".preloader").remove();
}

function clearAlerts() {
	$('.alerts').empty();
}

function showAlert(type, title, message) {
	var content = title;
	if (message != undefined && message.length > 0) {
		content = "<strong>" + title + "</strong> " + message;
	}
	
	$('.alerts').empty().append(
			"<div class=\"alert " + type + "\">" 
			+ "<a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>" 
			+ content + "</div>");
}