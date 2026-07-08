package Infinitygroup.imersive_cam.client;

import Infinitygroup.imersive_cam.api.client.event.ComputeCameraCouplingEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputeCameraEntityTransparencyEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerAimStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerAttackStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerInteractionStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerPickStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerRideBoatStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputePlayerUseItemStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputeTargetCameraOffsetEvent;
import Infinitygroup.imersive_cam.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import Infinitygroup.imersive_cam.api.client.event.ForceVanillaPlayerInputEvent;
import Infinitygroup.imersive_cam.api.client.event.SetupCameraRotationEvent;
import Infinitygroup.imersive_cam.api.client.event.TickEvent;
import Infinitygroup.imersive_cam.api.math.Vec2f;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class EventHooks {
	public static boolean isUsingItem(LivingEntity cameraEntity) {
		ComputePlayerUseItemStateEvent event = new ComputePlayerUseItemStateEvent(cameraEntity);
		event.setResult(false);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isInteracting(LivingEntity cameraEntity) {
		ComputePlayerInteractionStateEvent event = new ComputePlayerInteractionStateEvent(cameraEntity);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isAttacking(LivingEntity cameraEntity) {
		ComputePlayerAttackStateEvent event = new ComputePlayerAttackStateEvent(cameraEntity);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isPicking(LivingEntity cameraEntity) {
		ComputePlayerPickStateEvent event = new ComputePlayerPickStateEvent(cameraEntity);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isRidingBoat(LivingEntity cameraEntity, Entity vehicle) {
		ComputePlayerRideBoatStateEvent event = new ComputePlayerRideBoatStateEvent(cameraEntity, vehicle);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isAiming(LivingEntity entity) {
		ComputePlayerAimStateEvent event = new ComputePlayerAimStateEvent(entity);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isForcingCoupledCamera() {
		ComputeCameraCouplingEvent event = new ComputeCameraCouplingEvent();
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static Vec2f setupCameraRotation(LocalPlayer player, Vec2f cameraRot, Vec2f cameraRotO, Vec2f dRot, Vec2f dRotScaled) {
		SetupCameraRotationEvent event = new SetupCameraRotationEvent(player, cameraRot, cameraRotO, dRot, dRotScaled);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isForcingVanillaPlayerInput(Entity cameraEntity) {
		ForceVanillaPlayerInputEvent event = new ForceVanillaPlayerInputEvent(cameraEntity);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static float getCameraEntityAlpha(Entity entity, float partialTick) {
		ComputeCameraEntityTransparencyEvent event = new ComputeCameraEntityTransparencyEvent(entity, partialTick);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static Vec3 getTargetOffset(Vec3 defaultOffset, Camera camera, Entity cameraEntity, BlockGetter level) {
		ComputeTargetCameraOffsetEvent event = new ComputeTargetCameraOffsetEvent(defaultOffset, camera, cameraEntity, level);
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static boolean isTemporaryFirstPerson() {
		ComputeTemporaryFirstPersonStateEvent event = new ComputeTemporaryFirstPersonStateEvent();
		return ImersiveCam.getInstance().getEventBus().fire(event).getResult();
	}
	
	public static void tick() {
		TickEvent event = new TickEvent();
		ImersiveCam.getInstance().getEventBus().fire(event);
	}
}
