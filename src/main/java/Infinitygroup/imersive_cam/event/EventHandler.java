package Infinitygroup.imersive_cam.event;

import Infinitygroup.imersive_cam.api.event.Event;
import Infinitygroup.imersive_cam.plugin.PluginContainer;

import java.util.function.Consumer;

record EventHandler(
	int priority,
	int index,
	PluginContainer plugin,
	Consumer<Event> consumer
) {
	void handle(Event event) {
		this.consumer.accept(event);
	}
}
