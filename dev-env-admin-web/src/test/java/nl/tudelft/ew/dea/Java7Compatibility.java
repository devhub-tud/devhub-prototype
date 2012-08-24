package nl.tudelft.ew.dea;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test if you are working with Java 7.
 * 
 */
public class Java7Compatibility {

	@Test
	public void testStringSwitch() {
		switch ("aap") {
			case "aap":
				break;

			default:
				Assert.fail("Aap should have been asserted");
		}
	}
}
