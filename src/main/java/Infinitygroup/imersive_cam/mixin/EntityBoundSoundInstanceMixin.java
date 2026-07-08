package Infinitygroup.imersive_cam.mixin;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.config.Config;
import Infinitygroup.imersive_cam.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
public abstract class EntityBoundSoundInstanceMixin extends AbstractTickableSoundInstance {
	@Shadow
	private @Final Entity entity;
	
	protected EntityBoundSoundInstanceMixin(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
		super(soundEvent, soundSource, randomSource);
	}
	
	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(CallbackInfo ci) {
		if (this.entity == Minecraft.getInstance().getCameraEntity()) {
			this.correctSoundPositionIfNecessary();
		}
	}
	
	@Inject(
		method = "tick",
		at = @At("TAIL")
	)
	private void tick(CallbackInfo ci) {
		if (!this.entity.isRemoved() && this.entity == Minecraft.getInstance().getCameraEntity()) {
			this.correctSoundPositionIfNecessary();
		}
	}
	
	@Unique
	private void correctSoundPositionIfNecessary() {
		if (IImersiveCam.getInstance().isImersiveCam() && Config.CLIENT.getAudioConfig().isPlayerSoundCentered()) {
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(this.entity);
			this.x = pos.x();
			this.y = pos.y();
			this.z = pos.z();
		}
	}
}
