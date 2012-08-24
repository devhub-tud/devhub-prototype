package nl.tudelft.ew.dea;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test if you are working with Java 7.
 * 
 */
public class Java7CompatibilityTest {

	private static final Logger LOG = LoggerFactory.getLogger(Java7CompatibilityTest.class);

	@Test
	public void testStringSwitch() {
		switch ("aap") {
			case "aap":
				break;

			default:
				Assert.fail("Aap should have been asserted");
		}
	}

	@Test
	public void variablesWithUnderscores() {
		long someNumber = 111_001L;
		long withoutUnderscores = 111001L;
		assertEquals(someNumber, withoutUnderscores);
	}

	@Test
	public void easyCollectionMaking() {
		List<Map<Date, String>> listOfMaps = new ArrayList<>();
		HashMap<Date, String> aMap = new HashMap<>();
		aMap.put(new Date(), "Hello");
		listOfMaps.add(aMap);
	}

	@Test
	public void testMultiCatch() {
		try {
			Class string = Class.forName("java.lang.String");
			string.getMethod("length").invoke("test");
		} catch (ClassNotFoundException |
				IllegalAccessException |
				IllegalArgumentException |
				InvocationTargetException |
				NoSuchMethodException |
				SecurityException e) {
			fail("Should not have been initialized");
		}
	}

	@Test
	public void tryWithResource() throws Exception {
		String file1 = "target/TryWithResourceFile.out";
		try (OutputStream out = new FileOutputStream(file1)) {
			out.write("Some silly file content ...".getBytes());
			":-p".charAt(3);
		} catch (StringIndexOutOfBoundsException | IOException e) {
			LOG.info("Exception on operating file " + file1 + ": " + e.getMessage());
		}
		// Out automagically closes now
	}
}
