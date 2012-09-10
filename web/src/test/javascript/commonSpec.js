describe("Password test", function() {
	it("should accept A password longer then 8, with big and small cases and both a  symbol and a number.", function() {
		expect(isPasswordOk("Thisisagoodpassword1!")).toBe(true);
	});
	
	it("should not accept undefined passwords", function() {
		expect(isPasswordOk(null)).toBe(false);
	});
	
	it("should not too short passwords ", function() {
		expect(isPasswordOk("false")).toBe(false);
	});
	
	it("should not accept without a number.", function() {
		expect(isPasswordOk("Thisisagoodpassword!")).toBe(false);
	});
	
	it("should not accept without a symbol.", function() {
		expect(isPasswordOk("Thisisagoodpassword1")).toBe(false);
	});
	
	it("should not accept without a capital case.", function() {
		expect(isPasswordOk("thisisagoodpassword1!")).toBe(false);
	});
	
	it("should not accept without a small case.", function() {
		expect(isPasswordOk("THISISAGOODPASSWORD1!")).toBe(false);
	});
});