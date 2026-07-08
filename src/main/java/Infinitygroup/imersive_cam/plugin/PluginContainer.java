package Infinitygroup.imersive_cam.plugin;

import Infinitygroup.imersive_cam.api.plugin.IImersiveCamPlugin;

public record PluginContainer(String modName, String modId, IImersiveCamPlugin instance, String entrypoint) {
}
