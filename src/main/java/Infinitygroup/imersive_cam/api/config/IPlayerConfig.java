package Infinitygroup.imersive_cam.api.config;

import Infinitygroup.imersive_cam.api.client.TurningMode;

public interface IPlayerConfig {
	double getHidePlayerWhenLookingUpAngle();
	
	boolean isPlayerTransparencyEnabled();
	
	boolean isPlayerTransparentWhenAiming();
	
	boolean isPlayerTransparentWhenClimbing();
	
	TurningMode getTurningModeWhenUsingItem();
	
	TurningMode getTurningModeWhenAttacking();
	
	TurningMode getTurningModeWhenInteracting();
	
	TurningMode getTurningModeWhenPicking();
	
	int getTurningLockTime();
	
	double getTurningSpeedMultiplier();
	
	boolean isPlayerXRotTurningWithCamera();
	
	boolean isPlayerYRotTurningWithCamera();
	
	double getPlayerYRotTurnAngleLimit();
}
