package top.seraphjack.simplelogin.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public final class ServerLoader {

    public static void serverSetup(@SuppressWarnings("unused") FMLDedicatedServerSetupEvent event) {
        // NO-OP
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent e) throws RuntimeException {
        SLStorage.initialize(SLConfig.SERVER.storageProvider.get());

        PlayerLoginHandler.initLoginHandler(SLConfig.SERVER.plugins.get().stream().map(ResourceLocation::new));
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent e) throws IOException {
        PlayerLoginHandler.instance().stop();

        SimpleLogin.logger.info("Saving all entries");
        SLStorage.instance().storageProvider.save();
    }
}
