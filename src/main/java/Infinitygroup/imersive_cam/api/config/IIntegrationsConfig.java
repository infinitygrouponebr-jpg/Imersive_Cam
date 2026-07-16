package Infinitygroup.imersive_cam.api.config;

import java.util.List;

public interface IIntegrationsConfig {
	List<? extends String> getCuriosAdaptiveCrosshairItems();
	
	List<? extends String> getCuriosAdaptiveCrosshairItemProperties();
	
	boolean isEpicFightDecoupledCameraLockOnEnabled();

	boolean isMtsCompatibilityEnabled();

	double getMtsThirdPersonHeightOffset();

	double getMtsThirdPersonDistanceOffset();

	boolean isMtsCompatibilityDebugEnabled();
}
