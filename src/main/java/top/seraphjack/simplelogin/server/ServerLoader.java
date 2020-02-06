package top.seraphjack.simplelogin.server;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public final class ServerLoader {

    public static void serverSetup(FMLDedicatedServerSetupEvent event) {
        CapabilityLoader.registerCapabilities();
    }

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent e) throws RuntimeException {
        // e.registerServerCommand(new SLCommand()); TODO
        SLConstants.server = e.getServer();
        // Start player login handler
        PlayerLoginHandler.instance();

        SLStorage.initialize(SLConfig.server.storageMethod);
    }

    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent e) throws IOException {
        PlayerLoginHandler.instance().stop();

        SimpleLogin.logger.info("Saving all entries");
        SLStorage.instance().storageProvider.save();
    }
}
