package Infinitygroup.imersive_cam.api.client;

import Infinitygroup.imersive_cam.api.client.renderer.ICameraEntityRenderer;
import Infinitygroup.imersive_cam.api.client.renderer.ICrosshairRenderer;
import Infinitygroup.imersive_cam.api.client.world.phys.IObjectPicker;
import Infinitygroup.imersive_cam.api.config.IClientConfig;

import java.util.ServiceLoader;

public interface IImersiveCam {
	IImersiveCam INSTANCE = ServiceLoader.load(IImersiveCam.class).findFirst().orElseThrow();
	
	IImersiveCamCamera getCamera();
	
	ICameraEntityRenderer getCameraEntityRenderer();
	
	ICrosshairRenderer getCrosshairRenderer();
	
	IObjectPicker getObjectPicker();
	
	IClientConfig getClientConfig();
	
	boolean isImersiveCam();
	
	boolean isAiming();
	
	boolean isCameraDecoupled();
	
	boolean isFreeLooking();
	
	boolean isTemporaryFirstPerson();
	
	void changePerspective(Perspective perspective);
	
	void togglePerspective();
	
	void toggleCameraCoupling();
	
	void swapCameraSide();
	
	boolean isLookFollowingCrosshairTarget();
	
	void resetState();
	
	static IImersiveCam getInstance() {
		return INSTANCE;
	}
}
