package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.event.ComputePlayerAimStateEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputePlayerAimStateEventHandler;
import Infinitygroup.imersive_cam.api.event.IEventBus;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.player.LocalPlayer;

public enum TaczAimStateEventHandler implements ComputePlayerAimStateEventHandler {
	INSTANCE;
	
	private static final float AIM_PROGRESS_EPSILON = 0.001F;
	
	public static void register(IEventBus eventBus) {
		eventBus.register(1000, INSTANCE);
	}
	
	@Override
	public void handle(ComputePlayerAimStateEvent event) {
		if (event.getResult()) {
			return;
		}
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}
		if (!IGun.mainHandHoldGun(player)) {
			return;
		}
		if (!(player instanceof IClientPlayerGunOperator operator)) {
			return;
		}
		boolean activelyAiming = operator.isAim();
		float aimingProgress = operator.getClientAimingProgress(1.0F);
		boolean aimTransitionActive = aimingProgress > AIM_PROGRESS_EPSILON;
		if (activelyAiming || aimTransitionActive) {
			event.setResult(true);
		}
	}
}
