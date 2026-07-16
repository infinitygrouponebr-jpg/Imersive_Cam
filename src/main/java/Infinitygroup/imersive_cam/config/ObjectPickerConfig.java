package Infinitygroup.imersive_cam.config;

import Infinitygroup.imersive_cam.api.client.world.phys.PickOrigin;
import Infinitygroup.imersive_cam.api.client.world.phys.PickVector;
import Infinitygroup.imersive_cam.api.config.IObjectPickerConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

import static Infinitygroup.imersive_cam.ImersiveCamCommon.MOD_ID;

public class ObjectPickerConfig implements IObjectPickerConfig {
	// Camera-focused picking keeps interaction and aiming aligned with the tactical side-offset view.
	private static final double DEFAULT_TACTICAL_RAYTRACE_DISTANCE = 400.0D;
	private static final PickOrigin DEFAULT_ENTITY_PICK_ORIGIN = PickOrigin.PLAYER;
	private static final PickOrigin DEFAULT_BLOCK_PICK_ORIGIN = PickOrigin.PLAYER;
	private static final PickVector DEFAULT_PICK_VECTOR = PickVector.CAMERA;

	private final DoubleValue customRaytraceDistance;
	private final BooleanValue isCustomRaytraceDistanceEnabled;
	private final ConfigValue<PickOrigin> entityPickOrigin;
	private final ConfigValue<PickOrigin> blockPickOrigin;
	private final ConfigValue<PickVector> pickVector;
	
	protected ObjectPickerConfig(ModConfigSpec.Builder builder) {
		builder.push("object_picker");
		
		this.customRaytraceDistance = builder
			.comment("The raytrace distance used for the dynamic crosshair.")
			.translation(MOD_ID + ".configuration.object_picker.custom_raytrace_distance")
			.defineInRange("custom_raytrace_distance", DEFAULT_TACTICAL_RAYTRACE_DISTANCE, 0, Double.MAX_VALUE);
		
		this.isCustomRaytraceDistanceEnabled = builder
			.comment("Whether to use the custom raytrace distance used for the dynamic crosshair.")
			.translation(MOD_ID + ".configuration.object_picker.use_custom_raytrace_distance")
			.define("use_custom_raytrace_distance", true);
		
		builder.push("pick_origin");
		
		this.entityPickOrigin = builder
			.comment("The origin where the entity pick starts when using the static crosshair.")
			.translation(MOD_ID + ".configuration.object_picker.pick_origin.entity_pick_origin")
			.defineEnum("entity_pick_origin", DEFAULT_ENTITY_PICK_ORIGIN, PickOrigin.values());
		
		this.blockPickOrigin = builder
			.comment("The origin where the block pick starts when using the static crosshair.")
			.translation(MOD_ID + ".configuration.object_picker.pick_origin.block_pick_origin")
			.defineEnum("block_pick_origin", DEFAULT_BLOCK_PICK_ORIGIN, PickOrigin.values());
		
		builder.pop();
		builder.push("pick_vector");
		
		this.pickVector = builder
			.comment("The vector direction of the raytrace when picking objects. CAMERA is kept as the default so the screen center tracks what the tactical camera is actually looking at.")
			.translation(MOD_ID + ".configuration.object_picker.pick_vector.pick_vector")
			.defineEnum("pick_vector", DEFAULT_PICK_VECTOR, PickVector.values());
		
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

	public void applyApprovedProfile() {
		Config.CLIENT.set(this.customRaytraceDistance, this.customRaytraceDistance.getDefault());
		Config.CLIENT.set(this.isCustomRaytraceDistanceEnabled, this.isCustomRaytraceDistanceEnabled.getDefault());
		Config.CLIENT.set(this.entityPickOrigin, this.entityPickOrigin.getDefault());
		Config.CLIENT.set(this.blockPickOrigin, this.blockPickOrigin.getDefault());
		Config.CLIENT.set(this.pickVector, this.pickVector.getDefault());
	}
}
