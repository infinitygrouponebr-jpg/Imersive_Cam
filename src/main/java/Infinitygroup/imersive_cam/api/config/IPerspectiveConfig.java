package Infinitygroup.imersive_cam.api.config;

import Infinitygroup.imersive_cam.api.client.Perspective;

public interface IPerspectiveConfig {
	boolean isThirdPersonReplaced();
	
	boolean isFirstPersonEnabled();
	
	boolean isThirdPersonFrontEnabled();
	
	boolean isThirdPersonBackEnabled();
	
	Perspective getDefaultPerspective();
	
	boolean isPerspectivePersistent();
}
