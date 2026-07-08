package Infinitygroup.imersive_cam.mixin;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CompassItemPropertyFunction.class)
public class CompassItemPropertyFunctionMixin {
	@Redirect(
		method = "getWrappedVisualRotationY",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/world/entity/Entity.getVisualRotationYInDegrees()F"
		)
	)
	private static float getVisualRotationYInDegrees(Entity entity) {
		IImersiveCam instance = IImersiveCam.getInstance();
		if (instance.isImersiveCam() && instance.isCameraDecoupled()) {
			return instance.getCamera().getYRot();
		}
		return entity.getVisualRotationYInDegrees();
	}
}
