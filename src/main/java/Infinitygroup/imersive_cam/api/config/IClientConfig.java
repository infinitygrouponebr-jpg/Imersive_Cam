package Infinitygroup.imersive_cam.api.config;

public interface IClientConfig {
	ICameraConfig getCameraConfig();
	
	IPerspectiveConfig getPerspectiveConfig();
	
	IPlayerConfig getPlayerConfig();
	
	IObjectPickerConfig getObjectPickerConfig();
	
	ICrosshairConfig getCrosshairConfig();
	
	IAudioConfig getAudioConfig();
	
	IIntegrationsConfig getIntegrationsConfig();
}
