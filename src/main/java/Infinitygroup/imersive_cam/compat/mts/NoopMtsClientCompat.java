package Infinitygroup.imersive_cam.compat.mts;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class NoopMtsClientCompat implements IMtsClientCompat {
	@Override
	public boolean isMtsCameraContext() {
		return false;
	}

	@Override
	public boolean shouldBypassImmersiveCameraSetup() {
		return false;
	}

	@Override
	public boolean shouldBypassPerspectiveEnforcement() {
		return false;
	}

	@Override
	public boolean shouldLetVanillaHandleCameraType(CameraType cameraType) {
		return false;
	}

	@Override
	public boolean shouldBypassCrosshairCancellation() {
		return false;
	}

	@Override
	public @Nullable Vec3 adjustCameraAfterMts(Camera camera, float partialTick) {
		return null;
	}
}
