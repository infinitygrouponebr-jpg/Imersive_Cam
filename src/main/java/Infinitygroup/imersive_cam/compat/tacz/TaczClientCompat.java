package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.config.Config;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateContext;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class TaczClientCompat implements ITaczClientCompat {
	private static final float ADS_FADE_START = 0.60F;
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public boolean isHoldingGun() {
		LocalPlayer player = Minecraft.getInstance().player;
		return player != null && IGun.mainHandHoldGun(player);
	}
	
	@Override
	public boolean shouldRenderGunCrosshair(float partialTick) {
		return this.getGunCrosshairAlpha(partialTick) > 0.0F;
	}
	
	@Override
	public float getGunCrosshairAlpha(float partialTick) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (minecraft.level == null || player == null || !player.isAlive() || player.isSpectator()) {
			return 0.0F;
		}
		if (minecraft.options.hideGui || minecraft.screen != null || !IImersiveCam.getInstance().isImersiveCam()) {
			return 0.0F;
		}
		if (!Config.CLIENT.getCrosshairConfig().isTaczCrosshairEnabled() || !IGun.mainHandHoldGun(player)) {
			return 0.0F;
		}
		if (!(player instanceof IClientPlayerGunOperator clientOperator) || !(player instanceof IGunOperator gunOperator)) {
			return 0.0F;
		}
		ItemStack stack = player.getMainHandItem();
		if (isReloading(gunOperator) || shouldHideCrosshairByAnimation(stack)) {
			return 0.0F;
		}
		float aimingProgress = clientOperator.getClientAimingProgress(partialTick);
		boolean forceShowCrosshair = shouldForceShowCrosshair(stack);
		if (!Config.CLIENT.getCrosshairConfig().hideTaczCrosshairDuringAds() || forceShowCrosshair) {
			return 1.0F;
		}
		float hideThreshold = (float) Config.CLIENT.getCrosshairConfig().getTaczCrosshairAdsHideThreshold();
		return calcAdsAlpha(aimingProgress, hideThreshold);
	}
	
	private static boolean isReloading(IGunOperator operator) {
		ReloadState reloadState = operator.getSynReloadState();
		return reloadState != null && reloadState.getStateType().isReloading();
	}
	
	private static boolean shouldForceShowCrosshair(ItemStack stack) {
		return getGunDisplay(stack)
			.map(GunDisplayInstance::isShowCrosshair)
			.orElse(false);
	}
	
	private static boolean shouldHideCrosshairByAnimation(ItemStack stack) {
		Optional<GunDisplayInstance> display = getGunDisplay(stack);
		if (display.isEmpty()) {
			return false;
		}
		AnimationStateMachine<?> stateMachine = display.get().getAnimationStateMachine();
		if (stateMachine == null) {
			return false;
		}
		AnimationStateContext context = stateMachine.getContext();
		return context != null && context.shouldHideCrossHair();
	}
	
	private static Optional<GunDisplayInstance> getGunDisplay(ItemStack stack) {
		return TimelessAPI.getGunDisplay(stack);
	}
	
	private static float calcAdsAlpha(float aimingProgress, float hideThreshold) {
		float clampedProgress = Math.clamp(aimingProgress, 0.0F, 1.0F);
		float clampedThreshold = Math.clamp(hideThreshold, 0.0F, 1.0F);
		if (clampedProgress >= clampedThreshold) {
			return 0.0F;
		}
		if (clampedProgress <= ADS_FADE_START || clampedThreshold <= ADS_FADE_START) {
			return 1.0F;
		}
		float alpha = 1.0F - (clampedProgress - ADS_FADE_START) / (clampedThreshold - ADS_FADE_START);
		return Math.clamp(alpha, 0.0F, 1.0F);
	}
}
