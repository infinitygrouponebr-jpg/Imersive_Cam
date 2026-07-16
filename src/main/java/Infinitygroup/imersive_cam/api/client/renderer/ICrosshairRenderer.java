package Infinitygroup.imersive_cam.api.client.renderer;

import Infinitygroup.imersive_cam.api.math.Vec2f;
import org.jetbrains.annotations.Nullable;

public interface ICrosshairRenderer {
	boolean isCrosshairVisible();

	boolean isObstructionCrosshairVisible();

	boolean isObstructionIndicatorVisible();

	boolean isCrosshairDynamic();

	@Nullable Vec2f getCrosshairOffset();

	@Nullable CrosshairTargetSnapshot getCrosshairTargetSnapshot();
}
