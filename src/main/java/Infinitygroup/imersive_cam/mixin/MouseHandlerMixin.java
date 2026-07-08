package Infinitygroup.imersive_cam.mixin;

import Infinitygroup.imersive_cam.api.client.Perspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Redirect(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType) {
		return cameraType.isFirstPerson() || Perspective.IMERSIVE_CAMERA.equals(Perspective.current());
	}
}
