package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.event.ComputeTargetCameraOffsetEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import Infinitygroup.imersive_cam.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public enum TaczShoulderCameraOffsetHandler implements ComputeTargetCameraOffsetEventHandler {
	INSTANCE;

	@Override
	public void handle(ComputeTargetCameraOffsetEvent event) {
		if (!TaczCompatBootstrap.getClientCompat().shouldUseShoulderCamera()) {
			return;
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || event.getCameraEntity() != player) {
			return;
		}
		Vec3 modifiers = Config.CLIENT.getCameraConfig().getTaczGunOffsetModifiers();
		event.setResult(event.getResult().add(modifiers));
	}
}
