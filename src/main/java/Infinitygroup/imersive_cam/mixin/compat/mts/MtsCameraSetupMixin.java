package Infinitygroup.imersive_cam.mixin.compat.mts;

import Infinitygroup.imersive_cam.compat.mts.MtsCompatBootstrap;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Camera.class, priority = 900)
public abstract class MtsCameraSetupMixin {
	@Invoker("setPosition")
	protected abstract void imersivecam$setPosition(double x, double y, double z);

	@Inject(method = "setup", at = @At("TAIL"))
	private void imersivecam$applyMtsCameraOffset(
		BlockGetter level,
		Entity entity,
		boolean detached,
		boolean thirdPersonReverse,
		float partialTick,
		CallbackInfo ci
	) {
		Vec3 adjustedPosition = MtsCompatBootstrap.adjustCameraAfterMts((Camera) (Object) this, partialTick);
		if (adjustedPosition != null) {
			this.imersivecam$setPosition(adjustedPosition.x(), adjustedPosition.y(), adjustedPosition.z());
		}
	}
}
