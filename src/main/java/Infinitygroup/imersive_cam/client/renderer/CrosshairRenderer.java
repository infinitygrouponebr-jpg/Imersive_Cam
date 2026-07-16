package Infinitygroup.imersive_cam.client.renderer;

import Infinitygroup.imersive_cam.api.client.CrosshairType;
import Infinitygroup.imersive_cam.api.client.Perspective;
import Infinitygroup.imersive_cam.api.client.renderer.CrosshairTargetSnapshot;
import Infinitygroup.imersive_cam.api.client.renderer.ICrosshairRenderer;
import Infinitygroup.imersive_cam.api.client.world.phys.PickContext;
import Infinitygroup.imersive_cam.api.math.Vec2f;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.config.Config;
import Infinitygroup.imersive_cam.config.CrosshairConfig;
import Infinitygroup.imersive_cam.config.ObjectPickerConfig;
import Infinitygroup.imersive_cam.mixin.GuiAccessor;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static Infinitygroup.imersive_cam.ImersiveCamCommon.MOD_ID;

public class CrosshairRenderer implements ICrosshairRenderer {
	private static final double OBSTRUCTION_DISTANCE_EPSILON = 1.0E-3D;
	private static final double MIN_CROSSHAIR_TARGET_DISTANCE_SQR = 1.0E-6D;
	private static final long MAX_CROSSHAIR_TARGET_AGE_TICKS = 2L;
	private static final ResourceLocation OBSTRUCTION_INDICATOR_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/hud/obstruction_indicator.png");
	private static final ItemStack OBSTRUCTED_BARRIER_ICON = new ItemStack(Items.BARRIER);

	private final ImersiveCam instance;
	private Vec2f crosshairOffset;
	private boolean isCrosshairDynamic;
	private boolean isCrosshairVisible;
	private boolean isObstructionCrosshairVisible;
	private boolean isObstructionIndicatorVisible;
	private boolean hasTrueBlockObstruction;
	@Nullable
	private Vec3 crosshairWorldTarget;
	private long crosshairWorldTargetGameTime;
	@Nullable
	private ResourceKey<Level> crosshairWorldTargetDimension;

	public CrosshairRenderer(ImersiveCam instance) {
		this.instance = instance;
		this.init();
	}

	private void init() {
		this.crosshairOffset = null;
		this.isCrosshairDynamic = false;
		this.isCrosshairVisible = true;
		this.isObstructionCrosshairVisible = false;
		this.isObstructionIndicatorVisible = false;
		this.hasTrueBlockObstruction = false;
		this.clearCrosshairWorldTarget();
	}

	public void renderTick(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick) {
		this.hasTrueBlockObstruction = false;
		Minecraft minecraft = Minecraft.getInstance();
		if (this.instance.isImersiveCam()) {
			Entity cameraEntity = minecraft.getCameraEntity();
			this.isCrosshairDynamic = computeIsCrosshairDynamic(cameraEntity, this.instance.isAiming());
			if (minecraft.level != null && minecraft.player != null) {
				this.updateDynamicRaytrace(camera, modelViewMatrix, projectionMatrix, partialTick);
			} else {
				this.crosshairOffset = null;
				this.clearCrosshairWorldTarget();
			}
		} else {
			this.clearCrosshairWorldTarget();
		}
		this.isCrosshairVisible = computeIsCrosshairVisible(this.crosshairOffset, this.isCrosshairDynamic, this.instance.isAiming());
		if (this.instance.isImersiveCam()) {
			this.isObstructionIndicatorVisible = computeIsObstructionIndicatorVisible(
				this.crosshairOffset, this.isCrosshairDynamic, this.instance.isAiming(), this.hasTrueBlockObstruction
			);
			this.isObstructionCrosshairVisible = computeIsObstructionCrosshairVisible(
				this.instance.isAiming(), this.isObstructionIndicatorVisible
			);
		} else {
			this.isObstructionIndicatorVisible = false;
			this.isObstructionCrosshairVisible = false;
		}
	}

	public void preRenderCrosshair(GuiGraphics guiGraphics) {
		if (this.isCrosshairDynamic || this.isObstructionCrosshairVisible) {
			this.setupPoseStack(guiGraphics.pose());
		}
	}

	public void postRenderCrosshair(GuiGraphics guiGraphics) {
		if (this.isCrosshairDynamic || this.isObstructionCrosshairVisible) {
			this.resetPoseStack(guiGraphics.pose());
		}
		if (this.isObstructionCrosshairVisible) {
			this.renderObstructionCrosshair(guiGraphics);
		} else if (this.isObstructionIndicatorVisible) {
			this.setupPoseStack(guiGraphics.pose());
			this.renderObstructionIndicator(guiGraphics);
			this.resetPoseStack(guiGraphics.pose());
		}
	}

