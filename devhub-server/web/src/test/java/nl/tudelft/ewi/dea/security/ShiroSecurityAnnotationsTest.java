package nl.tudelft.ewi.dea.security;

import java.lang.annotation.Inherited;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.SessionScoped;

/**
 * Currently the {@link RequiresRoles} annotation is not working when using it
 * with Guice and Guice-servlets.
 * 
 * For instance when a Servlet or Resource is marked as {@link SessionScoped} or
 * {@link RequestScoped}, that class is proxied by Guice. But Guice fails to
 * relay annotation information (when using reflection) from the original class.
 * So Shiro will never know that the Servlet or Resource is annotated with
 * {@link RequiresRoles}.
 * 
 * One workaround would be to make the {@link RequiresRoles} annotation use the
 * {@link Inherited} annotation.
 * 
 * @author michael
 */
public class ShiroSecurityAnnotationsTest {

	private static final Logger LOG = LoggerFactory.getLogger(ShiroSecurityAnnotationsTest.class);

	@Test
	public void testThatNoClassIsAnnotatedWithRequiresRolesAnnotation() {
		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
		Set<Class<?>> classesWithRequiresRoles = reflections.getTypesAnnotatedWith(RequiresRoles.class);

		// Ignore third-party classes...
		for (Class<?> clazz : classesWithRequiresRoles) {
			if (!clazz.getPackage().getName().startsWith("nl.tudelft.ewi.dea")) {
				classesWithRequiresRoles.remove(clazz);
			}
		}

		if (!classesWithRequiresRoles.isEmpty()) {
			LOG.error("The following classes are annotated with @RequiresRoles while this is not supported:");
			for (Class<?> clazz : classesWithRequiresRoles) {
				LOG.error(" - " + clazz.getPackage().getName() + "." + clazz.getSimpleName());
			}
			Assert.fail();
		}
	}

}
