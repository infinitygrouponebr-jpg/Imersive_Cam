package Infinitygroup.imersive_cam.client.event.handler;

import Infinitygroup.imersive_cam.api.client.event.ComputePlayerRideBoatStateEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputePlayerRideBoatStateEventHandler;
import net.minecraft.world.entity.vehicle.Boat;

public enum ComputePlayerRideBoatStateEventHandlerImpl implements ComputePlayerRideBoatStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputePlayerRideBoatStateEvent event) {
		if (event.getVehicle() instanceof Boat) {
			event.setResult(true);
		}
	}
}
