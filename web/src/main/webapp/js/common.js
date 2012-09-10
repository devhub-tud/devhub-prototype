function isPasswordOk(password) {
	return password != undefined
		&& password.length >= 8
		&& /[a-z]/.test(password)
		&& /[A-Z]/.test(password)
		&& /\d/.test(password)
		&& /[!,@,#,$,%,^,&,*,?,_,~]/.test(password);
}