package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TaczAimDirectionPayload(
	float pitch,
	float yaw,
	long sequence,
	int selectedSlot
) implements CustomPacketPayload {
	public static final Type<TaczAimDirectionPayload> TYPE = new Type<>(
		ResourceLocation.fromNamespaceAndPath(ImersiveCamCommon.MOD_ID, "tacz_aim_direction")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, TaczAimDirectionPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT,
		TaczAimDirectionPayload::pitch,
		ByteBufCodecs.FLOAT,
		TaczAimDirectionPayload::yaw,
		ByteBufCodecs.VAR_LONG,
		TaczAimDirectionPayload::sequence,
		ByteBufCodecs.VAR_INT,
		TaczAimDirectionPayload::selectedSlot,
		TaczAimDirectionPayload::new
	);

	public static void register(RegisterPayloadHandlersEvent event) {
		event.registrar(ImersiveCamCommon.MOD_ID)
			.versioned("1")
			.optional()
			.playToServer(TYPE, STREAM_CODEC, TaczAimDirectionPayload::handle);
	}

	private static void handle(TaczAimDirectionPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				TaczServerAimState.INSTANCE.accept(player, payload);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
