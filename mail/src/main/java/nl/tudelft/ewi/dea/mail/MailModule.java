package nl.tudelft.ewi.dea.mail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Singleton;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

public class MailModule extends AbstractModule {

	private final MailProperties properties;
	private final Properties rawMailProperties;
	private final ExecutorService executor;

	public MailModule(MailProperties properties, ExecutorService executor) {
		this.properties = properties;
		this.executor = executor;
		rawMailProperties = configure(properties);
	}

	public MailModule(MailProperties properties) {
		this.properties = properties;
		this.executor = Executors.newCachedThreadPool();
		rawMailProperties = configure(properties);
	}

	@Override
	protected void configure() {
		bind(MailProperties.class).toInstance(properties);
		bind(ExecutorService.class).toInstance(executor);
		bind(Session.class).toInstance(Session.getDefaultInstance(rawMailProperties));
	}

	private Properties configure(MailProperties properties) {
		Properties props = new Properties(System.getProperties());
		props.put("mail.smtp.starttls.enable", properties.isSsl()); // added this
																						// line
		props.put("mail.smtp.host", properties.getHost());
		props.put("mail.smtp.user", properties.getUser());
		props.put("mail.smtp.password", properties.getPassword());
		props.put("mail.smtp.port", properties.getPort());
		props.put("mail.smtp.auth", properties.isAuth());
		return props;
	}

	@Provides
	@Singleton
	@MailQueue
	BlockingQueue<SimpleMessage> mailQueue() {
		return new LinkedBlockingQueue<SimpleMessage>();
	}

	@Provides
	@Singleton
	VelocityEngine velocityEngine() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
		engine.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		return engine;
	}

	@Provides
	@SMTP
	Transport mailTransport(Session session) {
		try {
			return session.getTransport("smtp");
		} catch (NoSuchProviderException e) {
			throw new MailException("Could not load mail Transport", e);
		}
	}

	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface MailQueue {};

	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface SMTP {};

}
