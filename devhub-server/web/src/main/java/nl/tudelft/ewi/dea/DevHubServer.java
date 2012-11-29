package nl.tudelft.ewi.dea;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class DevHubServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(getSetting(args, "port", 8080));
		server.setHandler(buildWebAppContext());
		server.start();

		server.join();
	}

	public static WebAppContext buildWebAppContext() throws Exception {
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setWar(new File("src/main/webapp/").getAbsolutePath());
		return webAppContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSetting(String[] args, String settingName, T defaultValue) {
		for (String arg : args) {
			if (arg.startsWith(settingName + "=")) {
				String value = arg.substring(settingName.length() + 1);
				if (defaultValue instanceof Integer) {
					return (T) (Object) Integer.parseInt(value);
				}
				else if (defaultValue instanceof Boolean) {
					return (T) (Object) value.toLowerCase().equals("true");
				}
				else {
					return (T) value;
				}
			}
		}
		return defaultValue;
	}
}
