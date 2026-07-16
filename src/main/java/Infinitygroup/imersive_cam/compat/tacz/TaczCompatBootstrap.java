package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.api.event.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public final class TaczCompatBootstrap {
	private static final String TACZ_MOD_ID = "tacz";
	private static ITaczClientCompat clientCompat = new NoopTaczClientCompat();
	private static boolean forgeEventsRegistered;

	private TaczCompatBootstrap() {
	}

	public static void registerIfPresent(IEventBus eventBus) {
		if (!ModList.get().isLoaded(TACZ_MOD_ID)) {
			ImersiveCamCommon.LOGGER.debug("TaCZ not detected; optional aim compatibility disabled");
			return;
		}
		clientCompat = new TaczClientCompat();
		TaczAimStateEventHandler.register(eventBus);
		eventBus.register(-5500, TaczShoulderCameraOffsetHandler.INSTANCE);
		eventBus.register(TaczAimSyncState.INSTANCE);
		if (!forgeEventsRegistered) {
			NeoForge.EVENT_BUS.register(TaczShootAlignmentHandler.INSTANCE);
			NeoForge.EVENT_BUS.register(TaczServerAimState.INSTANCE);
			NeoForge.EVENT_BUS.register(TaczServerFireAlignmentHandler.INSTANCE);
			forgeEventsRegistered = true;
		}
		ImersiveCamCommon.LOGGER.info("TaCZ detected; ADS, crosshair and shot alignment enabled");
	}

	public static ITaczClientCompat getClientCompat() {
		return clientCompat;
	}
}
