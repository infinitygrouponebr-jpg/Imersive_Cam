package Infinitygroup.imersive_cam.api.config;

import Infinitygroup.imersive_cam.api.client.world.phys.PickOrigin;
import Infinitygroup.imersive_cam.api.client.world.phys.PickVector;

public interface IObjectPickerConfig {
	double getCustomRaytraceDistance();
	
	boolean isCustomRaytraceDistanceEnabled();
	
	PickOrigin getEntityPickOrigin();
	
	PickOrigin getBlockPickOrigin();
	
	PickVector getPickVector();
}
