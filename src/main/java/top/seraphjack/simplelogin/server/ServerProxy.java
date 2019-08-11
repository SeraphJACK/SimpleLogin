package top.seraphjack.simplelogin.server;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.CommonProxy;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        new CapabilityLoader();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) throws RuntimeException {
        super.serverStarting(e);

        e.registerServerCommand(new SLCommand());

        // Start player login handler
        PlayerLoginHandler.instance();

        SLStorage.initialize(SLConfig.server.storageMethod);
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent e) throws IOException {
        super.serverStopping(e);

        PlayerLoginHandler.instance().stop();

        SimpleLogin.logger.info("Saving all entries...");
        SLStorage.storageProvider.save();
    }

}
