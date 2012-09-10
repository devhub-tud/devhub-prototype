function isPasswordOk(password) {
	return password != undefined
		&& password.length >= 8
		&& password.match(/[a-z]/)
		&& password.match(/[A-Z]/)
		&& password.match(/\d/)
		&& password.match(/.*\d.*\d.*\d/)
		&& password.match(/[!,@,#,$,%,^,&,*,?,_,~]/);
}