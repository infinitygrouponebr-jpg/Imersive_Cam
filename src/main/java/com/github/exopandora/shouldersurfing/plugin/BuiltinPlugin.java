package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraCouplingEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraEntityTransparencyEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAttackStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerInteractionStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerPickStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerRideBoatStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerUseItemStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeTargetCameraOffsetEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeTemporaryFirstPersonStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.SetupCameraRotationEventHandlerImpl;

public class BuiltinPlugin implements IShoulderSurfingPlugin {
	@Override
	public void register(IEventBus eventBus) {
		eventBus.register(ComputePlayerAimStateEventHandlerImpl.INSTANCE);
		eventBus.register(2000, SetupCameraRotationEventHandlerImpl.INSTANCE);
		eventBus.register(ComputeCameraCouplingEventHandlerImpl.INSTANCE);
		eventBus.register(1000, ComputeCameraEntityTransparencyEventHandlerImpl.INSTANCE);
		eventBus.register(1500, (TickEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.Climbing.INSTANCE);
		eventBus.register(1500, (ComputeCameraEntityTransparencyEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.Climbing.INSTANCE);
		eventBus.register(2000, (TickEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.Aiming.INSTANCE);
		eventBus.register(2000, (ComputeCameraEntityTransparencyEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.Aiming.INSTANCE);
		eventBus.register(0, ComputePlayerAttackStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerAttackStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(0, ComputePlayerInteractionStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerInteractionStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(0, ComputePlayerPickStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerPickStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(ComputePlayerRideBoatStateEventHandlerImpl.INSTANCE);
		eventBus.register(0, ComputePlayerUseItemStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerUseItemStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(-8000, ComputeTargetCameraOffsetEventHandlerImpl.PassengerModifiersAndMultipliers.INSTANCE);
		eventBus.register(-7000, ComputeTargetCameraOffsetEventHandlerImpl.SprintingModifiersAndMultipliers.INSTANCE);
		eventBus.register(-6000, ComputeTargetCameraOffsetEventHandlerImpl.AimingModifiersAndMultipliers.INSTANCE);
		eventBus.register(-5000, ComputeTargetCameraOffsetEventHandlerImpl.FallFlyingModifiersAndMultipliers.INSTANCE);
		eventBus.register(-4000, ComputeTargetCameraOffsetEventHandlerImpl.ClimbingModifiersAndMultipliers.INSTANCE);
		eventBus.register(-3000, ComputeTargetCameraOffsetEventHandlerImpl.CenterWhenLookingDown.INSTANCE);
		eventBus.register(2000, ComputeTargetCameraOffsetEventHandlerImpl.DynamicOffsets.INSTANCE);
		eventBus.register(3000, ComputeTargetCameraOffsetEventHandlerImpl.EntityScale.INSTANCE);
		eventBus.register(4000, ComputeTargetCameraOffsetEventHandlerImpl.OffsetLimits.INSTANCE);
		eventBus.register(ComputeTemporaryFirstPersonStateEventHandlerImpl.INSTANCE);
	}
}
