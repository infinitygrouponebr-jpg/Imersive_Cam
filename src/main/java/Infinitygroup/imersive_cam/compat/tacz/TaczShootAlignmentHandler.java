package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.renderer.CrosshairTargetSnapshot;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.config.Config;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;

public enum TaczShootAlignmentHandler {
	INSTANCE;
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGunShoot(GunShootEvent event) {
		if (event.getLogicalSide() != LogicalSide.CLIENT) {
			return;
		}
		if (!(event.getShooter() instanceof LocalPlayer player)) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != player || minecraft.level == null) {
			return;
		}
		ImersiveCam imersiveCam = ImersiveCam.getInstance();
		if (!imersiveCam.isImersiveCam()) {
			return;
		}
		if (!Config.CLIENT.getCrosshairConfig().isTaczShotAlignmentEnabled()) {
			return;
		}
		if (!IGun.mainHandHoldGun(player)) {
			return;
		}
		CrosshairTargetSnapshot snapshot = imersiveCam
			.getCrosshairRenderer()
			.getCrosshairTargetSnapshot();
		TaczAimSyncState.INSTANCE.lockTarget(snapshot, TaczAimSyncState.DEFAULT_SYNC_HOLD_TICKS);
		boolean aligned = imersiveCam.alignPlayerToCrosshairSnapshot(snapshot);
		if (!aligned) {
			imersiveCam.lookAtCrosshairTarget();
		}
		TaczAimSyncState.INSTANCE.syncImmediately(imersiveCam);
	}
}
