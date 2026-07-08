package Infinitygroup.imersive_cam.mixin;

import Infinitygroup.imersive_cam.client.ImersiveCam;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelPart.Cube.class)
public class ModelPart$CubeMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "compile",
		index = 5,
		argsOnly = true
	)
	public int compile(int color) {
		return ImersiveCam.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
