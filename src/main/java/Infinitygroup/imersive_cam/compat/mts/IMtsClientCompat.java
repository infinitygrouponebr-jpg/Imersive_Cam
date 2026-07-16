package Infinitygroup.imersive_cam.compat.mts;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IMtsClientCompat {
	boolean isMtsCameraContext();

	boolean shouldBypassImmersiveCameraSetup();

	boolean shouldBypassPerspectiveEnforcement();

	boolean shouldLetVanillaHandleCameraType(CameraType cameraType);

	boolean shouldBypassCrosshairCancellation();

	@Nullable Vec3 adjustCameraAfterMts(Camera camera, float partialTick);
}
