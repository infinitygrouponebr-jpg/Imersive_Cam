package Infinitygroup.imersive_cam.plugin;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.api.plugin.IImersiveCamPlugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class PluginLoader<T> {
	private static final PluginLoader<?> INSTANCE = ServiceLoader.load(PluginLoader.class).findFirst().orElseThrow();
	private static final String ENTRYPOINTS_KEY = "entrypoints";
	protected static final String PLUGIN_JSON_PATH = "imersive_cam_plugin.json";
	
	private final List<PluginContainer> plugins = new LinkedList<PluginContainer>();
	
	public abstract void loadPlugins();
	
	protected void loadPlugin(String modName, String modId, T source) {
		ImersiveCamCommon.LOGGER.info("Registering plugin for {} ({})", modName, modId);
		try (Reader reader = this.readConfiguration(source)) {
			JsonObject configuration = JsonParser.parseReader(reader).getAsJsonObject();
			if (configuration.has(ENTRYPOINTS_KEY)) {
				List<String> entrypoints = configuration.get(ENTRYPOINTS_KEY).getAsJsonArray().asList().stream()
					.distinct()
					.map(JsonElement::getAsString)
					.toList();
				if (entrypoints.isEmpty()) {
					ImersiveCamCommon.LOGGER.warn("Plugin for {} ({}) does not contain any entrypoints", modName, modId);
				}
				for (String entrypoint : entrypoints) {
					try {
						Class<?> entrypointClass = Class.forName(entrypoint);
						IImersiveCamPlugin plugin = (IImersiveCamPlugin) entrypointClass.getConstructor().newInstance();
						PluginContainer pluginContainer = new PluginContainer(modName, modId, plugin, entrypoint);
						this.plugins.add(pluginContainer);
					} catch (Throwable e) {
						ImersiveCamCommon.LOGGER.error("Failed to load entrypoint {} for {} ({})", entrypoint, modName, modId, e);
					}
				}
			} else {
				ImersiveCamCommon.LOGGER.error("Plugin for {} ({}) does not contain an entrypoints key", modName, modId);
			}
		} catch (Throwable e) {
			ImersiveCamCommon.LOGGER.error("Failed to load plugin for {} ({})", modName, modId, e);
		}
	}
	
	protected abstract Reader readConfiguration(T source) throws IOException;
	
	public List<PluginContainer> getPluginContainers() {
		return this.plugins;
	}
	
	public static PluginLoader<?> getInstance() {
		return INSTANCE;
	}
}
