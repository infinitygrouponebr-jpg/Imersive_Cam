package Infinitygroup.imersive_cam.client.event.handler;

import Infinitygroup.imersive_cam.api.client.CrosshairType;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerInteractionStateEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import Infinitygroup.imersive_cam.api.client.world.phys.PickVector;
import Infinitygroup.imersive_cam.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

public class ComputePlayerInteractionStateEventHandlerImpl {
	public enum Pre implements ComputePlayerInteractionStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerInteractionStateEvent event) {
			if (Minecraft.getInstance().options.keyUse.isDown() && !event.getCameraEntity().isUsingItem()) {
				HitResult hitResult = Minecraft.getInstance().hitResult;
				if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
					event.setResult(true);
				}
			}
		}
	}
	
	public enum Post implements ComputePlayerInteractionStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerInteractionStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			} else if (Config.CLIENT.getObjectPickerConfig().getPickVector() == PickVector.PLAYER && Config.CLIENT.getCrosshairConfig().getCrosshairType() == CrosshairType.DYNAMIC) {
				event.setResult(false);
			}
		}
	}
}
