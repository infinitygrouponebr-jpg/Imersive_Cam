package Infinitygroup.imersive_cam.client.event.handler;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import Infinitygroup.imersive_cam.config.Config;

public enum ComputeTemporaryFirstPersonStateEventHandlerImpl implements ComputeTemporaryFirstPersonStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeTemporaryFirstPersonStateEvent event) {
		if (!event.getResult()) {
			boolean result = switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
				case STATIC_WITH_1PP, DYNAMIC_WITH_1PP -> IImersiveCam.getInstance().isAiming();
				default -> false;
			};
			event.setResult(result);
		}
	}
}
