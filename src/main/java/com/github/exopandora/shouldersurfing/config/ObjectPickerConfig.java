package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;
import com.github.exopandora.shouldersurfing.api.config.IObjectPickerConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class ObjectPickerConfig implements IObjectPickerConfig {
	// Camera-focused picking keeps interaction and aiming aligned with the tactical shoulder view.
	private static final double DEFAULT_TACTICAL_RAYTRACE_DISTANCE = 64.0D;

	private final DoubleValue customRaytraceDistance;
	private final BooleanValue isCustomRaytraceDistanceEnabled;
	private final ConfigValue<PickOrigin> entityPickOrigin;
	private final ConfigValue<PickOrigin> blockPickOrigin;
	private final ConfigValue<PickVector> pickVector;
	
	protected ObjectPickerConfig(ModConfigSpec.Builder builder) {
		builder.push("object_picker");
		
		this.customRaytraceDistance = builder
			.comment("The raytrace distance used for the dynamic crosshair. Lower than the upstream default so the focus stays steadier and more tactical in third person.")
			.translation(MOD_ID + ".configuration.object_picker.custom_raytrace_distance")
			.defineInRange("custom_raytrace_distance", DEFAULT_TACTICAL_RAYTRACE_DISTANCE, 0, Double.MAX_VALUE);
		
		this.isCustomRaytraceDistanceEnabled = builder
			.comment("Whether to use the custom raytrace distance used for the dynamic crosshair.")
			.translation(MOD_ID + ".configuration.object_picker.use_custom_raytrace_distance")
			.define("use_custom_raytrace_distance", true);
		
		builder.push("pick_origin");
		
		this.entityPickOrigin = builder
			.comment("The origin where the entity pick starts when using the static crosshair. CAMERA better matches an over-the-shoulder tactical framing.")
			.translation(MOD_ID + ".configuration.object_picker.pick_origin.entity_pick_origin")
			.defineEnum("entity_pick_origin", PickOrigin.CAMERA, PickOrigin.values());
		
		this.blockPickOrigin = builder
			.comment("The origin where the block pick starts when using the static crosshair. CAMERA keeps block focus aligned with the camera rather than the center of the player model.")
			.translation(MOD_ID + ".configuration.object_picker.pick_origin.block_pick_origin")
			.defineEnum("block_pick_origin", PickOrigin.CAMERA, PickOrigin.values());
		
		builder.pop();
		builder.push("pick_vector");
		
		this.pickVector = builder
			.comment("The vector direction of the raytrace when picking objects. CAMERA is kept as the default so the screen center tracks what the shoulder camera is actually looking at.")
			.translation(MOD_ID + ".configuration.object_picker.pick_vector.pick_vector")
			.defineEnum("pick_vector", PickVector.CAMERA, PickVector.values());
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public double getCustomRaytraceDistance() {
		return this.customRaytraceDistance.get();
	}
	
	@Override
	public boolean isCustomRaytraceDistanceEnabled() {
		return this.isCustomRaytraceDistanceEnabled.get();
	}
	
	@Override
	public PickOrigin getEntityPickOrigin() {
		return this.entityPickOrigin.get();
	}
	
	@Override
	public PickOrigin getBlockPickOrigin() {
		return this.blockPickOrigin.get();
	}
	
	@Override
	public PickVector getPickVector() {
		return this.pickVector.get();
	}
}
