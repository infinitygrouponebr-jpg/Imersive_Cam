package Infinitygroup.imersive_cam.neoforge.event;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.api.client.IImersiveCam;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.client.renderer.CrosshairRenderer;
import Infinitygroup.imersive_cam.compat.tacz.TaczCrosshairLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Matrix4f;

public class ClientEventHandler {
	private static final ResourceLocation PRE_CROSSHAIR_LAYER = ResourceLocation.fromNamespaceAndPath(ImersiveCamCommon.MOD_ID, "pre_crosshair");
	private static final ResourceLocation POST_CROSSHAIR_LAYER = ResourceLocation.fromNamespaceAndPath(ImersiveCamCommon.MOD_ID, "post_crosshair");

	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event) {
		if (!Minecraft.getInstance().isPaused()) {
			ImersiveCam.getInstance().tick();
		}
	}

	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiLayerEvent.Pre event) {
		if (VanillaGuiLayers.CROSSHAIR.equals(event.getName())) {
			if (!IImersiveCam.getInstance().getCrosshairRenderer().isCrosshairVisible()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void registerGuiOverlaysEvent(RegisterGuiLayersEvent event) {
		event.registerBelow(
			VanillaGuiLayers.CROSSHAIR,
			PRE_CROSSHAIR_LAYER,
			(guiGraphics, deltaTracker) -> {
				CrosshairRenderer crosshairRenderer = ImersiveCam.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.preRenderCrosshair(guiGraphics);
				}
			}
		);
		event.registerAbove(
			VanillaGuiLayers.CROSSHAIR,
			POST_CROSSHAIR_LAYER,
			(guiGraphics, deltaTracker) -> {
				CrosshairRenderer crosshairRenderer = ImersiveCam.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.postRenderCrosshair(guiGraphics);
				}
			}
		);
		event.registerBelow(
			POST_CROSSHAIR_LAYER,
			TaczCrosshairLayer.LAYER_ID,
			(guiGraphics, deltaTracker) -> TaczCrosshairLayer.render(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false))
		);
	}

	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event) {
		if (RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage())) {
			float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			ImersiveCam instance = ImersiveCam.getInstance();
			Matrix4f modelViewMatrix = event.getModelViewMatrix();
			Matrix4f projectionMatrix = event.getProjectionMatrix();
			instance.getCamera().renderTick(camera.getEntity(), partialTick);
			instance.getCrosshairRenderer().renderTick(camera, modelViewMatrix, projectionMatrix, partialTick);
		}
	}

	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event) {
		ImersiveCam.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ImersiveCam.getInstance().updatePlayerRotations();
	}
}
