package Infinitygroup.imersive_cam.mixin;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.api.client.Perspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow
	private @Final Minecraft minecraft;
	
	@Redirect(
		method = "renderCrosshair",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isCrosshairVisible(CameraType cameraType) {
		return IImersiveCam.getInstance().getCrosshairRenderer().isCrosshairVisible();
	}
	
	@Redirect(
		method = "renderCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType) {
		return cameraType.isFirstPerson() || Perspective.IMERSIVE_CAMERA == Perspective.current() && this.minecraft.player.isScoping();
	}
}
