package Infinitygroup.imersive_cam.api.config;

import java.util.List;

public interface IIntegrationsConfig {
	List<? extends String> getCuriosAdaptiveCrosshairItems();
	
	List<? extends String> getCuriosAdaptiveCrosshairItemProperties();
	
	boolean isEpicFightDecoupledCameraLockOnEnabled();

	boolean isMtsCompatibilityEnabled();

	boolean isMtsCameraCompatibilityEnabled();

	double getMtsThirdPersonHeightOffset();

	double getMtsThirdPersonDistanceOffset();

	boolean isMtsCameraSmoothingEnabled();

	double getMtsPositionSmoothing();

	double getMtsRotationSmoothing();

	double getMtsMaximumYawSpeed();

	double getMtsTurnDeadzoneDegrees();

	double getMtsVehicleRotationFollow();

	boolean isMtsUseFreeThirdPerson();

	boolean isMtsCompatibilityDebugEnabled();
}
