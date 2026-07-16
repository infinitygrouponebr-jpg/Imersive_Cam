package Infinitygroup.imersive_cam.compat.tacz;

import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum TaczServerAimState {
	INSTANCE;

	private static final long MAX_SERVER_AIM_AGE_TICKS = 10L;
	private final Map<UUID, Sample> samples = new ConcurrentHashMap<UUID, Sample>();

	public void accept(ServerPlayer player, TaczAimDirectionPayload payload) {
		if (!isValidPayload(payload)) {
			return;
		}
		if (!player.isAlive()) {
			this.clear(player);
			return;
		}
		if (payload.selectedSlot() != player.getInventory().selected) {
			this.clear(player);
			return;
		}
		if (!IGun.mainHandHoldGun(player)) {
			this.clear(player);
			return;
		}
		UUID playerId = player.getUUID();
		Sample previous = this.samples.get(playerId);
		if (previous != null && payload.sequence() <= previous.sequence()) {
			return;
		}
		this.samples.put(playerId, new Sample(
			payload.pitch(),
			Mth.wrapDegrees(payload.yaw()),
			payload.sequence(),
			player.serverLevel().getGameTime(),
			payload.selectedSlot(),
			player.serverLevel().dimension()
		));
	}

	public Sample getValidSample(ServerPlayer player) {
		Sample sample = this.samples.get(player.getUUID());
		if (sample == null) {
			return null;
		}
		if (!this.isSampleValid(player, sample)) {
			this.clear(player);
			return null;
		}
		return sample;
	}

	public void clear(ServerPlayer player) {
		this.samples.remove(player.getUUID());
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent.Post event) {
		Iterator<Map.Entry<UUID, Sample>> iterator = this.samples.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Sample> entry = iterator.next();
			ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
			if (player == null || !this.isSampleValid(player, entry.getValue())) {
				iterator.remove();
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			this.clear(player);
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			this.clear(player);
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			this.clear(player);
		}
	}

	private boolean isSampleValid(ServerPlayer player, Sample sample) {
		if (!player.isAlive()) {
			return false;
		}
		if (!sample.dimension().equals(player.serverLevel().dimension())) {
			return false;
		}
		if (sample.selectedSlot() != player.getInventory().selected) {
			return false;
		}
		if (!IGun.mainHandHoldGun(player)) {
			return false;
		}
		long age = player.serverLevel().getGameTime() - sample.receivedServerTick();
		return age >= 0L
			&& age <= MAX_SERVER_AIM_AGE_TICKS
			&& Float.isFinite(sample.pitch())
			&& Float.isFinite(sample.yaw())
			&& sample.pitch() >= -90.0F
			&& sample.pitch() <= 90.0F;
	}

	private static boolean isValidPayload(TaczAimDirectionPayload payload) {
		return Float.isFinite(payload.pitch())
			&& Float.isFinite(payload.yaw())
			&& payload.pitch() >= -90.0F
			&& payload.pitch() <= 90.0F;
	}

	public record Sample(
		float pitch,
		float yaw,
		long sequence,
		long receivedServerTick,
		int selectedSlot,
		ResourceKey<Level> dimension
	) {
	}
}
