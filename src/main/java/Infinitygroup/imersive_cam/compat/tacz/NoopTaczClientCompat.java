package Infinitygroup.imersive_cam.compat.tacz;

public final class NoopTaczClientCompat implements ITaczClientCompat {
	@Override
	public boolean isAvailable() {
		return false;
	}
	
	@Override
	public boolean isHoldingGun() {
		return false;
	}
	
	@Override
	public boolean shouldRenderGunCrosshair(float partialTick) {
		return false;
	}
	
	@Override
	public float getGunCrosshairAlpha(float partialTick) {
		return 0.0F;
	}
}
