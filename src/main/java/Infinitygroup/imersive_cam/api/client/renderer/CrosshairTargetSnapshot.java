package Infinitygroup.imersive_cam.api.client.renderer;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record CrosshairTargetSnapshot(
	Vec3 position,
	ResourceKey<Level> dimension,
	long gameTime
) {
}
