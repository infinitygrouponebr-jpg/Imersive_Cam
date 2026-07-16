package Infinitygroup.imersive_cam.compat.tacz;

public interface ITaczClientCompat {
	boolean isAvailable();
	
	boolean isHoldingGun();
	
	boolean shouldRenderGunCrosshair(float partialTick);
	
	float getGunCrosshairAlpha(float partialTick);

	boolean shouldUseShoulderCamera();

	boolean shouldAlignGunfireToCrosshair();
}
