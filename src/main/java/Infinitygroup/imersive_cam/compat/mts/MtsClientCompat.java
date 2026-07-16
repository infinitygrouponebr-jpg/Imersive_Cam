package Infinitygroup.imersive_cam.compat.mts;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.config.Config;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;
import minecrafttransportsimulator.entities.instances.APart;
import minecrafttransportsimulator.entities.instances.PartSeat;
import minecrafttransportsimulator.mcinterface.IWrapperPlayer;
import minecrafttransportsimulator.mcinterface.InterfaceManager;
import minecrafttransportsimulator.systems.CameraSystem;
import minecrafttransportsimulator.systems.CameraSystem.CameraMode;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class MtsClientCompat implements IMtsClientCompat {
	private static final double MAX_DELTA_SECONDS = 0.05D;
	private static final double PAUSE_RESET_SECONDS = 0.25D;
	private static final double TELEPORT_DISTANCE_SQR = 256.0D;
	private static final double POSITION_EPSILON_SQR = 1.0E-8D;
	private final CameraSmoothingState smoothingState = new CameraSmoothingState();

	@Override
	public boolean isMtsCameraContext() {
		return this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldBypassImmersiveCameraSetup() {
		return this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldBypassPerspectiveEnforcement() {
		return this.getClientControllerSeat() != null;
	}

	@Override
	public boolean shouldLetVanillaHandleCameraType(CameraType cameraType) {
		return this.getClientControllerSeat() != null;
	}

	@Override
	public @Nullable Vec3 adjustCameraAfterMts(Camera camera, float partialTick) {
		Minecraft minecraft = Minecraft.getInstance();
		PartSeat seat = this.getClientControllerSeat();
		if (seat == null) {
			this.smoothingState.reset();
			this.debugDecision("ignored: player is not controlling an MTS vehicle", null, false, false, false, camera.getPosition(), null, partialTick);
			return null;
		}
		boolean nativeCameraActive = this.isNativeCameraActive();
		boolean thirdPerson = this.isThirdPersonCamera();
		HudDiagnostics hudDiagnostics = this.collectHudDiagnostics(seat, thirdPerson);
		if (!Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityEnabled()
			|| !Config.CLIENT.getIntegrationsConfig().isMtsCameraCompatibilityEnabled()) {
			this.smoothingState.reset();
			this.debugDecision("ignored: MTS camera compatibility disabled", hudDiagnostics, true, nativeCameraActive, thirdPerson, camera.getPosition(), null, partialTick);
			return null;
		}
		if (!thirdPerson) {
			this.smoothingState.reset();
			this.debugDecision("ignored: MTS camera mode is not third person", hudDiagnostics, true, nativeCameraActive, false, camera.getPosition(), null, partialTick);
			return null;
		}
		if (nativeCameraActive) {
			this.smoothingState.reset();
			this.debugDecision("ignored: MTS native/custom camera is active", hudDiagnostics, true, true, true, camera.getPosition(), null, partialTick);
			return null;
		}
		if (minecraft.level == null) {
			this.smoothingState.reset();
			this.debugDecision("ignored: world is null", hudDiagnostics, true, false, true, camera.getPosition(), null, partialTick);
			return null;
		}
		double heightOffset = Config.CLIENT.getIntegrationsConfig().getMtsThirdPersonHeightOffset();
		double distanceOffset = Config.CLIENT.getIntegrationsConfig().getMtsThirdPersonDistanceOffset();
		Vec3 nativePosition = camera.getPosition();
		Vector3f look = camera.getLookVector();
		Vec3 target = nativePosition.add(
			-look.x() * distanceOffset,
			heightOffset - look.y() * distanceOffset,
			-look.z() * distanceOffset
		);
		int vehicleIdentity = System.identityHashCode(seat.vehicleOn);
		long frameTimeNanos = minecraft.getFrameTimeNs();
		CameraType cameraType = minecraft.options.getCameraType();
		if (!Config.CLIENT.getIntegrationsConfig().isMtsCameraSmoothingEnabled()) {
			this.smoothingState.reset();
			this.debugDecision("applied: smoothing disabled; using final MTS target plus configured offsets", hudDiagnostics, true, false, true, nativePosition, target, partialTick);
			return target.distanceToSqr(nativePosition) > POSITION_EPSILON_SQR ? target : null;
		}
		Vec3 smoothed = this.smoothingState.update(
			target,
			nativePosition,
			camera.getYRot(),
			camera.getXRot(),
			camera.getEntity() != null ? camera.getEntity().getEyePosition(partialTick) : null,
			frameTimeNanos,
			vehicleIdentity,
			minecraft.level.dimension(),
			cameraType
		);
		this.debugDecision(
			Config.CLIENT.getIntegrationsConfig().isMtsUseFreeThirdPerson()
				? "applied: position smoothing only; preserving MTS/free third-person rotation"
				: "applied: position smoothing only; preserving native MTS rotation",
			hudDiagnostics,
			true,
			false,
			true,
			nativePosition,
			smoothed,
			partialTick
		);
		return smoothed.distanceToSqr(nativePosition) > POSITION_EPSILON_SQR ? smoothed : null;
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

	private HudDiagnostics collectHudDiagnostics(PartSeat seat, boolean thirdPerson) {
		boolean firstPerson = !thirdPerson;
		boolean renderHud1P = minecrafttransportsimulator.systems.ConfigSystem.client.renderingSettings.renderHUD_1P.value;
		boolean renderHud3P = minecrafttransportsimulator.systems.ConfigSystem.client.renderingSettings.renderHUD_3P.value;
		boolean customOverlayNull = CameraSystem.customCameraOverlay == null;
		int vehicleInstrumentCount = countInstalledInstruments(seat.vehicleOn.instruments);
		int partInstrumentCount = 0;
		for (APart part : seat.vehicleOn.parts) {
			partInstrumentCount += countInstalledInstruments(part.instruments);
		}
		boolean hudVisible = customOverlayNull
			&& seat.placementDefinition.isController
			&& (firstPerson ? renderHud1P : renderHud3P);
		int visibleInstrumentCount = hudVisible ? vehicleInstrumentCount + partInstrumentCount : 0;
		return new HudDiagnostics(
			true,
			seat.placementDefinition.isController,
			firstPerson,
			renderHud1P,
			renderHud3P,
			customOverlayNull,
			vehicleInstrumentCount,
			partInstrumentCount,
			visibleInstrumentCount,
			seat.vehicleOn.definition.packID + ":" + seat.vehicleOn.definition.systemName
		);
	}

	private static int countInstalledInstruments(java.util.List<?> instruments) {
		int count = 0;
		for (Object instrument : instruments) {
			if (instrument != null) {
				count++;
			}
		}
		return count;
	}

	private void debugDecision(
		String message,
		@Nullable HudDiagnostics hudDiagnostics,
		boolean seatedInMtsVehicle,
		boolean nativeCameraActive,
		boolean thirdPerson,
		Vec3 before,
		@Nullable Vec3 after,
		float partialTick
	) {
		if (Config.CLIENT.getIntegrationsConfig().isMtsCompatibilityDebugEnabled()) {
			ImersiveCamCommon.LOGGER.debug(
				"[MTS compat] {}; seated={}, nativeCameraActive={}, thirdPerson={}, before={}, after={}, partialTick={}, hud={}",
				message,
				seatedInMtsVehicle,
				nativeCameraActive,
				thirdPerson,
				before,
				after,
				partialTick,
				hudDiagnostics
			);
		}
	}

	private static final class CameraSmoothingState {
		private boolean initialized;
		private Vec3 smoothedPosition;
		private float smoothedYaw;
		private float smoothedPitch;
		private long lastFrameTimeNanos;
		private long lastAppliedFrameTimeNanos;
		private int vehicleIdentity;
		private ResourceKey<Level> dimension;
		private CameraType cameraType;
		private Vec3 lastOutputPosition;

		private Vec3 update(
			Vec3 targetPosition,
			Vec3 nativePosition,
			float targetYaw,
			float targetPitch,
			@Nullable Vec3 collisionAnchor,
			long frameTimeNanos,
			int vehicleIdentity,
			ResourceKey<Level> dimension,
			CameraType cameraType
		) {
			if (this.initialized && this.lastAppliedFrameTimeNanos == frameTimeNanos && this.lastOutputPosition != null) {
				return this.lastOutputPosition;
			}
			if (!this.initialized
				|| this.vehicleIdentity != vehicleIdentity
				|| this.dimension == null
				|| !this.dimension.equals(dimension)
				|| this.cameraType != cameraType) {
				this.initialize(targetPosition, targetYaw, targetPitch, frameTimeNanos, vehicleIdentity, dimension, cameraType);
				return this.rememberFrame(frameTimeNanos, targetPosition);
			}
			double rawDeltaSeconds = (frameTimeNanos - this.lastFrameTimeNanos) / 1.0E9D;
			if (rawDeltaSeconds < 0.0D || rawDeltaSeconds > PAUSE_RESET_SECONDS || this.smoothedPosition.distanceToSqr(targetPosition) > TELEPORT_DISTANCE_SQR) {
				this.initialize(targetPosition, targetYaw, targetPitch, frameTimeNanos, vehicleIdentity, dimension, cameraType);
				return this.rememberFrame(frameTimeNanos, targetPosition);
			}
			double deltaSeconds = Mth.clamp(rawDeltaSeconds, 0.0D, MAX_DELTA_SECONDS);
			double positionAlpha = exponentialAlpha(Config.CLIENT.getIntegrationsConfig().getMtsPositionSmoothing(), deltaSeconds);
			double rotationAlpha = exponentialAlpha(Config.CLIENT.getIntegrationsConfig().getMtsRotationSmoothing(), deltaSeconds);
			this.updateAngles(targetYaw, targetPitch, rotationAlpha, deltaSeconds);
			boolean obstacleMovedCloser = collisionAnchor != null
				&& targetPosition.distanceToSqr(collisionAnchor) < this.smoothedPosition.distanceToSqr(collisionAnchor);
			if (obstacleMovedCloser) {
				this.smoothedPosition = targetPosition;
			} else {
				this.smoothedPosition = new Vec3(
					Mth.lerp(positionAlpha, this.smoothedPosition.x(), targetPosition.x()),
					Mth.lerp(positionAlpha, this.smoothedPosition.y(), targetPosition.y()),
					Mth.lerp(positionAlpha, this.smoothedPosition.z(), targetPosition.z())
				);
			}
			this.lastFrameTimeNanos = frameTimeNanos;
			if (this.smoothedPosition.distanceToSqr(nativePosition) <= POSITION_EPSILON_SQR && targetPosition.distanceToSqr(nativePosition) <= POSITION_EPSILON_SQR) {
				this.smoothedPosition = nativePosition;
			}
			return this.rememberFrame(frameTimeNanos, this.smoothedPosition);
		}

		private void initialize(
			Vec3 position,
			float yaw,
			float pitch,
			long frameTimeNanos,
			int vehicleIdentity,
			ResourceKey<Level> dimension,
			CameraType cameraType
		) {
			this.initialized = true;
			this.smoothedPosition = position;
			this.smoothedYaw = yaw;
			this.smoothedPitch = pitch;
			this.lastFrameTimeNanos = frameTimeNanos;
			this.vehicleIdentity = vehicleIdentity;
			this.dimension = dimension;
			this.cameraType = cameraType;
		}

		private Vec3 rememberFrame(long frameTimeNanos, Vec3 outputPosition) {
			this.lastAppliedFrameTimeNanos = frameTimeNanos;
			this.lastOutputPosition = outputPosition;
			return outputPosition;
		}

		private void updateAngles(float targetYaw, float targetPitch, double alpha, double deltaSeconds) {
			double deltaYaw = Mth.wrapDegrees(targetYaw - this.smoothedYaw);
			double deadzone = Config.CLIENT.getIntegrationsConfig().getMtsTurnDeadzoneDegrees();
			if (Math.abs(deltaYaw) > deadzone) {
				double follow = Config.CLIENT.getIntegrationsConfig().getMtsVehicleRotationFollow();
				double maxStep = Config.CLIENT.getIntegrationsConfig().getMtsMaximumYawSpeed() * deltaSeconds;
				double followedDelta = Mth.clamp(deltaYaw * follow, -maxStep, maxStep);
				this.smoothedYaw = Mth.wrapDegrees((float) (this.smoothedYaw + followedDelta * alpha));
			}
			this.smoothedPitch = (float) Mth.lerp(alpha, this.smoothedPitch, targetPitch);
		}

		private void reset() {
			this.initialized = false;
			this.smoothedPosition = null;
			this.lastOutputPosition = null;
			this.lastFrameTimeNanos = 0L;
			this.lastAppliedFrameTimeNanos = 0L;
			this.dimension = null;
			this.cameraType = null;
		}

		private static double exponentialAlpha(double lambda, double deltaSeconds) {
			return 1.0D - Math.exp(-lambda * deltaSeconds);
		}
	}

	private record HudDiagnostics(
		boolean inPartSeat,
		boolean controller,
		boolean firstPerson,
		boolean renderHud1P,
		boolean renderHud3P,
		boolean customCameraOverlayNull,
		int vehicleInstruments,
		int partInstruments,
		int visibleInstruments,
		String vehicleId
	) {
	}
}
