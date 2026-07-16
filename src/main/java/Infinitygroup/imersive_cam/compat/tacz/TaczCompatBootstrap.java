package Infinitygroup.imersive_cam.compat.tacz;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.api.event.IEventBus;
import net.neoforged.fml.ModList;

public final class TaczCompatBootstrap {
	private static final String TACZ_MOD_ID = "tacz";
	private static ITaczClientCompat clientCompat = new NoopTaczClientCompat();

	private TaczCompatBootstrap() {
	}

	public static void registerIfPresent(IEventBus eventBus) {
		if (!ModList.get().isLoaded(TACZ_MOD_ID)) {
			ImersiveCamCommon.LOGGER.debug("TaCZ not detected; optional aim compatibility disabled");
			return;
		}
		clientCompat = new TaczClientCompat();
		TaczAimStateEventHandler.register(eventBus);
		ImersiveCamCommon.LOGGER.info("TaCZ detected; ADS compatibility enabled");
	}

	public static ITaczClientCompat getClientCompat() {
		return clientCompat;
	}
}
