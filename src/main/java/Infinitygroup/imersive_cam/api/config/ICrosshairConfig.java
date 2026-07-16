package Infinitygroup.imersive_cam.api.config;

import Infinitygroup.imersive_cam.api.client.CrosshairType;
import Infinitygroup.imersive_cam.api.client.CrosshairVisibility;
import Infinitygroup.imersive_cam.api.client.Perspective;

import java.util.List;

public interface ICrosshairConfig {
	CrosshairVisibility getCrosshairVisibility(Perspective perspective);

	CrosshairType getCrosshairType();

	List<? extends String> getAdaptiveCrosshairHoldItems();

	List<? extends String> getAdaptiveCrosshairUseItems();

	List<? extends String> getAdaptiveCrosshairHoldItemProperties();

	List<? extends String> getAdaptiveCrosshairUseItemProperties();

	boolean isObstructionIndicatorEnabled();

	boolean isObstructionIndicatorOnlyShownWhenAiming();

	int getObstructionIndicatorMinDistanceToCrosshair();

	double getObstructionIndicatorMaxDistanceToObstruction();

	boolean isTaczCrosshairEnabled();

	boolean hideTaczCrosshairDuringAds();

	double getTaczCrosshairAdsHideThreshold();
}
