package nl.tudelft.ewi.dea.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UserTest {

	@Test
	public void whenAUserIsCreatedARandomSaltIsCreated() {
		User newUserWithRandomSalt = User.newUserWithRandomSalt("Harry", "harry@potter.com", "shazam");
		assertThat(newUserWithRandomSalt.getSalt(), is(not(isEmptyOrNullString())));
	}

	@Test
	public void whenUserIsCreatedTheSaltIsUnique() {
		User oneUser = User.newUserWithRandomSalt("Harry", "harry@potter.com", "shazam");
		User otherUser = User.newUserWithRandomSalt("Harry", "harry@potter.com", "shazam");
		assertThat(oneUser.getSalt(), is(not(otherUser.getSalt())));
	}

}
