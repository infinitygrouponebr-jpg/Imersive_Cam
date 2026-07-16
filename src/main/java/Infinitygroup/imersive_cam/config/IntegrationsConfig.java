package Infinitygroup.imersive_cam.config;

import Infinitygroup.imersive_cam.api.config.IIntegrationsConfig;
import Infinitygroup.imersive_cam.config.Config.ClientConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

import java.util.ArrayList;
import java.util.List;

import static Infinitygroup.imersive_cam.ImersiveCamCommon.MOD_ID;

public class IntegrationsConfig implements IIntegrationsConfig {
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItems;
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItemProperties;
	private final BooleanValue isEpicFightDecoupledCameraLockOnEnabled;
	private final BooleanValue mtsCompatibilityEnabled;
	private final DoubleValue mtsThirdPersonHeightOffset;
	private final DoubleValue mtsThirdPersonDistanceOffset;
	private final BooleanValue mtsCompatibilityDebugEnabled;
	
	protected IntegrationsConfig(ModConfigSpec.Builder builder) {
		builder.push("integrations");
		builder.push("curios");
		
		this.curiosAdaptiveCrosshairItems = builder
			.comment("Items that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. The curios slot must be specified before the expression and is separated by an '@' character. Example: 'ring@angelring:.*_ring' matches 'angelring:diamond_ring' and 'angelring:angel_ring' when equipped in the 'ring' slot.")
			.translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_items")
			.defineList("adaptive_crosshair_items", ArrayList::new, String::new, ClientConfig::isValidItemWithSlot);
		
		this.curiosAdaptiveCrosshairItemProperties = builder
		   .comment("Item properties of an item, that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. Example: 'necklace@charged'")
		   .translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_item_properties")
		   .defineList("adaptive_crosshair_item_properties", ArrayList::new, String::new, ClientConfig::isValidItemPropertyWithSlot);
		
		builder.pop();
		builder.push("epicfight");
		
		this.isEpicFightDecoupledCameraLockOnEnabled = builder
			.comment("Whether to allow target lock-on when camera is decoupled.")
			.translation(MOD_ID + ".configuration.integrations.epicfight.decoupled_camera_lock_on")
			.define("decoupled_camera_lock_on", false);
		
		builder.pop();

		builder.push("mts");

		this.mtsCompatibilityEnabled = builder
			.comment("Whether to enable compatibility with Immersive Vehicles / Minecraft Transport Simulator.")
			.translation(MOD_ID + ".configuration.integrations.mts.mtsCompatibilityEnabled")
			.define("mtsCompatibilityEnabled", true);

		this.mtsThirdPersonHeightOffset = builder
			.comment("Additional global Y offset applied after Immersive Vehicles computes its third-person camera.")
			.translation(MOD_ID + ".configuration.integrations.mts.mtsThirdPersonHeightOffset")
			.defineInRange("mtsThirdPersonHeightOffset", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);

		this.mtsThirdPersonDistanceOffset = builder
			.comment("Additional backward offset applied along the final Immersive Vehicles camera rotation.")
			.translation(MOD_ID + ".configuration.integrations.mts.mtsThirdPersonDistanceOffset")
			.defineInRange("mtsThirdPersonDistanceOffset", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);

		this.mtsCompatibilityDebugEnabled = builder
			.comment("Whether to log Immersive Vehicles camera compatibility decisions.")
			.translation(MOD_ID + ".configuration.integrations.mts.debug")
			.define("debug", false);

		builder.pop();
		builder.pop();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItems() {
		return this.curiosAdaptiveCrosshairItems.get();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItemProperties() {
		return this.curiosAdaptiveCrosshairItemProperties.get();
	}
	
	@Override
	public boolean isEpicFightDecoupledCameraLockOnEnabled() {
		return this.isEpicFightDecoupledCameraLockOnEnabled.get();
	}

	@Override
	public boolean isMtsCompatibilityEnabled() {
		return this.mtsCompatibilityEnabled.get();
	}

	@Override
	public double getMtsThirdPersonHeightOffset() {
		return this.mtsThirdPersonHeightOffset.get();
	}

	@Override
	public double getMtsThirdPersonDistanceOffset() {
		return this.mtsThirdPersonDistanceOffset.get();
	}

	@Override
	public boolean isMtsCompatibilityDebugEnabled() {
		return this.mtsCompatibilityDebugEnabled.get();
	}

	public void applyApprovedProfile() {
		Config.CLIENT.set(this.curiosAdaptiveCrosshairItems, this.curiosAdaptiveCrosshairItems.getDefault());
		Config.CLIENT.set(this.curiosAdaptiveCrosshairItemProperties, this.curiosAdaptiveCrosshairItemProperties.getDefault());
		Config.CLIENT.set(this.isEpicFightDecoupledCameraLockOnEnabled, this.isEpicFightDecoupledCameraLockOnEnabled.getDefault());
		Config.CLIENT.set(this.mtsCompatibilityEnabled, this.mtsCompatibilityEnabled.getDefault());
		Config.CLIENT.set(this.mtsThirdPersonHeightOffset, this.mtsThirdPersonHeightOffset.getDefault());
		Config.CLIENT.set(this.mtsThirdPersonDistanceOffset, this.mtsThirdPersonDistanceOffset.getDefault());
		Config.CLIENT.set(this.mtsCompatibilityDebugEnabled, this.mtsCompatibilityDebugEnabled.getDefault());
	}
}
