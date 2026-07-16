package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.api.client.event.TickEvent;
import Infinitygroup.imersive_cam.api.client.event.handler.TickEventHandler;
import Infinitygroup.imersive_cam.api.client.renderer.CrosshairTargetSnapshot;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.config.Config;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public enum TaczAimSyncState implements TickEventHandler {
	INSTANCE;

	public static final int DEFAULT_SYNC_HOLD_TICKS = 10;
	private static final float MIN_DIRECTION_DELTA_DEGREES = 0.01F;

	@Nullable
	private CrosshairTargetSnapshot lockedTarget;
	private int remainingSyncTicks;
	private long sequence;
	private boolean hasSentDirection;
	private float lastSentPitch;
	private float lastSentYaw;

	public void lockTarget(@Nullable CrosshairTargetSnapshot snapshot, int holdTicks) {
		if (snapshot != null) {
			this.lockedTarget = snapshot;
		}
		this.remainingSyncTicks = Math.max(this.remainingSyncTicks, holdTicks);
		this.hasSentDirection = false;
	}

	public void clear() {
		this.lockedTarget = null;
		this.remainingSyncTicks = 0;
		this.hasSentDirection = false;
	}

	public void syncImmediately(ImersiveCam imersiveCam) {
		this.sync(imersiveCam, true);
	}

	@Override
	public void handle(TickEvent event) {
		ImersiveCam imersiveCam = ImersiveCam.getInstance();
		boolean shouldAlign = TaczCompatBootstrap.getClientCompat().shouldAlignGunfireToCrosshair();
		if (!shouldAlign && this.remainingSyncTicks <= 0) {
			return;
		}
		this.sync(imersiveCam, false);
	}

	private void sync(ImersiveCam imersiveCam, boolean forceSend) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (minecraft.level == null || player == null || !player.isAlive() || player.isSpectator()) {
			this.clear();
			return;
		}
		if (!Config.CLIENT.getCrosshairConfig().isTaczShotAlignmentEnabled() || !IGun.mainHandHoldGun(player)) {
			this.clear();
			return;
		}
		CrosshairTargetSnapshot snapshot = this.selectFreshTarget(imersiveCam);
		boolean aligned = imersiveCam.alignPlayerToCrosshairSnapshot(snapshot);
		if (aligned) {
			this.lockedTarget = snapshot;
			this.sendDirectionIfNeeded(player, snapshot, forceSend);
		}
		if (this.remainingSyncTicks > 0) {
			this.remainingSyncTicks--;
		}
		if (!aligned && this.remainingSyncTicks <= 0) {
			this.clear();
		}
	}

	@Nullable
	private CrosshairTargetSnapshot selectFreshTarget(ImersiveCam imersiveCam) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return null;
		}
		CrosshairTargetSnapshot snapshot = imersiveCam.getCrosshairRenderer().getCrosshairTargetSnapshot();
		if (imersiveCam.getCrosshairRenderer().isCrosshairTargetFresh(snapshot, minecraft.level)) {
			return snapshot;
		}
		if (imersiveCam.getCrosshairRenderer().isCrosshairTargetFresh(this.lockedTarget, minecraft.level)) {
			return this.lockedTarget;
		}
		this.lockedTarget = null;
		return null;
	}

	private void sendDirectionIfNeeded(LocalPlayer player, @Nullable CrosshairTargetSnapshot snapshot, boolean forceSend) {
		if (snapshot == null || !canSendAimPayload()) {
			return;
		}
		AimDirection direction = computeAimDirection(player, snapshot.position());
		if (direction == null) {
			return;
		}
		float yawDelta = Math.abs(Mth.wrapDegrees(direction.yaw() - this.lastSentYaw));
		float pitchDelta = Math.abs(direction.pitch() - this.lastSentPitch);
		if (!forceSend && this.hasSentDirection && pitchDelta < MIN_DIRECTION_DELTA_DEGREES && yawDelta < MIN_DIRECTION_DELTA_DEGREES) {
			return;
		}
		this.lastSentPitch = direction.pitch();
		this.lastSentYaw = direction.yaw();
		this.hasSentDirection = true;
		PacketDistributor.sendToServer(new TaczAimDirectionPayload(
			direction.pitch(),
			direction.yaw(),
			++this.sequence,
			player.getInventory().selected
		));
	}

	private static boolean canSendAimPayload() {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		return connection instanceof ICommonPacketListener listener
			&& listener.hasChannel(TaczAimDirectionPayload.TYPE);
	}

	@Nullable
	private static AimDirection computeAimDirection(LocalPlayer player, Vec3 target) {
		Vec3 delta = target.subtract(player.getEyePosition());
		double horizontalDistance = Math.sqrt(delta.x() * delta.x() + delta.z() * delta.z());
		if (!Double.isFinite(horizontalDistance) || horizontalDistance <= 1.0E-6D) {
			return null;
		}
		float pitch = (float) (-(Mth.atan2(delta.y(), horizontalDistance) * Mth.RAD_TO_DEG));
		float yaw = Mth.wrapDegrees((float) (Mth.atan2(delta.z(), delta.x()) * Mth.RAD_TO_DEG) - 90.0F);
		if (!Float.isFinite(pitch) || !Float.isFinite(yaw) || pitch < -90.0F || pitch > 90.0F) {
			return null;
		}
		return new AimDirection(pitch, yaw);
	}

	private record AimDirection(float pitch, float yaw) {
	}
}
