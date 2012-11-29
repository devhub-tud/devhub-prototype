package nl.tudelft.ewi.dea.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import nl.tudelft.ewi.dea.DevHubException;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * The {@link TemplateEngine} class is responsible for loading and parsing
 * template files found in the resources folder. Its main purpose is HTML
 * formatting for the web-frontend.
 */
public class TemplateEngine {

	private static final Logger LOG = LoggerFactory.getLogger(TemplateEngine.class);

	private final Map<String, Long> modificationTimes;
	private final VelocityEngine engine;

	private Path path;

	private final ExecutorService executor;

	/**
	 * Constructs a new {@link TemplateEngine} object, and initializes it. This
	 * constructor will throw a {@link RuntimeException} if initializing fails.
	 * 
	 * @param path The path containing all the templates.
	 * @param executor
	 */
	public TemplateEngine(Path path, ExecutorService executor) {
		this.executor = executor;
		try {
			this.path = path;
			this.modificationTimes = Maps.newHashMap();
			this.engine = new VelocityEngine();

			engine.setProperty("resource.loader", "string");
			engine.setProperty("runtime.log.logsystem.class", getPath(NullLogChute.class));
			engine.setProperty("string.resource.loader.class", getPath(StringResourceLoader.class));
			engine.setProperty("string.resource.loader.description", "Velocity StringResource loader");
			engine.setProperty("string.resource.loader.repository.class", getPath(StringResourceRepositoryImpl.class));
			engine.init();

			updateTemplates();

		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new DevHubException("Could not initialize the TemplateEngine", e);
		}
	}

	/**
	 * Start the watcher.
	 */
	public void watchForChanges() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					initWatcher();
				}
				catch (InterruptedException e) {
					LOG.debug("Template engine interrupted.");
				} catch (IOException e) {
					LOG.error("Template enginge stopped due to IO error: " + e.getLocalizedMessage(), e);
				}
			}
		});

	}

	private void initWatcher() throws IOException, InterruptedException {
		WatchService watcher = path.getFileSystem().newWatchService();
		path.register(watcher,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);

		LOG.info("Now watching template folder: " + path.toFile().getAbsolutePath());

		while (true) {
			WatchKey key = watcher.take();
			List<WatchEvent<?>> events = key.pollEvents();
			if (!events.isEmpty()) {
				updateTemplates();
			}
			key.reset();
		}
	}

	private void updateTemplates() {
		LOG.info("Updating template cache...");
		StringResourceRepository repo = StringResourceLoader.getRepository();
		URI uri = path.toUri();
		if (uri == null) {
			LOG.error("Could not locate templates folder!");
			return;
		}

		File dir = new File(uri);

		File[] templates = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				boolean isTemplateFile = arg1 != null && arg1.endsWith(".tpl");
				if (!isTemplateFile) {
					return false;
				}

				return fileHasBeenModified(new File(arg0, arg1));
			}
		});

		if (templates != null && templates.length > 0) {
			for (File template : templates) {
				repo.putStringResource(template.getName(), getTemplateFromResource(template.getName()));
			}
		}
	}

	private boolean fileHasBeenModified(File arg0) {
		synchronized (modificationTimes) {
			String name = arg0.getName();
			Long time = modificationTimes.get(name);
			long lastModified = arg0.lastModified();
			if (time == null || lastModified > time) {
				modificationTimes.put(name, lastModified);
				return true;
			}
			return false;
		}
	}

	private String getPath(Class<?> clazz) {
		Package clazzPackage = clazz.getPackage();
		String packagePath = clazzPackage.getName();
		if (packagePath != null && packagePath.length() > 0) {
			return packagePath + "." + clazz.getSimpleName();
		}
		return clazz.getSimpleName();
	}

	private String getTemplateFromResource(final String templatePath) {
		File file = new File(path.toFile(), templatePath);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			StringBuilder builder = new StringBuilder();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				builder.append(line + "\n");
			}

			return builder.toString();
		} catch (IOException ex) {
			LOG.error("Could not load template: " + templatePath);
			throw new DevHubException(ex);
		}
	}

	/**
	 * This method will return a {@link Template} object containing the requested
	 * template. This method will also throw a {@link RuntimeException} if the
	 * template could not be loaded, in which case you probably specified the
	 * wrong file.
	 * 
	 * @param templatePath The template file to load.
	 * 
	 * @return The loaded {@link Template} object.
	 */
	public Template getTemplate(final String templatePath) {
		synchronized (engine) {
			if (!engine.resourceExists(templatePath)) {
				StringResourceRepository repo = StringResourceLoader.getRepository();
				repo.putStringResource(templatePath, getTemplateFromResource(templatePath));
			}

			try {
				return engine.getTemplate(templatePath);
			} catch (Exception e) {
				LOG.error(e.getLocalizedMessage(), e);
				throw new DevHubException(e);
			}
		}
	}

}
