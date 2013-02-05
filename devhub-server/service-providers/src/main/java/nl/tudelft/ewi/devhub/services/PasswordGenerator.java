package nl.tudelft.ewi.devhub.services;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordGenerator {

	private PasswordGenerator() {
		// Prevent instantiation.
	}

	public static String generate() {
		return RandomStringUtils.randomAlphanumeric(12);
	}
}
