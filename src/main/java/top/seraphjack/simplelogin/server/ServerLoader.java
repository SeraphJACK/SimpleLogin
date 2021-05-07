package top.seraphjack.simplelogin.server;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public final class ServerLoader {

    public static void serverSetup(@SuppressWarnings("unused") FMLDedicatedServerSetupEvent event) {
        CapabilityLoader.registerCapabilities();
    }

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent e) throws RuntimeException {
        // Start player login handler
        PlayerLoginHandler.initLoginHandler(SLConfig.SERVER.plugins.get().stream().map(ResourceLocation::new)
                .collect(Collectors.toList()));

        SLStorage.initialize(SLConfig.SERVER.storageMethod.get(), e.getServer().func_240776_a_(SLConstants.SL_ENTRY));
    }

    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent e) throws IOException {
        PlayerLoginHandler.instance().stop();

        SimpleLogin.logger.info("Saving all entries");
        SLStorage.instance().storageProvider.save();
    }
}