	private void setupPoseStack(PoseStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.pushPose();
			poseStack.last().pose().translate(this.crosshairOffset.x(), -this.crosshairOffset.y(), 0F);
		}
	}

	private void resetPoseStack(PoseStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.popPose();
		}
	}

	private void renderObstructionCrosshair(GuiGraphics guiGraphics) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.gameMode == null) {
			return;
		}
		if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !((GuiAccessor) minecraft.gui).invokeCanRenderCrosshairForSpectator(minecraft.hitResult)) {
			return;
		}
		int iconSize = 16;
		int x = (guiGraphics.guiWidth() - iconSize) / 2;
		int y = (guiGraphics.guiHeight() - iconSize) / 2;
		guiGraphics.renderItem(OBSTRUCTED_BARRIER_ICON, x, y);
	}

	private void renderObstructionIndicator(GuiGraphics guiGraphics) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTION_INDICATOR_SPRITE);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}

	private void renderCustomCrosshair(GuiGraphics guiGraphics, ResourceLocation sprite) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || ((GuiAccessor) minecraft.gui).invokeCanRenderCrosshairForSpectator(minecraft.hitResult)) {
			guiGraphics.blit(sprite, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 0, 0, 15, 15, 15, 15);
		}
	}

	private void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick) {
		this.hasTrueBlockObstruction = false;
		ObjectPickerConfig objectPickerConfig = Config.CLIENT.getObjectPickerConfig();
		double interactionRangeOverride = objectPickerConfig.isCustomRaytraceDistanceEnabled()
			? objectPickerConfig.getCustomRaytraceDistance()
			: 0;
		Player player = Minecraft.getInstance().player;
		if (player == null || player.level() == null) {
			this.crosshairOffset = null;
			this.clearCrosshairWorldTarget();
			return;
		}
		// Trace primary crosshair
		PickContext.Builder pickContextBuilder = new PickContext.Builder(camera);
		if (this.isCrosshairDynamic) {
			pickContextBuilder.dynamicTrace();
		}
		PickContext primaryContext = pickContextBuilder.build();
		HitResult primaryHitResult = this.instance.getObjectPicker().pick(primaryContext, interactionRangeOverride, partialTick, player);
		Vec3 primaryTargetPosition = primaryHitResult.getLocation();
		Vec3 projectedPosition = primaryTargetPosition;
		// Trace obstruction crosshair
		if (!this.isCrosshairDynamic && primaryHitResult.getType() != HitResult.Type.MISS) {
			Vec3 eyePosition = player.getEyePosition(partialTick);
			PickContext obstructionContext = pickContextBuilder.obstructionTrace(primaryTargetPosition).build();
			BlockHitResult obstructionBlockHit = this.instance.getObjectPicker().pickBlocks(
				obstructionContext, eyePosition.distanceTo(primaryTargetPosition), partialTick
			);
			if (isTrueBlockObstruction(primaryHitResult, obstructionBlockHit, eyePosition, primaryTargetPosition)) {
				this.hasTrueBlockObstruction = true;
				projectedPosition = obstructionBlockHit.getLocation();
			}
		}
		if (!isFinite(projectedPosition) || projectedPosition.distanceToSqr(player.getEyePosition(partialTick)) <= MIN_CROSSHAIR_TARGET_DISTANCE_SQR) {
			this.crosshairOffset = null;
			this.clearCrosshairWorldTarget();
			return;
		}
		Vec2f projected = project2D(projectedPosition.subtract(camera.getPosition()), modelViewMatrix, projectionMatrix);
		Vec2f crosshairOffset = null;
		if (projected != null) {
			Window window = Minecraft.getInstance().getWindow();
			Vec2f screenSize = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f center = screenSize.divide(2);
			CrosshairConfig crosshairConfig = Config.CLIENT.getCrosshairConfig();
			double maxDistanceToObstruction = crosshairConfig.getObstructionIndicatorMaxDistanceToObstruction();
			if (this.isCrosshairDynamic || !crosshairConfig.isObstructionIndicatorEnabled() || maxDistanceToObstruction <= 0 || projectedPosition.distanceToSqr(player.getEyePosition()) <= maxDistanceToObstruction * maxDistanceToObstruction) {
				crosshairOffset = projected.subtract(center).divide((float) window.getGuiScale());
			}
		}
		this.crosshairOffset = crosshairOffset;
		if (crosshairOffset != null) {
			this.crosshairWorldTarget = new Vec3(projectedPosition.x(), projectedPosition.y(), projectedPosition.z());
			this.crosshairWorldTargetGameTime = player.level().getGameTime();
			this.crosshairWorldTargetDimension = player.level().dimension();
		} else {
			this.clearCrosshairWorldTarget();
		}
	}

	public void resetState() {
		this.init();
	}

	private static boolean computeIsCrosshairDynamic(@Nullable Entity cameraEntity, boolean isAiming) {
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
			case CrosshairType.ADAPTIVE -> isAiming;
			case CrosshairType.DYNAMIC,
			     CrosshairType.DYNAMIC_WITH_1PP -> cameraEntity instanceof Player player && !player.isScoping();
			default -> false;
		};
	}

	private static boolean computeIsCrosshairVisible(@Nullable Vec2f crosshairOffset, boolean isCrosshairDynamic, boolean isAiming) {
		if (crosshairOffset == null && isCrosshairDynamic) {
			return false;
		}
		HitResult hitResult = Minecraft.getInstance().hitResult;
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairVisibility(Perspective.current())) {
			case NEVER -> false;
			case WHEN_AIMING -> isAiming;
			case WHEN_IN_RANGE -> hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			case WHEN_AIMING_OR_IN_RANGE -> isAiming || hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			default -> true;
		};
	}

	private static boolean computeIsObstructionIndicatorVisible(@Nullable Vec2f crosshairOffset, boolean isCrosshairDynamic, boolean isAiming, boolean hasTrueBlockObstruction) {
		if (!hasTrueBlockObstruction) {
			return false;
		}
		if (crosshairOffset == null || !Config.CLIENT.getCrosshairConfig().isObstructionIndicatorEnabled()) {
			return false;
		}
		if (isCrosshairDynamic) {
			return false;
		}
		if (!isAiming && Config.CLIENT.getCrosshairConfig().isObstructionIndicatorOnlyShownWhenAiming()) {
			return false;
		}
		int minDistanceToCrosshair = Config.CLIENT.getCrosshairConfig().getObstructionIndicatorMinDistanceToCrosshair();
		return crosshairOffset.lengthSquared() >= minDistanceToCrosshair * minDistanceToCrosshair;
	}

	private static boolean isTrueBlockObstruction(
		HitResult primaryHitResult,
		BlockHitResult obstructionHitResult,
		Vec3 eyePosition,
		Vec3 targetPosition
	) {
		if (obstructionHitResult.getType() != HitResult.Type.BLOCK) {
			return false;
		}
		if (primaryHitResult instanceof BlockHitResult primaryBlockHit && primaryBlockHit.getBlockPos().equals(obstructionHitResult.getBlockPos())) {
			return false;
		}
		double targetDistance = eyePosition.distanceTo(targetPosition);
		double obstructionDistance = eyePosition.distanceTo(obstructionHitResult.getLocation());
		return obstructionDistance + OBSTRUCTION_DISTANCE_EPSILON < targetDistance;
	}

	private static boolean computeIsObstructionCrosshairVisible(boolean isAiming, boolean isObstructionIndicatorVisible) {
		return isAiming && isObstructionIndicatorVisible;
	}

	@Override
	public boolean isCrosshairDynamic() {
		return this.isCrosshairDynamic;
	}

	@Override
	public boolean isCrosshairVisible() {
		return this.isCrosshairVisible;
	}

	@Override
	public boolean isObstructionCrosshairVisible() {
		return this.isObstructionCrosshairVisible;
	}

	@Override
	public boolean isObstructionIndicatorVisible() {
		return this.isObstructionIndicatorVisible;
	}

	@Override
	public @Nullable Vec2f getCrosshairOffset() {
		return this.crosshairOffset;
	}

	@Override
	public @Nullable CrosshairTargetSnapshot getCrosshairTargetSnapshot() {
		if (this.crosshairWorldTarget == null || this.crosshairWorldTargetDimension == null || !isFinite(this.crosshairWorldTarget)) {
			return null;
		}
		return new CrosshairTargetSnapshot(
			new Vec3(this.crosshairWorldTarget.x(), this.crosshairWorldTarget.y(), this.crosshairWorldTarget.z()),
			this.crosshairWorldTargetDimension,
			this.crosshairWorldTargetGameTime
		);
	}

	public boolean isCrosshairTargetFresh(@Nullable CrosshairTargetSnapshot snapshot, Level level) {
		if (snapshot == null) {
			return false;
		}
		if (!snapshot.dimension().equals(level.dimension())) {
			return false;
		}
		long age = level.getGameTime() - snapshot.gameTime();
		return age >= 0L
			&& age <= MAX_CROSSHAIR_TARGET_AGE_TICKS
			&& isFinite(snapshot.position());
	}

	private void clearCrosshairWorldTarget() {
		this.crosshairWorldTarget = null;
		this.crosshairWorldTargetGameTime = Long.MIN_VALUE;
		this.crosshairWorldTargetDimension = null;
	}

	public static boolean isFinite(Vec3 position) {
		return Double.isFinite(position.x())
			&& Double.isFinite(position.y())
			&& Double.isFinite(position.z());
	}

	private static @Nullable Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection) {
		Window window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getScreenWidth();
		int screenHeight = window.getScreenHeight();
		if (screenWidth == 0 || screenHeight == 0) {
			return null;
		}
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.mul(modelView);
		vec.mul(projection);
		if (vec.w() == 0.0F) {
			return null;
		}
		float w = (1.0F / vec.w()) * 0.5F;
		float x = (vec.x() * w + 0.5F) * screenWidth;
		float y = (vec.y() * w + 0.5F) * screenHeight;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		if (Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y) || w < 0.0F) {
			return null;
		}
		return new Vec2f(x, y);
	}
}
