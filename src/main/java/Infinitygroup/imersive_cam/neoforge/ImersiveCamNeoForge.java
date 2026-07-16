package Infinitygroup.imersive_cam.neoforge;

import Infinitygroup.imersive_cam.ImersiveCamCommon;
import Infinitygroup.imersive_cam.client.InputHandler;
import Infinitygroup.imersive_cam.client.ImersiveCam;
import Infinitygroup.imersive_cam.compat.tacz.TaczAimDirectionPayload;
import Infinitygroup.imersive_cam.config.Config;
import Infinitygroup.imersive_cam.neoforge.event.ClientEventHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = ImersiveCamCommon.MOD_ID, dist = Dist.CLIENT)
public class ImersiveCamNeoForge {
	public ImersiveCamNeoForge(ModContainer modContainer, IEventBus modEventBus) {
		modEventBus.addListener(TaczAimDirectionPayload::register);
		if (FMLEnvironment.dist.isClient()) {
			modEventBus.addListener(this::clientSetup);
			modEventBus.addListener(this::registerKeyMappingsEvent);
			modEventBus.addListener(this::modConfigLoadingEvent);
			modEventBus.addListener(this::modConfigReloadingEvent);
			modEventBus.addListener(ClientEventHandler::registerGuiOverlaysEvent);
			modContainer.registerConfig(Type.CLIENT, Config.CLIENT_SPEC);
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event) {
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGuiOverlayEvent);
		NeoForge.EVENT_BUS.addListener(ClientEventHandler::renderLevelStageEvent);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ClientEventHandler::movementInputUpdateEvent);
	}
	
	@SubscribeEvent
	public void modConfigLoadingEvent(ModConfigEvent.Loading event) {
		ImersiveCam.getInstance().init();
	}
	
	@SubscribeEvent
	public void modConfigReloadingEvent(ModConfigEvent.Reloading event) {
		if (ImersiveCamCommon.MOD_ID.equals(event.getConfig().getModId()) && event.getConfig().getType() == Type.CLIENT) {
			Config.onConfigReload();
		}
	}
	
	@SubscribeEvent
	public void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
		event.register(InputHandler.TOGGLE_FIRST_PERSON);
		event.register(InputHandler.TOGGLE_THIRD_PERSON_FRONT);
		event.register(InputHandler.TOGGLE_THIRD_PERSON_BACK);
		event.register(InputHandler.FREE_LOOK);
		event.register(InputHandler.TOGGLE_CAMERA_COUPLING);
		event.register(InputHandler.ENTER_FIRST_PERSON);
		event.register(InputHandler.ENTER_THIRD_PERSON_FRONT);
		event.register(InputHandler.ENTER_THIRD_PERSON_BACK);
		event.register(InputHandler.ENTER_IMERSIVE_CAMERA);
	}
}
