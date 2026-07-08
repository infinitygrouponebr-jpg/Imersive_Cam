package Infinitygroup.imersive_cam.client.event.handler;

import Infinitygroup.imersive_cam.api.client.event.ComputeCameraCouplingEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputeCameraCouplingEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public enum ComputeCameraCouplingEventHandlerImpl implements ComputeCameraCouplingEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeCameraCouplingEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			event.setResult(player.getVehicle() instanceof AbstractMinecart);
		}
	}
}
