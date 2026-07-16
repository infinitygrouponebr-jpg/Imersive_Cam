package Infinitygroup.imersive_cam.compat.tacz;

import com.tacz.guns.api.event.common.GunFireEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;

public enum TaczServerFireAlignmentHandler {
	INSTANCE;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGunFire(GunFireEvent event) {
		if (event.getLogicalSide() != LogicalSide.SERVER) {
			return;
		}
		if (!(event.getShooter() instanceof ServerPlayer player)) {
			return;
		}
		TaczServerAimState.Sample sample = TaczServerAimState.INSTANCE.getValidSample(player);
		if (sample == null) {
			return;
		}
		player.setXRot(sample.pitch());
		player.setYRot(sample.yaw());
	}
}
