package nl.tudelft.ewi.devhub.sonarplugin;

import java.util.List;

import org.sonar.api.ServerComponent;
import org.sonar.api.SonarPlugin;

import com.google.common.collect.ImmutableList;

/**
 * This class is the entry point for all extensions
 */
public class DevHubPlugin extends SonarPlugin {

	public List<Class<? extends ServerComponent>> getExtensions() {
		return ImmutableList.of(DevHubRealm.class, NewProjectPermissions.class);
	}

}
