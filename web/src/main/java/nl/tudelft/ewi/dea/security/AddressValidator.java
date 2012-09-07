package nl.tudelft.ewi.dea.security;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public final class AddressValidator {

	private static final Pattern TU_MAIL_PATTERN = Pattern.compile("([\\w]+\\.)+[\\w\\-]+@(student.)?tudelft\\.nl$");

	public static boolean isTuAddress(@Nullable String address) {
		return TU_MAIL_PATTERN.matcher(nullToEmpty(address)).matches();
	}

	/**
	 * Throws an {@link InvalidAddressException} if the mail address is not
	 * valid.
	 */
	public static void verifyTuAdress(@Nullable String address) throws InvalidAddressException {
		if (!isTuAddress(address)) {
			throw new InvalidAddressException(address);
		}
	}

	private AddressValidator() {};
}
