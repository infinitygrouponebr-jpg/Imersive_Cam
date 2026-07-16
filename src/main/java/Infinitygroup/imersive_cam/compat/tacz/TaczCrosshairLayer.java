package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.api.client.renderer.ICrosshairRenderer;
import Infinitygroup.imersive_cam.api.math.Vec2f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import static Infinitygroup.imersive_cam.ImersiveCamCommon.MOD_ID;

public final class TaczCrosshairLayer {
	public static final ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "tacz_crosshair");
	private static final ResourceLocation TACZ_CROSSHAIR_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/hud/tacz_crosshair.png");
	private static final int CROSSHAIR_SIZE = 16;
	
	private TaczCrosshairLayer() {
	}
	
	public static void render(GuiGraphics guiGraphics, float partialTick) {
		IImersiveCam instance = IImersiveCam.getInstance();
		if (!instance.isImersiveCam()) {
			return;
		}
		ICrosshairRenderer crosshairRenderer = instance.getCrosshairRenderer();
		if (crosshairRenderer.isObstructionCrosshairVisible() || crosshairRenderer.isObstructionIndicatorVisible()) {
			return;
		}
		float alpha = TaczCompatBootstrap.getClientCompat().getGunCrosshairAlpha(partialTick);
		if (alpha <= 0.0F) {
			return;
		}
		float x = (guiGraphics.guiWidth() - CROSSHAIR_SIZE) / 2.0F;
		float y = (guiGraphics.guiHeight() - CROSSHAIR_SIZE) / 2.0F;
		Vec2f crosshairOffset = crosshairRenderer.getCrosshairOffset();
		if (crosshairOffset != null) {
			x += crosshairOffset.x();
			y -= crosshairOffset.y();
		}
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.blit(TACZ_CROSSHAIR_SPRITE, Math.round(x), Math.round(y), 0, 0, CROSSHAIR_SIZE, CROSSHAIR_SIZE, CROSSHAIR_SIZE, CROSSHAIR_SIZE);
		} finally {
			guiGraphics.pose().popPose();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableBlend();
		}
	}
}
