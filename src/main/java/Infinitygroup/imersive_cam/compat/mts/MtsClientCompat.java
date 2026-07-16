package Infinitygroup.imersive_cam.compat.mts;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.config.Config;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;
import minecrafttransportsimulator.entities.instances.PartSeat;
import minecrafttransportsimulator.mcinterface.IWrapperPlayer;
import minecrafttransportsimulator.mcinterface.InterfaceManager;
import minecrafttransportsimulator.systems.CameraSystem;
import minecrafttransportsimulator.systems.CameraSystem.CameraMode;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class MtsClientCompat implements IMtsClientCompat {
	@Override
	public boolean isMtsCameraContext() {
		return this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldBypassImmersiveCameraSetup() {
		return Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()
			&& this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldBypassPerspectiveEnforcement() {
		return Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()
			&& this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldLetVanillaHandleCameraType(CameraType cameraType) {
		return Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()
			&& this.getClientControllerSeat() != null
			&& (cameraType.isFirstPerson() || this.isNativeCameraActive());
	}

	@Override
	public boolean shouldBypassCrosshairCancellation() {
		return Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()
			&& this.getClientControllerSeat() != null;
	}

	@Override
	public @Nullable Vec3 adjustCameraAfterMts(Camera camera, float partialTick) {
		if (!Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()) {
			this.debugDecision("ignored: compatibility disabled", false, false, false, camera.getPosition(), null, partialTick);
			return null;
		}
		PartSeat seat = this.getClientControllerSeat();
		if (seat == null) {
			this.debugDecision("ignored: player is not controlling an MTS vehicle", false, false, false, camera.getPosition(), null, partialTick);
			return null;
		}
		boolean thirdPerson = this.isThirdPersonCamera();
		boolean nativeCameraActive = this.isNativeCameraActive();
		if (!thirdPerson) {
			this.debugDecision("ignored: MTS camera mode is not third person", true, nativeCameraActive, false, camera.getPosition(), null, partialTick);
			return null;
		}
		if (nativeCameraActive) {
			this.debugDecision("ignored: MTS native/custom camera is active", true, true, true, camera.getPosition(), null, partialTick);
			return null;
		}
		double heightOffset = Config.CLIENT.getIntegrationsConfig().getMtsThirdPersonHeightOffset();
		double distanceOffset = Config.CLIENT.getIntegrationsConfig().getMtsThirdPersonDistanceOffset();
		Vec3 before = camera.getPosition();
		if (heightOffset == 0.0D && distanceOffset == 0.0D) {
			this.debugDecision("ignored: offsets are zero; camera remains native", true, false, true, before, before, partialTick);
			return null;
		}
		Vector3f look = camera.getLookVector();
		Vec3 after = before.add(
			-look.x() * distanceOffset,
			heightOffset - look.y() * distanceOffset,
			-look.z() * distanceOffset
		);
		this.debugDecision("applied: Immersive Cam MTS offset", true, false, true, before, after, partialTick);
		return after;
	}

	@Nullable
	private PartSeat getClientControllerSeat() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || minecraft.player == null) {
			return null;
		}
		IWrapperPlayer player = InterfaceManager.clientInterface.getClientPlayer();
		if (player == null) {
			return null;
		}
		AEntityB_Existing ridingEntity = player.getEntityRiding();
		if (ridingEntity instanceof PartSeat seat && seat.vehicleOn != null && seat.placementDefinition.isController) {
			return seat;
		}
		return null;
	}

	private boolean isThirdPersonCamera() {
		CameraMode cameraMode = InterfaceManager.clientInterface.getCameraMode();
		return cameraMode.thirdPerson && !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	private boolean isNativeCameraActive() {
		return CameraSystem.activeCamera != null || CameraSystem.customCameraOverlay != null;
	}

	private void debugDecision(
		String message,
		boolean seatedInMtsVehicle,
		boolean nativeCameraActive,
		boolean thirdPerson,
		Vec3 before,
		@Nullable Vec3 after,
		float partialTick
	) {
		if (Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityDebugEnabled()) {
			ImersiveCamCommon.LOGGER.debug(
				"[MTS compat] {}; seated={}, nativeCameraActive={}, thirdPerson={}, before={}, after={}, partialTick={}",
				message,
				seatedInMtsVehicle,
				nativeCameraActive,
				thirdPerson,
				before,
				after,
				partialTick
			);
		}
	}
}
