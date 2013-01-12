package nl.tudelft.ewi.devhub.services;

import java.util.Random;

public class PasswordGenerator {

	private static final String AVOID = "'\"`()[]{}*+-/\\.:;?<>=~|";

	private PasswordGenerator() {
		// Prevent instantiation.
	}

	public static String generate() {
		Random random = new Random(System.nanoTime());
		StringBuilder builder = new StringBuilder();

		while (builder.length() < 10) {
			int randomInt = random.nextInt(127 - 33) + 33;
			char c = (char) randomInt;
			if (AVOID.indexOf(c) != -1) {
				builder.append(c);
			}
		}

		return builder.toString();
	}
}
