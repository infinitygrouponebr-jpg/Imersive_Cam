package Infinitygroup.imersive_cam.compat.mts;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public final class MtsCompatBootstrap {
	private static final String MTS_MOD_ID = "mts";
	private static IMtsClientCompat clientCompat = new NoopMtsClientCompat();
	private static boolean checked;

	private MtsCompatBootstrap() {
	}

	private static IMtsClientCompat getClientCompat() {
		if (!checked) {
			checked = true;
			if (ModList.get().isLoaded(MTS_MOD_ID)) {
				clientCompat = new MtsClientCompat();
				ImersiveCamCommon.LOGGER.info("Immersive Vehicles detected; camera compatibility enabled");
			} else {
				ImersiveCamCommon.LOGGER.debug("Immersive Vehicles not detected; optional MTS compatibility disabled");
			}
		}
		return clientCompat;
	}

	public static boolean isMtsCameraContext() {
		return getClientCompat().isMtsCameraContext();
	}

	public static boolean shouldBypassImmersiveCameraSetup() {
		return getClientCompat().shouldBypassImmersiveCameraSetup();
	}

	public static boolean shouldBypassPerspectiveEnforcement() {
		return getClientCompat().shouldBypassPerspectiveEnforcement();
	}

	public static boolean shouldLetVanillaHandleCameraType(CameraType cameraType) {
		return getClientCompat().shouldLetVanillaHandleCameraType(cameraType);
	}

	public static @Nullable Vec3 adjustCameraAfterMts(Camera camera, float partialTick) {
		return getClientCompat().adjustCameraAfterMts(camera, partialTick);
	}
}
