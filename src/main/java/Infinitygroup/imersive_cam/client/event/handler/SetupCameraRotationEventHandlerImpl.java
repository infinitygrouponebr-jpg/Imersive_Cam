package Infinitygroup.imersive_cam.client.event.handler;

import Infinitygroup.imersive_cam.api.client.event.SetupCameraRotationEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.SetupCameraRotationEventHandler;
import Infinitygroup.imersive_cam.api.util.EntityHelper;

public enum SetupCameraRotationEventHandlerImpl implements SetupCameraRotationEventHandler {
	INSTANCE;
	
	@Override
	public void handle(SetupCameraRotationEvent event) {
		if (event.getPlayer().isPassenger()) {
			event.setResult(EntityHelper.applyPassengerRotationConstraints(event.getPlayer(), event.getResult(), event.getCameraRotO()));
		}
	}
}
