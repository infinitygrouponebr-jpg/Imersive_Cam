package Infinitygroup.imersive_cam.client.renderer;

import Infinitygroup.imersive_cam.api.client.renderer.ICameraEntityRenderer;
import Infinitygroup.imersive_cam.api.util.EntityHelper;
import Infinitygroup.imersive_cam.client.EventHooks;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.client.ImersiveCamCamera;
import Infinitygroup.imersive_cam.config.Config;
import net.minecraft.util.FastColor;
import net.minecraft.util.FastColor.ABGR32;
import net.minecraft.world.entity.Entity;

public class CameraEntityRenderer implements ICameraEntityRenderer {
	private final ImersiveCam instance;
	private float cameraEntityAlpha = 1.0F;
	private boolean isRenderingCameraEntity;
	
	public CameraEntityRenderer(ImersiveCam instance) {
		this.instance = instance;
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick) {
		if (this.isCameraEntityRenderingSkipped(entity)) {
			return true;
		}
		if (this.instance.isImersiveCam() && Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			this.cameraEntityAlpha = EventHooks.getCameraEntityAlpha(entity, partialTick);
		} else {
			this.cameraEntityAlpha = 1.0F;
		}
		this.isRenderingCameraEntity = true;
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick) {
		this.isRenderingCameraEntity = false;
	}
	
	private boolean isCameraEntityRenderingSkipped(Entity cameraEntity) {
		if (!this.instance.isImersiveCam() || cameraEntity.isSpectator()) {
			return false;
		}
		ImersiveCamCamera camera = this.instance.getCamera();
		if (camera.isInsideEntity(cameraEntity)) {
			return true;
		} else if (camera.isLookingUp()) {
			return true;
		}
		return EntityHelper.isScoping(cameraEntity);
	}
	
	public int applyCameraEntityAlphaContextAware(int color) {
		return this.isRenderingCameraEntity ? this.applyCameraEntityAlpha(color) : color;
	}
	
	public int applyCameraEntityAlpha(int color) {
		int cameraEntityAlpha = this.getCameraEntityAlphaAsInt();
		int alpha = ABGR32.alpha(color);
		if (cameraEntityAlpha < alpha) {
			return ABGR32.transparent(color) + (cameraEntityAlpha << 24);
		}
		return color;
	}
	
	@Override
	public boolean isRenderingCameraEntity() {
		return this.isRenderingCameraEntity;
	}
	
	@Override
	public float getCameraEntityAlpha() {
		return this.cameraEntityAlpha;
	}
	
	@Override
	public int getCameraEntityAlphaAsInt() {
		return FastColor.as8BitChannel(this.cameraEntityAlpha);
	}
}
