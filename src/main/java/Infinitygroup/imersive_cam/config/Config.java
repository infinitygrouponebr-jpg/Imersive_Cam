package Infinitygroup.imersive_cam.config;

import Infinitygroup.imersive_cam.api.client.Perspective;
import Infinitygroup.imersive_cam.api.config.IClientConfig;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static Infinitygroup.imersive_cam.ImersiveCamCommon.MOD_ID;

public class Config {
	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	static {
		Pair<ClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}
	
	public static class ClientConfig implements IClientConfig {
		private static final int CURRENT_DEFAULT_PROFILE_VERSION = 1;
		private static final String MIGRATION_SECTION = "[migration]";
		private static final String DEFAULT_PROFILE_VERSION_KEY = "default_profile_version";
		private static final boolean CLIENT_CONFIG_FILE_WAS_PRESENT_ON_DISK = Files.isRegularFile(clientConfigPath());
		private static final boolean DEFAULT_PROFILE_VERSION_WAS_PRESENT_ON_DISK = defaultProfileVersionExistsOnDisk();

		private final IntValue defaultProfileVersion;
		private final CameraConfig cameraConfig;
		private final PerspectiveConfig perspectiveConfig;
		private final PlayerConfig playerConfig;
		private final ObjectPickerConfig objectPickerConfig;
		private final CrosshairConfig crosshairConfig;
		private final AudioConfig audioConfig;
		private final IntegrationsConfig integrationsConfig;
		private boolean defaultProfileMigrationHandled;
		private boolean requiresSaving = false;
		
		public ClientConfig(ModConfigSpec.Builder builder) {
			builder.push("migration");
			this.defaultProfileVersion = builder
				.comment("Internal version of the default Immersive Cam profile applied to existing client configs.")
				.translation(MOD_ID + ".configuration.migration.default_profile_version")
				.defineInRange(DEFAULT_PROFILE_VERSION_KEY, CURRENT_DEFAULT_PROFILE_VERSION, 0, Integer.MAX_VALUE);
			builder.pop();

			this.audioConfig = new AudioConfig(builder);
			this.cameraConfig = new CameraConfig(builder);
			this.crosshairConfig = new CrosshairConfig(builder);
			this.integrationsConfig = new IntegrationsConfig(builder);
			this.objectPickerConfig = new ObjectPickerConfig(builder);
			this.perspectiveConfig = new PerspectiveConfig(builder);
			this.playerConfig = new PlayerConfig(builder);
		}
		
		@Override
		public CameraConfig getCameraConfig() {
			return this.cameraConfig;
		}
		
		@Override
		public PerspectiveConfig getPerspectiveConfig() {
			return this.perspectiveConfig;
		}
		
		@Override
		public PlayerConfig getPlayerConfig() {
			return this.playerConfig;
		}
		
		@Override
		public ObjectPickerConfig getObjectPickerConfig() {
			return this.objectPickerConfig;
		}
		
		@Override
		public CrosshairConfig getCrosshairConfig() {
			return this.crosshairConfig;
		}
		
		@Override
		public AudioConfig getAudioConfig() {
			return this.audioConfig;
		}
		
		@Override
		public IntegrationsConfig getIntegrationsConfig() {
			return this.integrationsConfig;
		}
		
		public boolean requiresSaving() {
			return this.requiresSaving;
		}
		
		public void save() {
			try {
				Config.CLIENT_SPEC.save();
				this.requiresSaving = false;
			} catch (Exception e) {
				// ignore
			}
		}
		
		protected <T> void set(ConfigValue<T> configValue, T value) {
			if (value != null && !value.equals(configValue.get())) {
				configValue.set(value);
				this.requiresSaving = true;
			}
		}

		public boolean migrateToCurrentDefaultProfile() {
			if (this.defaultProfileMigrationHandled) {
				return false;
			}
			int storedVersion = !CLIENT_CONFIG_FILE_WAS_PRESENT_ON_DISK || DEFAULT_PROFILE_VERSION_WAS_PRESENT_ON_DISK
				? this.defaultProfileVersion.get()
				: 0;
			this.defaultProfileMigrationHandled = true;
			if (storedVersion >= CURRENT_DEFAULT_PROFILE_VERSION) {
				return false;
			}
			this.applyApprovedIntellijProfile();
			this.set(this.defaultProfileVersion, CURRENT_DEFAULT_PROFILE_VERSION);
			return true;
		}

		private void applyApprovedIntellijProfile() {
			this.audioConfig.applyApprovedProfile();
			this.cameraConfig.applyApprovedProfile();
			this.crosshairConfig.applyApprovedProfile();
			this.integrationsConfig.applyApprovedProfile();
			this.objectPickerConfig.applyApprovedProfile();
			this.perspectiveConfig.applyApprovedProfile();
			this.playerConfig.applyApprovedProfile();
		}

		private static Path clientConfigPath() {
			return FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml");
		}

		private static boolean defaultProfileVersionExistsOnDisk() {
			Path configPath = clientConfigPath();
			if (!Files.isRegularFile(configPath)) {
				return false;
			}
			boolean inMigrationSection = false;
			try {
				for (String rawLine : Files.readAllLines(configPath)) {
					String line = rawLine.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					if (line.startsWith("[") && line.endsWith("]")) {
						inMigrationSection = MIGRATION_SECTION.equals(line);
						continue;
					}
					if (inMigrationSection && line.startsWith(DEFAULT_PROFILE_VERSION_KEY) && line.contains("=")) {
						return true;
					}
				}
			} catch (IOException e) {
				return false;
			}
			return false;
		}
		
		protected static boolean isValidDouble(Object number) {
			if (number != null) {
				try {
					Double.parseDouble(number.toString());
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		
		protected static boolean isValidItemWithSlot(Object id) {
			if (id == null) {
				return false;
			}
			String[] split = id.toString().split("@", 2);
			if (split.length < 2) {
				return false;
			}
			return ResourceLocation.isValidNamespace(split[0]) && split[1] != null;
		}
		
		protected static boolean isValidItemPropertyWithSlot(Object id) {
			if (id == null) {
				return false;
			}
			String[] split = id.toString().split("@", 2);
			if (split.length < 2) {
				return false;
			}
			return ResourceLocation.isValidNamespace(split[0]) && ResourceLocation.tryParse(split[1]) != null;
		}
	}
	
	public static void onConfigReload() {
		boolean profileMigrated = Config.CLIENT.migrateToCurrentDefaultProfile();
		boolean cameraPositionCorrected = Config.CLIENT.getCameraConfig().enforcePermanentCameraPosition();
		Perspective currentPerspective = Perspective.current();
		PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
		ImersiveCam instance = ImersiveCam.getInstance();
		if (!currentPerspective.isEnabled(perspectiveConfig) && (currentPerspective != Perspective.FIRST_PERSON || !instance.isTemporaryFirstPerson())) {
			instance.changePerspective(currentPerspective.next(perspectiveConfig));
		}
		if (perspectiveConfig.isPerspectivePersistent()) {
			perspectiveConfig.setDefaultPerspective(Perspective.current());
		}
		if (profileMigrated || cameraPositionCorrected) {
			Config.CLIENT.save();
		}
	}
}
