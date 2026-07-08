package Infinitygroup.imersive_cam.api.client;

import Infinitygroup.imersive_cam.api.math.Vec2f;
import net.minecraft.world.phys.Vec3;

public interface IImersiveCamCamera {
	double getCameraDistance();
	
	Vec3 getOffset();
	
	Vec3 getRenderOffset();
	
	Vec3 getTargetOffset();
	
	float getXRot();
	
	void setXRot(float xRot);
	
	float getYRot();
	
	void setYRot(float yRot);
	
	Vec2f getRenderRotation();
}
