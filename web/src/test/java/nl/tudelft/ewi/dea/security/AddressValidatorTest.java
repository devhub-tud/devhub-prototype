package nl.tudelft.ewi.dea.security;

import static nl.tudelft.ewi.dea.security.AddressValidator.isTuAddress;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AddressValidatorTest {

	@Test
	public void acceptStudentAddress() {
		assertThat(isTuAddress("john.doe@student.tudelft.nl"), is(true));
	}

	@Test
	public void acceptEmployeeAddress() {
		assertThat(isTuAddress("john.doe@tudelft.nl"), is(true));
	}
	
	@Test
	public void acceptDashesAndNumbers() {
		assertThat(isTuAddress("john.doe-1@tudelft.nl"), is(true));
	}
	
	@Test
	public void acceptuUnderscores() {
		assertThat(isTuAddress("john.doe_1@tudelft.nl"), is(true));
	}

	@Test
	public void rejectOtherEmailAddress() {
		assertThat(isTuAddress("john.doe@gmail.com"), is(false));
		assertThat(isTuAddress("john.doe@hotmail.com"), is(false));
	}

	@Test
	public void rejectEmptyTUAddress() {
		assertThat(isTuAddress("john.doe"), is(false));
		assertThat(isTuAddress(""), is(false));
		assertThat(isTuAddress(null), is(false));
	}

	@Test
	public void rejectIllegalCharacters() {
		assertThat(isTuAddress("john.doe#@tudelft.nl"), is(false));
		assertThat(isTuAddress("john.doe@@tudelft.nl"), is(false));
		assertThat(isTuAddress("john.doe$@tudelft.nl"), is(false));
	}
}
